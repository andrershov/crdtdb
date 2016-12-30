package crdt.api.types;

import crdt.api.Crdt;

public interface DWFlag extends Crdt {
	void enable();
	void disable();
	boolean read();
}
