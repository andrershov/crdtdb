package crdt.api;

import crdt.inner.DeltaStorage;

public interface CrdtDb {
    Model load(String nodeId, String key);

    void store(Model m);

    DeltaStorage getDeltaStorage();
}
