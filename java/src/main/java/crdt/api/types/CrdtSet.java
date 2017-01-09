package crdt.api.types;

import java.util.Set;

import crdt.api.Crdt;

public interface CrdtSet<E> extends Crdt {

	void add(E elem);

	void remove(E elem);

	Set<E> elements();

}