package crdt.inner.types;

import crdt.api.Crdt;
import crdt.inner.CrdtState;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotSet;

public class DWFlagState extends DotSet implements CrdtState {

	public DWFlagState(Dot dot) {
		super(dot);
	}

	public DWFlagState() {
		super();
	}

	
	@Override
	public Crdt createCrdt(String nodeId, CausalContext cc) {
		return new DWFlagImpl(nodeId, this, cc);
	}
	
	@Override
	public DotSet createEmpty() {
		return new DWFlagState();
	}

}
