package crdt.api.types;

import crdt.api.CRDT;

public interface EWFlag extends CRDT {
	void enable();
	void disable();
	boolean read();
}
