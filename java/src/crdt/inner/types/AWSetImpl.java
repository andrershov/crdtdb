package crdt.inner.types;

import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.AWSet;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotMap;
import crdt.inner.causal.DotSet;

public class AWSetImpl<V> implements AWSet<V> {
	@JsonProperty("dotMap")
	public DotMap<V> dotMap;
	@JsonIgnore
	private CausalContext cc;
	@JsonIgnore
	private AWSetImpl<V> delta;
	
	private AWSetImpl(CausalContext cc, DotMap<V> dotMap){
		this.cc = cc;
		this.dotMap = dotMap;
	}
	
	public AWSetImpl(CausalContext cc) {
		this(cc, new DotMap<>());
	}
	
	@JsonCreator
	public AWSetImpl(@JsonProperty("dotMap") DotMap<V> dotMap) {
		this.dotMap = dotMap;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean join(CRDT that) {
		if (that == null) return false;
		if (!(that instanceof AWSetImpl)) throw new RuntimeException("CRDT types do not match");
		AWSetImpl<V> thatSet = (AWSetImpl<V>)that;
	
		if (dotMap.join(thatSet.dotMap, cc, thatSet.cc)){
			cc.join(thatSet.cc);
			return true;
		}
		return false;
	}

	@Override
	public CRDT clone(CausalContext cc) {
		return new AWSetImpl<>(cc, dotMap.copy());
	}

	@Override
	@JsonIgnore
	public CRDT getDelta() {
		return delta;
	}

	@Override
	public void add(V elem) {
		this.join(addDelta(elem));
	}
	
	private AWSetImpl<V> createAndMergeDelta(DotMap<V> newDotMap, CausalContext newCC) {
		AWSetImpl<V> currentDelta =  new AWSetImpl<>(newCC, newDotMap);
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}
		
		return currentDelta;
	}

	private AWSetImpl<V> addDelta(V elem) {
		Dot dot = cc.next();
		DotMap<V> newDotMap = new DotMap<>(elem, new DotSet(dot));
		CausalContext newCC = new CausalContext(cc, dotMap.get(elem).dots());
		newCC.addDot(dot);
		return createAndMergeDelta(newDotMap, newCC);
	}
	
	@Override
	public void remove(V elem) {
		this.join(removeDelta(elem));
	}

	private CRDT removeDelta(V elem) {
		DotMap<V> newDotMap = new DotMap<>();
		CausalContext newCC = new CausalContext(cc, dotMap.get(elem).dots());
		return createAndMergeDelta(newDotMap, newCC);
	}


	@Override
	public Set<V> elements() {
		return dotMap.nonEmptyEntries().map(entry -> entry.getKey()).collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		return "AWSetImpl [dotMap=" + dotMap + ", cc=" + cc + ", delta=" + delta + "]";
	}
	
	
}
