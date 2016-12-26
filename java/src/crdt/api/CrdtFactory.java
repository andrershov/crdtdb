package crdt.api;

import crdt.api.types.AWSet;
import crdt.api.types.PNCounter;
import crdt.api.types.RWSet;
import crdt.inner.types.DWFlagImpl;
import crdt.inner.types.EWFlagImpl;
import crdt.inner.types.MVRegisterImpl;

public interface CrdtFactory {
	public EWFlagImpl createEWFlag();

	public <V> MVRegisterImpl<V> createMVRegister();

	public DWFlagImpl createDWFlag();

	public PNCounter createPNCounter();

	public <E> AWSet<E> createAWSet();
	
	public <E> RWSet<E> createRWSet(); 
}
