package crdt.inner.types;

import crdt.api.CRDT;
import crdt.api.types.DWFlag;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotSet;

public class DWFlagImpl implements DWFlag  {
	private DWFlagImpl delta;
	protected DotSet dotSet;
	protected CausalContext cc;
	
	
	private DWFlagImpl(CausalContext cc, DotSet dotSet){
		this.cc = cc;
		this.dotSet = dotSet;
	}
	
	public DWFlagImpl(CausalContext cc){
		this(cc, new DotSet());
	}
	
	private DWFlagImpl createAndMergeDelta(DotSet newDotset, CausalContext newCC) {
		DWFlagImpl currentDelta =  new DWFlagImpl(newCC, newDotset);
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}
		
		return currentDelta;
	}
	
	private DWFlagImpl disableDelta(){
		Dot dot = cc.next();
		DotSet newDotset = new DotSet(dot);
		return createAndMergeDelta(newDotset, cc.addDot(dot));
	}
	
	private DWFlagImpl enableDelta(){
		DotSet newDotset = new DotSet();
		return createAndMergeDelta(newDotset, cc);
	}
	
	
	public void enable(){
		DWFlagImpl currentDelta = enableDelta();
		this.join(currentDelta);
	}
	
	public void disable(){
		DWFlagImpl currentDelta = disableDelta();
		this.join(currentDelta);
	}
	
		
	public boolean join(CRDT that){
		if (that == null) return false;
		if (!(that instanceof DWFlagImpl)) throw new RuntimeException("CRDT types do not match");
		DWFlagImpl thatFlag = (DWFlagImpl)that;
	
		if (dotSet.join(thatFlag.dotSet, cc, thatFlag.cc)){
			cc.join(thatFlag.cc);
			return true;
		}
		return false;
	}
	
	public boolean read(){
		return dotSet.isEmpty();
	}

	@Override
	public DWFlagImpl clone(CausalContext cc) {
		return new DWFlagImpl(cc, new DotSet(dotSet));
	}

	@Override
	public DWFlagImpl getDelta() {
		return delta;
	}

	@Override
	public String toString() {
		return "DWFlag [delta=" + delta + ", dotSet=" + dotSet + ", cc=" + cc + "]";
	}
}
