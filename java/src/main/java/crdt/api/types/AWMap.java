package crdt.api.types;

import crdt.api.Crdt;

public interface AWMap<K> extends Crdt {
    <V extends Crdt> V get(K key);

    <V extends Crdt> void put(K key, V value);

    void remove(K key);
}
