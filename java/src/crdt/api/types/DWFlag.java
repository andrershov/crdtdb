package crdt.api.types;

import crdt.api.CRDT;

public interface DWFlag extends CRDT {
	void enable();
	void disable();
	boolean read();
}
