package crdt.inner.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.EWFlag;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotSet;
import crdt.inner.types.abstr.DotSetCrdt;

public class EWFlagImpl extends DotSetCrdt implements EWFlag  {
	
	protected EWFlagImpl(CausalContext cc, DotSet dotSet){
		super(cc, dotSet);
	}
	
	public EWFlagImpl(CausalContext cc){
		super(cc);
	}
	
	@JsonCreator
	public EWFlagImpl(@JsonProperty("dotSet") DotSet dotSet){
		super(dotSet);
	}
	
	

	private DotSetCrdt enableDelta(){
		Dot dot = cc.next();
		DotSet newDotset = new DotSet(dot);
		CausalContext newCC = new CausalContext(cc, dotSet.dots());
		newCC.addDot(dot);
		return createAndMergeDelta(newDotset, newCC);
	}
	
	private DotSetCrdt disableDelta(){
		DotSet newDotset = new DotSet();
		CausalContext newCC = new CausalContext(cc, dotSet.dots());
		return createAndMergeDelta(newDotset, newCC);
	}
	
	
	public void enable(){
		this.join(enableDelta());
	}
	
	public void disable(){
		this.join(disableDelta());
	}
	
		
	public boolean join(CRDT that){
		if (that == null) return false;
		if (!(that instanceof EWFlagImpl)) throw new RuntimeException("CRDT types do not match");
		EWFlagImpl thatFlag = (EWFlagImpl)that;
		
		return join(thatFlag);
	}
	
	public boolean read(){
		return !dotSet.isEmpty();
	}

	@Override
	public EWFlagImpl clone(CausalContext cc) {
		return new EWFlagImpl(cc, dotSet.copy());
	}

	

	@Override
	public String toString() {
		return Boolean.toString(read());
	}


	@Override
	public String innerToString() {
		return "EWFlag [delta=" + delta + ", dotSet=" + dotSet + ", cc=" + cc + "]";
	}

	
	@Override
	protected DotSetCrdt createCRDT(DotSet dotSet, CausalContext cc) {
		return new EWFlagImpl(cc, dotSet);
	}
}
