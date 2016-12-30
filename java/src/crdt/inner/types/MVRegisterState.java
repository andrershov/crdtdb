package crdt.inner.types;

import crdt.api.Crdt;
import crdt.inner.CrdtState;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotFun;

public class MVRegisterState<V> extends DotFun<V> implements CrdtState {
	
	public MVRegisterState(){
		super();
	}
	
	public MVRegisterState(Dot dot, V value) {
		super(dot, value);
	}
	
	public MVRegisterState(MVRegisterState<V> that){
		super(that);
	}
	
	
	@Override
	public Crdt createCrdt(String nodeId, CausalContext cc) {
		return new MVRegisterImpl<>(nodeId, new MVRegisterState<>(this), cc);
	}
}
