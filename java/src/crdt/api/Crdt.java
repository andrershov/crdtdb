package crdt.api;

import crdt.inner.CrdtState;
import crdt.inner.causal.CausalContext;

public interface Crdt {
	public String innerToString();

	public CrdtState getState();
	public CausalContext getCausalContext();
	
	public CrdtState getDelta();
	public CausalContext getCausalContextDelta();

	
}
