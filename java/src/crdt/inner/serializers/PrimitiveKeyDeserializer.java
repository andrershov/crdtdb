package crdt.inner.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class PrimitiveKeyDeserializer extends KeyDeserializer {

	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		if (key.equals("true") || key.equals("false")){
			return Boolean.parseBoolean(key);
		}
		//TODO add more classes
		return key;
	}

}
