package crdt.inner.types;

import crdt.api.Crdt;
import crdt.inner.CrdtState;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.DotMap;
import crdt.inner.causal.DotStore;

public class RWSetState<V> extends DotMap<V> implements CrdtState {

    public RWSetState() {
        super();
    }

    public RWSetState(V elem, DotStore dotStore) {
        super(elem, dotStore);
    }

    @Override
    public Crdt createCrdt(String nodeId, CausalContext cc) {
        return new RWSetImpl<>(nodeId, this, cc);
    }

    @Override
    public DotMap<V> createEmpty() {
        return new RWSetState<>();
    }

}
