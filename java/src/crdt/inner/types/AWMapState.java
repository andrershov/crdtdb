package crdt.inner.types;

import crdt.api.Crdt;
import crdt.inner.CrdtState;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.DotMap;

public class AWMapState<K> extends DotMap<K> implements CrdtState {
	
	public AWMapState() {
		super();
	}
	
	@Override
	public Crdt createCrdt(String nodeId, CausalContext cc) {
		return new AWMapImpl<>(nodeId, this, cc);
	}

	@Override
	public DotMap<K> createEmpty() {
		return new AWMapState<>();
	}

}
