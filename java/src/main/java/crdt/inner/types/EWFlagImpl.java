package crdt.inner.types;

import crdt.api.types.EWFlag;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;

public class EWFlagImpl extends CrdtBase<EWFlagState> implements EWFlag  {
	
	public EWFlagImpl(String nodeId, EWFlagState state, CausalContext cc){
		super(nodeId, state, cc);
	}
	
	public EWFlagImpl(String nodeId, CausalContext cc) {
		super(nodeId, new EWFlagState(), cc);
	}

	public void enable(){
		Dot dot = cc.next(nodeId);
		EWFlagState currentStateDelta = new EWFlagState(dot);
		CausalContext currentCCDelta = new CausalContext(state.dots(), dot);
		joinDelta(currentStateDelta, currentCCDelta);
	}
	
	public void disable(){
		EWFlagState currentStateDelta = new EWFlagState();
		CausalContext currentCCDelta = new CausalContext(state.dots());
		joinDelta(currentStateDelta, currentCCDelta);
	}
	
		
	public boolean read(){
		return !state.isEmpty();
	}
	

	@Override
	public String toString() {
		return Boolean.toString(read());
	}
}
