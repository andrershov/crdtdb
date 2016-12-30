package crdt.api.types;

import crdt.api.Crdt;

public interface EWFlag extends Crdt {
	void enable();
	void disable();
	boolean read();
}
