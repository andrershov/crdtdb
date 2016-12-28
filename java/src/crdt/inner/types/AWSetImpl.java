package crdt.inner.types;

import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.AWSet;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotMap;
import crdt.inner.causal.DotSet;
import crdt.inner.types.abstr.DotMapCrdt;

public class AWSetImpl<V> extends DotMapCrdt<V> implements AWSet<V> {
	protected AWSetImpl(CausalContext cc, DotMap<V> dotMap){
		super(cc, dotMap);
	}
	
	public AWSetImpl(CausalContext cc) {
		super(cc);
	}
	
	@JsonCreator
	public AWSetImpl(@JsonProperty("dotMap") DotMap<V> dotMap) {
		super(dotMap);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean join(CRDT that) {
		if (that == null) return false;
		if (!(that instanceof AWSetImpl)) throw new RuntimeException("CRDT types do not match");
		AWSetImpl<V> thatSet = (AWSetImpl<V>)that;
		
		return join(thatSet);
	}


	@Override
	public void add(V elem) {
		this.join(addDelta(elem));
	}
	

	private DotMapCrdt<V> addDelta(V elem) {
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

	private DotMapCrdt<V> removeDelta(V elem) {
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
		return elements().toString();
	}

	@Override
	public String innerToString() {
		return "AWSetImpl [dotMap=" + dotMap + ", cc=" + cc + ", delta=" + delta + "]";
	}


	@Override
	protected DotMapCrdt<V> createCRDT(DotMap<V> dotMap, CausalContext cc) {
		return new AWSetImpl<>(cc, dotMap);
	}
	
}
