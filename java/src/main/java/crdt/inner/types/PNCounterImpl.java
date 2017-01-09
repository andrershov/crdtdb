package crdt.inner.types;

import crdt.api.types.PNCounter;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;

import java.util.HashSet;
import java.util.Optional;

public class PNCounterImpl extends CrdtBase<PNCounterState> implements PNCounter {
    public PNCounterImpl(String nodeId, CausalContext cc) {
        super(nodeId, new PNCounterState(), cc);
    }

    public PNCounterImpl(String nodeId, PNCounterState state, CausalContext cc) {
        super(nodeId, state, cc);
    }

    private void update(Pair updatePair) {
        Optional<Dot> dot = cc.max(nodeId);
        Pair p;
        PNCounterState currentStateDelta;
        CausalContext currentCCDelta;
        if (dot.isPresent() && (p = state.get(dot.get())) != null) {
            Pair newPair = p.add(updatePair);
            currentStateDelta = new PNCounterState(dot.get(), newPair);
            currentCCDelta = new CausalContext(state.dots());
        } else {
            Dot newDot = cc.next(nodeId);
            currentStateDelta = new PNCounterState(newDot, updatePair);
            HashSet<Dot> dots = new HashSet<>();
            dots.add(newDot);
            currentCCDelta = new CausalContext(dots);
        }
        joinDelta(currentStateDelta, currentCCDelta);
    }

    public void increment(int count) {
        update(Pair.inc(count));
    }

    @Override
    public void decrement(int count) {
        update(Pair.dec(count));
    }

    @Override
    public void reset() {
        PNCounterState currentStateDelta = new PNCounterState();
        CausalContext currentCCDelta = new CausalContext(cc);
        joinDelta(currentStateDelta, currentCCDelta);
    }

    @Override
    public int value() {
        int sum = 0;
        for (Pair p : state.values()) {
            sum = sum + p.inc - p.dec;
        }
        return sum;
    }

    @Override
    public String toString() {
        return Integer.toString(value());
    }
}
