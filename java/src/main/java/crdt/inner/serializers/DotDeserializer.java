package crdt.inner.serializers;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import crdt.inner.causal.Dot;

import java.io.IOException;

public class DotDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String key, DeserializationContext arg1) throws IOException {
        String[] arr = key.split(",");
        return new Dot(arr[0], Integer.parseInt(arr[1]));
    }

}
