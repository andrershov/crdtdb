package crdt.api;

import crdt.api.types.AWMap;
import crdt.api.types.PNCounter;
import crdt.api.types.RWSet;
import crdt.inner.types.AWSetImpl;
import crdt.inner.types.DWFlagImpl;
import crdt.inner.types.EWFlagImpl;
import crdt.inner.types.MVRegisterImpl;

public interface CrdtFactory {
	public EWFlagImpl createEWFlag();

	public <V> MVRegisterImpl<V> createMVRegister();

	public DWFlagImpl createDWFlag();

	public PNCounter createPNCounter();

	public <E> AWSetImpl<E> createAWSet();
	
	public <E> RWSet<E> createRWSet();

	public <K> AWMap<K> createAWMap(); 
}
