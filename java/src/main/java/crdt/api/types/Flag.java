package crdt.api.types;

import crdt.api.Crdt;

public interface Flag extends Crdt {
    void enable();

    void disable();

    boolean read();
}
