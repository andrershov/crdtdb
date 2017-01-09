package crdt.inner;

import crdt.api.Crdt;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.DotStore;

public interface CrdtState extends DotStore {
    Crdt createCrdt(String nodeId, CausalContext cc);
}
