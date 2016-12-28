package crdt.inner.types.abstr;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotSet;

public abstract class DotSetCrdt implements DotStoreCrdt {
	@JsonIgnore
	protected CausalContext cc;
	@JsonProperty("dotSet")
	protected DotSet dotSet;
	@JsonIgnore
	protected DotSetCrdt delta;
	
	
	protected DotSetCrdt(CausalContext cc, DotSet dotSet){
		this.cc = cc;
		this.dotSet = dotSet;
	}
	
	protected DotSetCrdt(CausalContext cc){
		this(cc, new DotSet());
	}
	
	protected DotSetCrdt(DotSet dotSet){
		this(null, dotSet);
	}
	


	protected boolean join(DotSetCrdt that) {
		if (this.dotSet.join(that.dotSet, cc, that.cc)){
			cc.join(that.cc);
			return true;
		}
		return false;
	}
	
	protected DotSetCrdt createAndMergeDelta(DotSet newDotset, CausalContext newCC) {
		DotSetCrdt currentDelta = createCRDT(newDotset, newCC);  
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}
		
		return currentDelta;
	}
	
	@Override
	public CRDT clone(CausalContext cc) {
		return createCRDT(new DotSet(dotSet), cc);
	}
	
	abstract protected DotSetCrdt createCRDT(DotSet dotSet, CausalContext cc);

	@JsonIgnore
	@Override
	public CRDT getDelta() {
		return delta;
	}
	
	@JsonIgnore
	@Override
	public CausalContext getCausalContext() {
		return cc;
	}

	@Override
	public DotStoreCrdt createEmpty(CausalContext cc) {
		return createCRDT(new DotSet(), cc);
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return dotSet.isEmpty();
	}

	@Override
	public Set<Dot> dots() {
		return dotSet.dots();
	}
}
