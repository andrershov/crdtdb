package crdt.api;

import crdt.inner.causal.Causal;

public interface Crdt {
	public String innerToString();

	public Causal getCausal();
	
	public Causal getDelta();
	
}
