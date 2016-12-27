package crdt.inner.types;

import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.RWSet;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotMap;
import crdt.inner.causal.DotSet;
import crdt.inner.causal.DotStore;

public class RWSetImpl<V> implements RWSet<V> {

	@JsonIgnore
	private CausalContext cc;
	@JsonProperty("dotMap")
	private DotMap<V> dotMap = new DotMap<>();
	@JsonIgnore
	private RWSetImpl<V> delta;
	
	private RWSetImpl(CausalContext cc, DotMap<V> dotMap){
		this.cc = cc;
		this.dotMap = dotMap;
	}

	public RWSetImpl(CausalContext cc) {
		this(cc, new DotMap<>());
	}

	@JsonCreator
	public RWSetImpl(@JsonProperty("dotMap") DotMap<V> dotMap) {
		this.dotMap = dotMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean join(CRDT that) {
		if (that == null) return false;
		if (!(that instanceof RWSetImpl)) throw new RuntimeException("CRDT types do not match");
		RWSetImpl<V> thatSet = (RWSetImpl<V>)that;
		
		if (dotMap.join(thatSet.dotMap, cc, thatSet.cc)){
			cc.join(thatSet.cc);
			return true;
		}
		return false;
	}

	@Override
	public CRDT clone(CausalContext cc) {
		return new RWSetImpl<>(cc, dotMap.copy());
	}

	@Override
	public CRDT getDelta() {
		return delta;
	}

	@Override
	public void add(V elem) {
		this.join(addDelta(elem));
	}
	
	
	private CRDT updateDelta(V elem, boolean add) {
		Dot dot = cc.next();
		DotMap<Boolean> innerMap = new DotMap<>(add, new DotSet(dot));
		DotMap<V> outterMap = new DotMap<>(elem, innerMap);
		CausalContext newCC = new CausalContext(cc, dotMap.get(elem).dots());
		newCC.addDot(dot);
		return createAndMergeDelta(outterMap, newCC);
	}

	private CRDT addDelta(V elem) {
		return updateDelta(elem, true);
	}

	private CRDT removeDelta(V elem) {
		return updateDelta(elem, false);
	}

	private RWSetImpl<V> createAndMergeDelta(DotMap<V> newDotMap, CausalContext newCC) {
		RWSetImpl<V> currentDelta = new RWSetImpl<>(newCC, newDotMap);
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}

		return currentDelta;
	}

	@Override
	public void remove(V elem) {
		this.join(removeDelta(elem));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<V> elements() {
		Set<V> res = new HashSet<>();
		Set<Entry<V, DotStore>> nonEmptyEntriesOutterMap = dotMap.nonEmptyEntries().collect(Collectors.toSet());
		for (Entry<V, DotStore> entry : nonEmptyEntriesOutterMap){
			V elem = entry.getKey();
			Stream<Entry<Boolean, DotStore>> nonEmptyEntriesInnerMap = ((DotMap<Boolean>)entry.getValue()).nonEmptyEntries();
			if (nonEmptyEntriesInnerMap.noneMatch(e -> e.getKey() == false)){
				res.add(elem);
			}
		}
		return res;
	}
	
	@Override
	public String toString() {
		return "RWSetImpl [dotMap=" + dotMap + ", cc=" + cc + ", delta=" + delta + "]";
	}

}
