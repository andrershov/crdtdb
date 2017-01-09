package crdt.api.types;

import crdt.api.Crdt;

import java.util.Collection;

public interface MVRegister<V> extends Crdt {
    void write(V value);

    void clear();

    Collection<V> values();
}
