package crdt.inner.types;
import java.util.Collection;

import crdt.api.types.MVRegister;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;

public class MVRegisterImpl<V> extends CrdtBase<MVRegisterState<V>> implements MVRegister<V>  {
	MVRegisterImpl(String nodeId, MVRegisterState<V> state, CausalContext cc){
		super(nodeId, state, cc);
	}
	

	public MVRegisterImpl(String nodeId, CausalContext cc) {
		super(nodeId, new MVRegisterState<>(), cc);
	}

	@Override
	public void write(V value){
		Dot dot = cc.next(nodeId);
		MVRegisterState<V> currentStateDelta = new MVRegisterState<>(dot, value);
		CausalContext currentCCDelta = new CausalContext(state.dots(), dot);
		joinDelta(currentStateDelta, currentCCDelta);
	}

	@Override
	public void clear(){
		MVRegisterState<V> currentStateDelta = new MVRegisterState<>();
		CausalContext currentCCDelta = new CausalContext(state.dots());
		joinDelta(currentStateDelta, currentCCDelta);
	}

	
	@Override
	public Collection<V> values(){
		return state.values();
	}
	

	@Override
	public String toString() {
		return values().toString();
	}
}
