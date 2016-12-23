package crdt.api.types;

import java.util.Collection;

import crdt.api.CRDT;

public interface MVRegister<V> extends CRDT {
	void write(V value);
	void clear();
	Collection<V> values();
}
