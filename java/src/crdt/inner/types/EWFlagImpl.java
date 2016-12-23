package crdt.inner.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.EWFlag;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotSet;

public class EWFlagImpl implements EWFlag  {
	@JsonIgnore
	private EWFlagImpl delta;
	@JsonProperty("dotSet")
	protected DotSet dotSet;
	@JsonIgnore
	protected CausalContext cc;
	
	
	private EWFlagImpl(CausalContext cc, DotSet dotSet){
		this.cc = cc;
		this.dotSet = dotSet;
	}
	
	public EWFlagImpl(CausalContext cc){
		this(cc, new DotSet());
	}
	
	@JsonCreator
	public EWFlagImpl(@JsonProperty("dotSet") DotSet dotSet){
		this.dotSet = dotSet;
	}
	
	
	private EWFlagImpl createAndMergeDelta(DotSet newDotset, CausalContext newCC) {
		EWFlagImpl currentDelta =  new EWFlagImpl(newCC, newDotset);
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}
		
		return currentDelta;
	}
	
	private EWFlagImpl enableDelta(){
		Dot dot = cc.next();
		DotSet newDotset = new DotSet(dot);
		return createAndMergeDelta(newDotset, cc.addDot(dot));
	}
	
	private EWFlagImpl disableDelta(){
		DotSet newDotset = new DotSet();
		return createAndMergeDelta(newDotset, cc);
	}
	
	
	public void enable(){
		EWFlagImpl currentDelta = enableDelta();
		this.join(currentDelta);
	}
	
	public void disable(){
		EWFlagImpl currentDelta = disableDelta();
		this.join(currentDelta);
	}
	
		
	public boolean join(CRDT that){
		if (that == null) return false;
		if (!(that instanceof EWFlagImpl)) throw new RuntimeException("CRDT types do not match");
		EWFlagImpl thatFlag = (EWFlagImpl)that;
	
		if (dotSet.join(thatFlag.dotSet, cc, thatFlag.cc)){
			cc.join(thatFlag.cc);
			return true;
		}
		return false;
	}
	
	public boolean read(){
		return !dotSet.isEmpty();
	}

	@Override
	public EWFlagImpl clone(CausalContext cc) {
		return new EWFlagImpl(cc, new DotSet(dotSet));
	}

	@Override
	@JsonIgnore
	public EWFlagImpl getDelta() {
		return delta;
	}

	@Override
	public String toString() {
		return "EWFlag [delta=" + delta + ", dotSet=" + dotSet + ", cc=" + cc + "]";
	}
}
