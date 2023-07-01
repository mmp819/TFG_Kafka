package es.unican.martinm.kttmSpark.spark;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import com.datastax.driver.core.Session;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.unican.martinm.kttmSpark.cassandra.CassandraConnectionPool;
import es.unican.martinm.kttmSpark.domain.KttmKafka;

public class KttmMain {
	
	private static final String APP_NAME = "KttmSpark";
	private static final int STR_CONTEXT_DURATION = 1000;
	private static final int PUERTO_CASSANDRA = 9042;
	
	private static final int PARAMETROS_ESPERADOS = 4;
	private static final String REG_EXP_SERVIDORES = "^[a-zA-Z0-9._-]+(:[0-9]+)?$";
	private static final String REG_EXP_TOPICO = "^[a-zA-Z0-9._-]+$";
	

	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();

		// Comprueba numero de parametros
		if (args.length != PARAMETROS_ESPERADOS) {
			System.err.println("Error. Falta alguno de los siguientes par\u00e1metros:\n1- Servidores" +
				" bootstrap\n2- T\u00f3pico\n3- Fichero JSON");
					
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
		
		// Configurar consumidor Kafka
		Map<String, Object> kafkaConf = new HashMap<>();
		kafkaConf.put("bootstrap.servers", servidoresBootstrap);
		kafkaConf.put("key.deserializer", StringDeserializer.class);
		kafkaConf.put("value.deserializer", StringDeserializer.class);
		kafkaConf.put("group.id", "1");
		kafkaConf.put("auto.offset.reset", "earliest");
		kafkaConf.put("enable.auto.commit", false);
		
		// Obtener datos JSON de Kafka y convertir a objetos Java
		Collection<String> topicos = Arrays.asList(topico);
		
		JavaInputDStream<ConsumerRecord<String, String>> kafkaStream = 
				KafkaUtils.createDirectStream(jsc, LocationStrategies.PreferConsistent(),
						ConsumerStrategies.<String, String>Subscribe(topicos, kafkaConf));
		
		JavaDStream<String> streamJson = kafkaStream.map(ConsumerRecord::value);
		
		JavaDStream<KttmKafka> kttmStream = streamJson.map(record -> {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(record, KttmKafka.class);
		});
		

		// Almacenamiento en Cassandra
		kttmStream.foreachRDD(rdd -> {
			rdd.foreachPartition(partitionOfRecords -> {
				// ConnectionPool is a static, lazily initialized pool of connections
				CassandraConnectionPool pool = CassandraConnectionPool.getInstance();
				Session session = pool.getConnection();
				KttmKafka aux;
				StringBuilder insert;
				StringBuilder lang;
				//int filas = 0;
				
				while (partitionOfRecords.hasNext()) {
					lang = new StringBuilder();
					aux = partitionOfRecords.next();
					// Obtiene lenguas
					for (String l : aux.getLanguage()) {
						lang.append("'");
						lang.append(l);
						lang.append("',");
					}
					if (aux.getLanguage().size() > 0) {
						lang.deleteCharAt(lang.length() - 1);
					}
					
					// Trata campos con ' en el nombre
					String city = aux.getGeoIp().getCity();
					city = city.replace("'", "''");
					String region = aux.getGeoIp().getRegion();
					region = region.replace("'", "''");
					String eventData = aux.getEvent().getData();
					if (eventData != null) {
						eventData = eventData.replace("'", "''");
					}
					
					
					insert = new StringBuilder();
					insert.append("INSERT INTO kttm.kttm_kafka ")
					.append("(timestamp,session,number,event,agent,client_ip,geo_ip,language,")
					.append("adblock_list,app_version,path,loaded_image,referrer,referrer_host,")
					.append("server_ip,screen,window,session_length,timezone,timezone_offset)")
					.append(" values('")
					.append(aux.getTimestamp()).append("','")
					.append(aux.getSession()).append("',")
					.append(aux.getNumber()).append(",")
					.append("{type: '").append(aux.getEvent().getType()).append("',")
					.append("data: '").append(eventData).append("'},")
					.append("{type: '").append(aux.getAgent().getType()).append("',")
					.append("category: '").append(aux.getAgent().getCategory()).append("',")
					.append("browser: '").append(aux.getAgent().getBrowser()).append("',")
					.append("browser_version: '").append(aux.getAgent().getBrowser_version()).append("',")
					.append("os: '").append(aux.getAgent().getOs()).append("'},'")
					.append(aux.getClientIp()).append("',")
					.append("{continent: '").append(aux.getGeoIp().getContinent()).append("',")
					.append("country: '").append(aux.getGeoIp().getCountry()).append("',")
					.append("region: '").append(region).append("',")
					.append("city: '").append(city).append("'},")
					.append("{").append(lang.toString()).append("},'")
					.append(aux.getAdblockList()).append("','")
				    .append(aux.getAppVersion()).append("','")
					.append(aux.getPath()).append("','")
					.append(aux.getLoadedImage()).append("','")
					.append(aux.getReferrer()).append("','")
					.append(aux.getReferrerHost()).append("','")
					.append(aux.getServerIp()).append("','")
					.append(aux.getScreen()).append("','")
					.append(aux.getWindow()).append("',")
					.append(aux.getSessionLength()).append(",'")
					.append(aux.getTimezone()).append("','")
					.append(aux.getTimezoneOffset())
					.append("');");
					
					session.execute(insert.toString());
				}

				pool.returnConnection(session); 
			});

			long endTime = System.currentTimeMillis();
	        System.out.println("Tiempo de ingesta: " + (endTime - startTime) + " ms -- " + 
					((endTime - startTime) / 1000.0) + " s");
		});
				
		
		jsc.start();
		try {
			jsc.awaitTermination();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}   
		CassandraConnectionPool.cleanUp();

	}
}
