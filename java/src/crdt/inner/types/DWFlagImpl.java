package crdt.inner.types;

import crdt.api.types.DWFlag;
import crdt.api.types.EWFlag;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;

public class DWFlagImpl extends CrdtBase<DWFlagState> implements DWFlag  {
	
	public DWFlagImpl(String nodeId, DWFlagState state, CausalContext cc){
		super(nodeId, state, cc);
	}
	
	public DWFlagImpl(String nodeId, CausalContext cc) {
		super(nodeId, new DWFlagState(), cc);
	}

	public void disable(){
		Dot dot = cc.next(nodeId);
		DWFlagState currentStateDelta = new DWFlagState(dot);
		CausalContext currentCCDelta = new CausalContext(state.dots(), dot);
		joinDelta(currentStateDelta, currentCCDelta);
	}
	
	public void enable(){
		DWFlagState currentStateDelta = new DWFlagState();
		CausalContext currentCCDelta = new CausalContext(state.dots());
		joinDelta(currentStateDelta, currentCCDelta);
	}
	
		
	public boolean read(){
		return state.isEmpty();
	}
	

	@Override
	public String toString() {
		return Boolean.toString(read());
	}
}
