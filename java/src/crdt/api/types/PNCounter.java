package crdt.api.types;

import crdt.api.CRDT;

public interface PNCounter extends CRDT {
	public void increment(int count);
	public void decrement(int count);
	public void reset();
	public int value();
}
