package crdt.inner.serializers;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

public class PrimitiveKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        if (key.equals("true") || key.equals("false")) {
            return Boolean.parseBoolean(key);
        }
        //TODO add more classes
        return key;
    }

}
