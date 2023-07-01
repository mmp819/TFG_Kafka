package es.unican.martinm.producer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {

	private static final int PARAMETROS_ESPERADOS = 3;
	private static final String REG_EXP_SERVIDORES = "^[a-zA-Z0-9._-]+(:[0-9]+)?$";
	private static final String REG_EXP_TOPICO = "^[a-zA-Z0-9._-]+$";
	
	public static void main(String[] args) {
		
		// Comprueba numero de parametros
		if (args.length != PARAMETROS_ESPERADOS) {
			System.err.println("Error. Falta alguno de los siguientes par\u00e1metros:\n1- Servidores" +
					" bootstrap\n2- T\u00f3pico\n3- Fichero");
			
			System.exit(1);
		}
		
		String servidoresBootstrap = args[0];
		
		// Comprueba formato de servidores bootstrap
		if (!servidoresBootstrap.matches(REG_EXP_SERVIDORES)) {
			System.err.println("Error. El nombre de los servidores bootstrap sigue un formato" +
					" incorrecto. Debe ser:\n1- <hostname>\n2- <ip>\n3- <hostanme>:<puerto>\n" +
					"4- <ip>:<puerto>");
			
			System.exit(1);
		}
			
		String topico = args[1];
		
		// Comprueba formato de topico
		if (!topico.matches(REG_EXP_TOPICO)) {
			System.err.println("Error. El nombre del topico sigue un formato incorrecto. Debe " +
					"contener \u00fanicamente: may\u00fasculas, min\u00fasculas, n\u00fameros," +
					"puntos, o guiones.");
			
			System.exit(1);
		}
		
		String fichero = args[2];
		
		// Comprueba que la ruta es correcta
		if (!Files.exists(Paths.get(fichero))) {
			System.err.println("Error. No existe el archivo indicado o la ruta es incorrecta.");
			
			System.exit(1);
		}
		
		// Configurar productor
		Properties propiedades = new Properties();
		propiedades.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servidoresBootstrap);
		propiedades.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		propiedades.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());	
		// SSL
		propiedades.setProperty("security.protocol", "SSL");
		propiedades.setProperty("ssl.keystore.location", "/home/mario/Escritorio/TFG/CertificadosKafka/" + 
				"Clientes/Productor/productor.ks.p12");
		propiedades.setProperty("ssl.keystore.password", "productor-ks-password");
		propiedades.setProperty("ssl.key.password", "productor-ks-password");
		propiedades.setProperty("ssl.truststore.location", "/home/mario/Escritorio/TFG/CertificadosKafka/" +
				"Clientes/clientes.ts.p12");
		propiedades.setProperty("ssl.truststore.password", "clientes-ts-password");
		
		// Productor Kafka
		KafkaProducer<String, String> productor = new KafkaProducer<>(propiedades);
		
		// Envio
		try (BufferedReader reader = new BufferedReader(new FileReader(fichero))) {
			String string;
			while ((string = reader.readLine()) != null) {
				System.out.println(string);
				ProducerRecord<String, String> record = new ProducerRecord<>(topico, string);
				productor.send(record);
				productor.flush();
			}
		} catch (IOException e) {
			System.err.println("Error abriendo fichero.");
			e.printStackTrace();
		} finally {
			productor.close(); // Cerrar incluso con excepcion
		}
	}
}
