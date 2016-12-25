package crdt.api.types;

import java.util.Set;

import crdt.api.CRDT;

public interface AWSet<E> extends CRDT {
	public void add(E elem);
	public void remove(E elem);
	public Set<E> elements();
}