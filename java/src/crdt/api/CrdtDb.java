package crdt.api;

import crdt.inner.DeltaStorage;

public interface CrdtDb {
	public Model load(String nodeId, String key);
	public void store(Model m);
	public DeltaStorage getDeltaStorage();
}
