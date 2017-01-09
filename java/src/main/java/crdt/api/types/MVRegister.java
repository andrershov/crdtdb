package crdt.api.types;

import java.util.Collection;

import crdt.api.Crdt;

public interface MVRegister<V> extends Crdt {
	void write(V value);
	void clear();
	Collection<V> values();
}
