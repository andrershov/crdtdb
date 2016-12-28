package crdt.inner.types.abstr;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotFun;

public abstract class DotFunCrdt<V> implements DotStoreCrdt {
	@JsonIgnore
	protected CausalContext cc;
	@JsonProperty("dotFun")
	protected DotFun<V> dotFun;
	@JsonIgnore
	protected DotFunCrdt<V> delta;
	
	
	protected DotFunCrdt(CausalContext cc, DotFun<V> dotFun){
		this.cc = cc;
		this.dotFun = dotFun;
	}
	
	protected DotFunCrdt(CausalContext cc){
		this(cc, new DotFun<>());
	}
	
	protected DotFunCrdt(DotFun<V> dotFun){
		this(null, dotFun);
	}
	


	protected boolean join(DotFunCrdt<V> that) {
		if (dotFun.join(that.dotFun, cc, that.cc)){
			cc.join(that.cc);
			return true;
		}
		return false;
	}
	
	protected DotFunCrdt<V> createAndMergeDelta(DotFun<V> newDotfun, CausalContext newCC) {
		DotFunCrdt<V> currentDelta = createCRDT(newDotfun, newCC);  
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}
		
		return currentDelta;
	}
	
	@Override
	public CRDT clone(CausalContext cc) {
		return createCRDT(new DotFun<>(dotFun), cc);
	}
	
	abstract protected DotFunCrdt<V> createCRDT(DotFun<V> newDotfun, CausalContext cc);

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
		return createCRDT(new DotFun<>(), cc);
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return dotFun.isEmpty();
	}

	@Override
	public Set<Dot> dots() {
		return dotFun.dots();
	}
}
