package es.unican.martinm.kttmSpark.serializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Clase que representa un deserializador para una lista de lenguajes obtenida de un fichero
 * JSON.
 * 
 * @author Mario Martin Perez <mmp819@alumnos.unican.es>
 * @version 1.0
 */
public class CustomLanguageDeserializerJSON extends JsonDeserializer<List<String>>{

	@Override
	public List<String> deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		JsonNode node = mapper.readTree(jp);
		List<String> lista = new ArrayList<>();
		if (node.isArray()) {
			for (JsonNode n : node) {
				lista.add(n.asText());
			}
		}
		
		return lista;
		
	}
}
