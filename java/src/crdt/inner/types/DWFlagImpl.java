package crdt.inner.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.DWFlag;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotSet;
import crdt.inner.types.abstr.DotSetCrdt;

public class DWFlagImpl extends DotSetCrdt implements DWFlag  {
	
	protected DWFlagImpl(CausalContext cc, DotSet dotSet){
		super(cc, dotSet);
	}
	
	public DWFlagImpl(CausalContext cc){
		super(cc);
	}
	
	@JsonCreator
	public DWFlagImpl(@JsonProperty("dotSet") DotSet dotSet){
		super(dotSet);
	}
	
	

	private DotSetCrdt disableDelta(){
		Dot dot = cc.next();
		DotSet newDotset = new DotSet(dot);
		CausalContext newCC = new CausalContext(cc, dotSet.dots());
		newCC.addDot(dot);
		return createAndMergeDelta(newDotset, newCC);
	}
	
	private DotSetCrdt enableDelta(){
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
		if (!(that instanceof DWFlagImpl)) throw new RuntimeException("CRDT types do not match");
		DWFlagImpl thatFlag = (DWFlagImpl)that;
		
		return join(thatFlag);
	}
	
	public boolean read(){
		return dotSet.isEmpty();
	}

	@Override
	public DWFlagImpl clone(CausalContext cc) {
		return new DWFlagImpl(cc, dotSet.copy());
	}

	

	@Override
	public String toString() {
		return Boolean.toString(read());
	}


	@Override
	public String innerToString() {
		return "DWFlag [delta=" + delta + ", dotSet=" + dotSet + ", cc=" + cc + "]";
	}

	
	@Override
	protected DotSetCrdt createCRDT(DotSet dotSet, CausalContext cc) {
		return new DWFlagImpl(cc, dotSet);
	}
}
