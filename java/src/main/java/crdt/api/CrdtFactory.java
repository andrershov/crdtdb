package crdt.api;

import crdt.api.types.*;

public interface CrdtFactory {
    EWFlag createEWFlag();

    <V> MVRegister<V> createMVRegister();

    DWFlag createDWFlag();

    PNCounter createPNCounter();

    <E> AWSet<E> createAWSet();

    <E> RWSet<E> createRWSet();

    <K> AWMap<K> createAWMap();
}
