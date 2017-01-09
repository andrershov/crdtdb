package crdt.api.types;

import crdt.api.Crdt;

import java.util.Set;

public interface CrdtSet<E> extends Crdt {

    void add(E elem);

    void remove(E elem);

    Set<E> elements();

}