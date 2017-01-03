package crdt.inner.types;

import crdt.api.Crdt;
import crdt.inner.CrdtState;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.DotMap;
import crdt.inner.causal.DotStore;

public class AWSetState<V> extends DotMap<V> implements CrdtState {
	public AWSetState(){
		super();
	}
	
	public AWSetState(V value, DotStore dotStore) {
		super(value, dotStore);
	}
	
	
	@Override
	public Crdt createCrdt(String nodeId, CausalContext cc) {
		return new AWSetImpl<>(nodeId, (AWSetState<V>)this.copy(), cc);
	}
	
	@Override
	public DotMap<V> createEmpty() {
		return new AWSetState<>();
	}

}
