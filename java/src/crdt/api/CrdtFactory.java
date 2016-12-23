package crdt.api;

import crdt.inner.types.DWFlagImpl;
import crdt.inner.types.EWFlagImpl;
import crdt.inner.types.MVRegisterImpl;

public interface CrdtFactory {
	public EWFlagImpl createEWFlag();

	public <V> MVRegisterImpl<V> createMVRegister();

	public DWFlagImpl createDWFlag();
}
