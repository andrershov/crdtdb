package crdt.api;

import crdt.inner.CrdtState;
import crdt.inner.causal.Causal;
import crdt.inner.causal.CausalContext;

public interface Crdt {
	public String innerToString();

	public Causal getCausal();
	
	public Causal getDelta();
	
}
