package crdt.inner.types;

import crdt.api.types.RWSet;
import crdt.inner.causal.*;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RWSetImpl<V> extends CrdtBase<RWSetState<V>> implements RWSet<V> {

    RWSetImpl(String nodeId, RWSetState<V> state, CausalContext cc) {
        super(nodeId, state, cc);
    }

    public RWSetImpl(String nodeId, CausalContext cc) {
        super(nodeId, new RWSetState<>(), cc);
    }


    @Override
    public void add(V elem) {
        update(elem, true);
    }

    @Override
    public void remove(V elem) {
        update(elem, false);
    }


    private void update(V elem, boolean add) {
        Dot dot = cc.next(nodeId);
        DotMap<Boolean> innerMap = new DotMap<>(add, new DotSet(dot));
        RWSetState<V> currentStateDelta = new RWSetState<>(elem, innerMap);
        CausalContext currentCCDelta = new CausalContext(state.get(elem).dots(), dot);
        joinDelta(currentStateDelta, currentCCDelta);
    }


    @SuppressWarnings("unchecked")
    @Override
    public Set<V> elements() {
        Set<V> res = new HashSet<>();
        Set<Entry<V, DotStore>> nonEmptyEntriesOutterMap = state.nonEmptyEntries().collect(Collectors.toSet());
        for (Entry<V, DotStore> entry : nonEmptyEntriesOutterMap) {
            V elem = entry.getKey();
            Stream<Entry<Boolean, DotStore>> nonEmptyEntriesInnerMap = ((DotMap<Boolean>) entry.getValue()).nonEmptyEntries();
            if (nonEmptyEntriesInnerMap.noneMatch(e -> !e.getKey())) {
                res.add(elem);
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return elements().toString();
    }
}
