package crdt.inner.types;

import crdt.api.Crdt;
import crdt.inner.CrdtState;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotSet;

public class EWFlagState extends DotSet implements CrdtState {

	public EWFlagState(Dot dot) {
		super(dot);
	}

	public EWFlagState() {
		super();
	}



	@Override
	public Crdt createCrdt(String nodeId, CausalContext cc) {
		return new EWFlagImpl(nodeId, (EWFlagState)this.copy(), cc);
	}
	
	@Override
	public DotSet createEmpty() {
		return new EWFlagState();
	}

}
