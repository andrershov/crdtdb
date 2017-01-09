package crdt.api;

import crdt.api.types.AWMap;
import crdt.api.types.PNCounter;
import crdt.api.types.AWSet;
import crdt.api.types.DWFlag;
import crdt.api.types.EWFlag;
import crdt.api.types.MVRegister;
import crdt.api.types.RWSet;

public interface CrdtFactory {
	public EWFlag createEWFlag();

	public <V> MVRegister<V> createMVRegister();

	public DWFlag createDWFlag();

	public PNCounter createPNCounter();

	public <E> AWSet<E> createAWSet();
	
	public <E> RWSet<E> createRWSet();

	public <K> AWMap<K> createAWMap(); 
}
