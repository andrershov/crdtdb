package crdt.api.types;

import crdt.api.CRDT;
import crdt.inner.types.abstr.DotStoreCrdt;

public interface AWMap<K> extends CRDT, DotStoreCrdt {
	<V extends DotStoreCrdt> V get(K key);
	<V extends DotStoreCrdt> void put(K key, V value);
	void remove(K key);
}
