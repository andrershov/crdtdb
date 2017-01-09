package crdt.api.types;

import crdt.api.Crdt;

public interface PNCounter extends Crdt {
    void increment(int count);

    void decrement(int count);

    void reset();

    int value();
}
