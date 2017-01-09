package crdt.api;

import crdt.inner.causal.Causal;

public interface Model {
    <V extends Crdt> V getRoot();

    void setRoot(Crdt root);

    CrdtFactory factory();

    Causal getDelta();

    String getKey();
}
