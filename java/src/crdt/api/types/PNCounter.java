package crdt.api.types;

import crdt.api.Crdt;

public interface PNCounter extends Crdt {
	public void increment(int count);
	public void decrement(int count);
	public void reset();
	public int value();
}
