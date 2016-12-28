package crdt.inner.types.abstr;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotMap;

public abstract class DotMapCrdt<K> implements DotStoreCrdt {
	@JsonIgnore
	protected CausalContext cc;
	
	@JsonProperty("dotMap")
	protected DotMap<K> dotMap;
	
	@JsonIgnore
	protected DotMapCrdt<K> delta;
	
	
	protected DotMapCrdt(CausalContext cc, DotMap<K> dotMap){
		this.dotMap = dotMap;
		this.cc = cc;
	}
	
	protected DotMapCrdt(CausalContext cc){
		this(cc, new DotMap<>());
	}
	
	protected DotMapCrdt(DotMap<K> dotMap){
		this(null, dotMap);
	}


	protected boolean join(DotMapCrdt<K> that) {
		if (dotMap.join(that.dotMap, cc, that.cc)){
			cc.join(that.cc);
			return true;
		}
		return false;
	}
	
	protected DotMapCrdt<K> createAndMergeDelta(DotMap<K> newDotmap, CausalContext newCC) {
		DotMapCrdt<K> currentDelta = createCRDT(newDotmap, newCC);  
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}
		
		return currentDelta;
	}
	
	@Override
	public CRDT clone(CausalContext cc) {
		return createCRDT(new DotMap<>(dotMap), cc);
	}
	
	abstract protected DotMapCrdt<K> createCRDT(DotMap<K> dotMap, CausalContext cc);

	@JsonIgnore
	@Override
	public CRDT getDelta() {
		return delta;
	}
	
	@Override
	public DotStoreCrdt createEmpty(CausalContext cc) {
		return createCRDT(new DotMap<>(), cc);
	}
	
	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return dotMap.isEmpty();
	}
	
	@JsonIgnore
	@Override
	public CausalContext getCausalContext() {
		return cc;
	}

	@Override
	public Set<Dot> dots() {
		return dotMap.dots();
	}

}
