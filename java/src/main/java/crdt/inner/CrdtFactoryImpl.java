package crdt.inner;

import crdt.api.CrdtFactory;
import crdt.api.types.*;
import crdt.inner.causal.CausalContext;
import crdt.inner.types.*;

public class CrdtFactoryImpl implements CrdtFactory {
    private final CausalContext cc;
    private final String nodeId;

    CrdtFactoryImpl(String nodeId, CausalContext cc) {
        this.nodeId = nodeId;
        this.cc = cc;
    }

    public EWFlag createEWFlag() {
        return new EWFlagImpl(nodeId, cc);
    }

    public <V> MVRegister<V> createMVRegister() {
        return new MVRegisterImpl<>(nodeId, cc);
    }

    public DWFlag createDWFlag() {
        return new DWFlagImpl(nodeId, cc);
    }

    @Override
    public PNCounter createPNCounter() {
        return new PNCounterImpl(nodeId, cc);
    }

    @Override
    public <E> AWSet<E> createAWSet() {
        return new AWSetImpl<>(nodeId, cc);
    }

    @Override
    public <E> RWSet<E> createRWSet() {
        return new RWSetImpl<>(nodeId, cc);
    }

    @Override
    public <K> AWMap<K> createAWMap() {
        return new AWMapImpl<>(nodeId, cc);
    }


}
