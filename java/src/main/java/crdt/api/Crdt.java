package crdt.api;

import crdt.inner.causal.Causal;

public interface Crdt {
    String innerToString();

    Causal getCausal();

    Causal getDelta();

}
