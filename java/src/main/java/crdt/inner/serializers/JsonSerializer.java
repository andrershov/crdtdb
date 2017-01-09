package crdt.inner.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializer implements Serializer {
	
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public String serialize(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T deserialize(String str, Class<T> clazz) {
		try {
			return mapper.readValue(str, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
