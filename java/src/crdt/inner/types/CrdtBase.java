package crdt.inner.types;
import crdt.api.Crdt;
import crdt.inner.CrdtState;
import crdt.inner.causal.CausalContext;

public abstract class CrdtBase<C extends CrdtState> implements Crdt  {
	protected C state;
	protected CausalContext cc;
	protected C stateDelta;
	protected CausalContext ccDelta;
	protected String nodeId;

	CrdtBase(String nodeId, C state, CausalContext cc){
		this.nodeId = nodeId;
		this.state = state;
		this.cc = cc;
	}
	
	
	protected void joinDelta(C currentStateDelta, CausalContext currentCCDelta) {
		if (stateDelta == null) {
			stateDelta = currentStateDelta;
			ccDelta = currentCCDelta;
		} else {
			stateDelta.join(currentStateDelta, ccDelta, currentCCDelta);
			ccDelta.join(currentCCDelta);
		}
		state.join(currentStateDelta, cc, currentCCDelta);
		cc.join(currentCCDelta);
	}
	
	@Override
	public CausalContext getCausalContext() {
		return cc;
	}
	
	@Override
	public CrdtState getState() {
		return state;
	}
	

	@Override
	public CrdtState getDelta() {
		return stateDelta;
	}


	@Override
	public CausalContext getCausalContextDelta() {
		return ccDelta;
	}


	@Override
	public String innerToString() {
		return this.getClass() + " [state=" + state + ", cc=" + cc + ", stateDelta=" + stateDelta + ", ccDelta=" + ccDelta
				+ ", nodeId=" + nodeId + "]";
	}
	
}
