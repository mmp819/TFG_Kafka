package es.unican.martinm.trainSpark.spark;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import com.datastax.driver.core.Session;

import es.unican.martinm.trainSpark.cassandra.CassandraConnectionPool;
import es.unican.martinm.trainSpark.domain.TrainRecord;

public class TrainMain {

	private static final String APP_NAME = "TrainSpark";
	private static final int STR_CONTEXT_DURATION = 1000;
	private static final int PUERTO_CASSANDRA = 9042;
	
	private static final int PARAMETROS_ESPERADOS = 4;
	private static final String REG_EXP_SERVIDORES = "^[a-zA-Z0-9._-]+(:[0-9]+)?$";
	private static final String REG_EXP_TOPICO = "^[a-zA-Z0-9._-]+$";
	
	private static final int LPS_ACTIVO = 1;
	private static final int GPS_PERDIDO = 0;
	
	private static final String TOPIC_FILTRADO = "lecturas_filtradas";
	private static final String TOPIC_ALERT_ANOMALO = "lecturas_anomalas_alert";
	private static final String TOPIC_ALERT_GPS = "lecturas_gps_alert";
	
	private static final String CASSANDRA_TABLA = "lecturas";
	
	public static void main(String[] args) {
		// Comprueba numero de parametros
		if (args.length != PARAMETROS_ESPERADOS) {
			System.err.println("Error. Falta alguno de los siguientes par\u00e1metros:\n1- Servidores" +
				" bootstrap\n2- T\u00f3pico\n3- Servidor de Cassandra.");
							
			System.exit(1);
		}
						
		String sparkMaster = args[0];
				
		// Spark utilizado en local sobre una maquina
		if (!sparkMaster.equals("local")) {
			System.err.println("Error. La ejecucion del programa est\u00e1 pensada para ser en local," +
					" sobre una sola m\u00e1quina. \n1- URL nodo master = local.");
								
			System.exit(1);
		}
				
		String servidoresBootstrap = args[1];
				
		// Comprueba formato de servidores bootstrap
		if (!servidoresBootstrap.matches(REG_EXP_SERVIDORES)) {
			System.err.println("Error. El nombre de los servidores bootstrap sigue un formato" +
				" incorrecto. Debe ser:\n1- <hostname>\n2- <ip>\n3- <hostanme>:<puerto>\n" +
				"4- <ip>:<puerto>");
							
			System.exit(1);
		}
						
		String topico = args[2];
				
		// Comprueba formato de topico
		if (!topico.matches(REG_EXP_TOPICO)) {
			System.err.println("Error. El nombre del topico sigue un formato incorrecto. Debe " +
				"contener \u00fanicamente: may\u00fasculas, min\u00fasculas, n\u00fameros," +
				"puntos, o guiones.");
							
			System.exit(1);
		}
						
		String servidorCassandra = args[3];
				
		// Comprueba formato servidor Cassandra
		if (!servidoresBootstrap.matches(REG_EXP_SERVIDORES)) {
			System.err.println("Error. El nombre del servidor Cassandra sigue un formato" +
				" incorrecto. Debe ser:\n1- <hostname>\n2- <ip>\n3- <hostanme>:<puerto>\n" +
				"4- <ip>:<puerto>");
							
			System.exit(1);
		}
		
		// Inicializar StreamingContext
		SparkConf spkConf = new SparkConf();
		spkConf.setAppName(APP_NAME);
		spkConf.setMaster(sparkMaster);
		spkConf.set("spark.cassandra.connection.host", servidorCassandra);

		JavaStreamingContext jsc = new JavaStreamingContext(spkConf, 
			new Duration(STR_CONTEXT_DURATION));
				
		CassandraConnectionPool.setUP(servidorCassandra, PUERTO_CASSANDRA);
		
		// Configurar consumidor y productor Kafka
		Map<String, Object> kafkaConf = new HashMap<String, Object>();

		kafkaConf.put("bootstrap.servers", servidoresBootstrap);
		kafkaConf.put("key.serializer", StringSerializer.class);
		kafkaConf.put("key.deserializer", StringDeserializer.class);
		kafkaConf.put("value.deserializer", StringDeserializer.class);
		kafkaConf.put("value.serializer", StringSerializer.class);
		kafkaConf.put("group.id", "1");
		kafkaConf.put("auto.offset.reset", "latest");
		kafkaConf.put("enable.auto.commit", true);
		// SSL
		kafkaConf.put("security.protocol", "SSL");
		kafkaConf.put("ssl.keystore.location", "/home/mario/Escritorio/TFG/CertificadosKafka/" + 
				"Clientes/Spark-Cassandra/sparkcliente.ks.p12");
		kafkaConf.put("ssl.keystore.password", "sparkcliente-ks-password");
		kafkaConf.put("ssl.key.password", "sparkcliente-ks-password");
		kafkaConf.put("ssl.truststore.location", "/home/mario/Escritorio/TFG/CertificadosKafka/" +
				"Clientes/clientes.ts.p12");
		kafkaConf.put("ssl.truststore.password", "clientes-ts-password");
		Collection<String> topics = Arrays.asList(topico);
				
		JavaInputDStream<ConsumerRecord<String, String>> kafkaStream = 
				KafkaUtils.createDirectStream(jsc, LocationStrategies.PreferConsistent(),
						    ConsumerStrategies.<String, String>Subscribe(topics, kafkaConf));
		
		
		// Convierte los registros de Kafka en registros del tipo TrainRecord	
		JavaDStream<TrainRecord> registros = kafkaStream.map(registro -> new TrainRecord(registro.value()));
		
		// Almacenamiento en Cassandra
		registros.foreachRDD(rdd -> {
			rdd.foreachPartition(partitionOfRecords -> {
				KafkaProducer<String, String> productor = new KafkaProducer<>(kafkaConf);

				CassandraConnectionPool pool = CassandraConnectionPool.getInstance();
				Session session = pool.getConnection();
				TrainRecord aux;
				ProducerRecord<String, String> record = null;
				
				while (partitionOfRecords.hasNext()) {
				    aux = partitionOfRecords.next();
				    
				    // Filtrar
				    String medidas = buildRow(aux);
				    String query;
				    
				    if (aux.getLps() != LPS_ACTIVO && aux.getGpsQuality() != GPS_PERDIDO) { // Lectura normal
				    	record = new ProducerRecord<>(TOPIC_FILTRADO, medidas);
					    productor.send(record);
				    } else { // Lecturas con alerta/s (1 lectura puede desencadenar varias alertas)
				    	
				    	// Caida de presion - Posibles fugas
				    	if (aux.getLps() == LPS_ACTIVO) {
				    		record = new ProducerRecord<>(TOPIC_ALERT_ANOMALO, medidas);
						    productor.send(record);
				    	}
				    	
				    	// Perdida GPS
				    	if (aux.getGpsQuality() == GPS_PERDIDO) {
				    		record = new ProducerRecord<>(TOPIC_ALERT_GPS, medidas);
						    productor.send(record);
				    	}
				    	
				    }
				    
				    query = buildCassandraQuery(CASSANDRA_TABLA, medidas);
				    session.execute(query);
				    
					productor.flush();
				}
				pool.returnConnection(session);
				productor.close();
			});
		});
				
		jsc.start();
		try {
			jsc.awaitTermination();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			CassandraConnectionPool.cleanUp();
		}
	}
	
	/**
	 * Construye una sentencia que permita insertar un conjunto de datos en una tabla concreta.
	 * 
	 * @param tabla Tabla destino.
	 * @param medidas Medidas de interes.
	 * @return sentencia construida.
	 */
	private static String buildCassandraQuery(String tabla, String medidas) {
		String[] aux = medidas.split(",", 2);
		String timestamp = aux[0];
		
		String medidasQuery = medidas.replaceFirst(".*?,", "'" + timestamp + "',");
		StringBuilder builder;
		builder = new StringBuilder();
		builder.append("INSERT INTO train.")
				.append(tabla)
		    	.append(" (timestamp,TP2,TP3,H1,DV_pressure,reservoirs,oil_temperature")
		    	.append(",flowmeter,motor_current,COMP,DV_electric,towers,MPG,LPS")
		    	.append(",pressure_switch,oil_level,caudal_impulses,gpsLong,gpsLat")
		    	.append(",gpsSpeed,gpsQuality)")
		    	.append(" values(")
		    	.append(medidasQuery)
		    	.append(");");
				/** Debugging*/
		    	System.out.println();
		    	System.out.println();
		    	System.out.println();
		    	System.out.println();
		    	System.out.println(builder.toString());
		    	System.out.println();
		    	System.out.println();
		    	System.out.println();
		    	System.out.println();
		return builder.toString();
	}
	
	/**
	 * Construye una fila con todos los datos correspondientes a una medida y separados por comas.
	 * 
	 * @param tr Registro con medidas.
	 * @return fila construida.
	 */
	private static String buildRow(TrainRecord tr) {
		StringBuilder builder;
		builder = new StringBuilder();
		builder.append(tr.getTimestamp()).append(",")
    	.append(tr.getTp2()).append(",")
    	.append(tr.getTp3()).append(",")
    	.append(tr.getH1()).append(",")
    	.append(tr.getDvPressure()).append(",")
    	.append(tr.getReservoirs()).append(",")
    	.append(tr.getOilTemperature()).append(",")
    	.append(tr.getFlowMeter()).append(",")
    	.append(tr.getMotorCurrent()).append(",")
    	.append(tr.getComp()).append(",")
    	.append(tr.getDvElectric()).append(",")
    	.append(tr.getTowers()).append(",")
    	.append(tr.getMpg()).append(",")
    	.append(tr.getLps()).append(",")
    	.append(tr.getPressureSwitch()).append(",")
    	.append(tr.getOilLevel()).append(",")
    	.append(tr.getCaudalImpulses()).append(",")
    	.append(tr.getGpsLong()).append(",")
    	.append(tr.getGpsLat()).append(",")
    	.append(tr.getGpsSpeed()).append(",")
    	.append(tr.getGpsQuality());
		
		return builder.toString();
	}
}
