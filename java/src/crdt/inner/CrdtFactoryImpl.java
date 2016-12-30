package crdt.inner;

import crdt.api.CrdtFactory;
import crdt.api.types.AWMap;
import crdt.api.types.AWSet;
import crdt.api.types.PNCounter;
import crdt.api.types.RWSet;
import crdt.inner.causal.CausalContext;
import crdt.inner.types.AWMapImpl;
import crdt.inner.types.AWSetImpl;
import crdt.inner.types.DWFlagImpl;
import crdt.inner.types.EWFlagImpl;
import crdt.inner.types.MVRegisterImpl;
import crdt.inner.types.PNCounterImpl;
import crdt.inner.types.RWSetImpl;

public class CrdtFactoryImpl implements CrdtFactory {
	private CausalContext cc;
	private String nodeId;

	CrdtFactoryImpl(String nodeId, CausalContext cc) {
		this.nodeId = nodeId;
		this.cc = cc;
	}
	
	public EWFlagImpl createEWFlag(){
		return new EWFlagImpl(nodeId, cc);
	}

	public <V> MVRegisterImpl<V> createMVRegister() {
		return new MVRegisterImpl<V>(nodeId, cc);
	}

	public DWFlagImpl createDWFlag() {
		return new DWFlagImpl(nodeId, cc);
	}

	@Override
	public PNCounter createPNCounter() {
		return new PNCounterImpl(nodeId, cc);
	}

	@Override
	public <E> AWSetImpl<E> createAWSet() {
		return new AWSetImpl<>(nodeId, cc);
	}

	@Override
	public <E> RWSet<E> createRWSet() {
		return new RWSetImpl<>(nodeId, cc);
	}

	@Override
	public <K> AWMap<K> createAWMap() {
		return new AWMapImpl<>(nodeId, cc);
	}


}
