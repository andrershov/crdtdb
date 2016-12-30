package crdt.inner.types;

import crdt.api.Crdt;
import crdt.inner.CrdtState;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.DotMap;

public class AWMapState<K> extends DotMap<K> implements CrdtState {
	
	public AWMapState() {
		super();
	}
	
	public AWMapState(AWMapState<K> that){
		super(that);
	}

	@Override
	public Crdt createCrdt(String nodeId, CausalContext cc) {
		return new AWMapImpl<>(nodeId, new AWMapState<>(this), cc);
	}


}
