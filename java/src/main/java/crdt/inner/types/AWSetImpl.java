package crdt.inner.types;

import crdt.api.types.AWSet;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotSet;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AWSetImpl<V> extends CrdtBase<AWSetState<V>> implements AWSet<V> {
    public AWSetImpl(String nodeId, AWSetState<V> state, CausalContext cc) {
        super(nodeId, state, cc);
    }

    public AWSetImpl(String nodeId, CausalContext cc) {
        super(nodeId, new AWSetState<>(), cc);
    }

    @Override
    public void add(V elem) {
        Dot dot = cc.next(nodeId);
        AWSetState<V> currentStateDelta = new AWSetState<>(elem, new DotSet(dot));
        CausalContext currentCCDelta = new CausalContext(state.get(elem).dots(), dot);
        joinDelta(currentStateDelta, currentCCDelta);
    }


    @Override
    public void remove(V elem) {
        AWSetState<V> currentStateDelta = new AWSetState<>();
        CausalContext currentCCDelta = new CausalContext(state.get(elem).dots());
        joinDelta(currentStateDelta, currentCCDelta);
    }


    @Override
    public Set<V> elements() {
        return state.nonEmptyEntries().map(Map.Entry::getKey).collect(Collectors.toSet());
    }


    @Override
    public String toString() {
        return elements().toString();
    }
}
