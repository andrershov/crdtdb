package crdt.inner.serializers;

public interface Serializer {
    String serialize(Object obj);

    <T> T deserialize(String str, Class<T> clazz);
}
