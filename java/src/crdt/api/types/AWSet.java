package crdt.api.types;

import java.util.Set;

import crdt.api.Crdt;

public interface AWSet<E> extends Crdt {
	public void add(E elem);
	public void remove(E elem);
	public Set<E> elements();
}
