package crdt.inner.types;

import crdt.api.Crdt;
import crdt.inner.CrdtState;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotFun;

public class PNCounterState extends DotFun<Pair> implements CrdtState {

    public PNCounterState(Dot dot, Pair pair) {
        super(dot, pair);
    }

    public PNCounterState() {
        super();
    }


    @Override
    public Crdt createCrdt(String nodeId, CausalContext cc) {
        return new PNCounterImpl(nodeId, this, cc);
    }

    @Override
    public DotFun<Pair> createEmpty() {
        return new PNCounterState();
    }
}
