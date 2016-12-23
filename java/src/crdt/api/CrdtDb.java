package crdt.api;

import crdt.inner.DeltaStorage;

public interface CrdtDb {
	public Model load(String nodeId, String key);
	public void store(String key, Model m);
	public DeltaStorage getDeltaStorage();
}
