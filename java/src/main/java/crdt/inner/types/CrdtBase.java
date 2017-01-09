package crdt.inner.types;

import crdt.api.Crdt;
import crdt.inner.CrdtState;
import crdt.inner.causal.Causal;
import crdt.inner.causal.CausalContext;

public abstract class CrdtBase<T extends CrdtState> implements Crdt {
    protected final Causal causal;
    protected Causal delta;
    protected final String nodeId;
    protected final T state;
    protected final CausalContext cc;


    public CrdtBase(String nodeId, T state, CausalContext cc) {
        this.nodeId = nodeId;
        this.state = state;
        this.cc = cc;
        this.causal = new Causal(cc, state);
    }


    protected void joinDelta(CrdtState currentStateDelta, CausalContext currentCCDelta) {
        Causal currentDelta = new Causal(currentCCDelta, currentStateDelta);
        if (delta == null) {
            delta = currentDelta;
        } else {
            delta.join(currentDelta);
        }

        causal.join(currentDelta);
    }


    @Override
    public String innerToString() {
        return this.getClass().getSimpleName() + " [causal=" + causal + ", delta=" + delta + ", nodeId=" + nodeId + "]";
    }


    @Override
    public Causal getCausal() {
        return causal;
    }


    @Override
    public Causal getDelta() {
        return delta;
    }

}
