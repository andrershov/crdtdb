package crdt.inner;

import crdt.api.CrdtFactory;
import crdt.inner.causal.CausalContext;
import crdt.inner.types.DWFlagImpl;
import crdt.inner.types.EWFlagImpl;
import crdt.inner.types.MVRegisterImpl;

public class CrdtFactoryImpl implements CrdtFactory {
	private CausalContext cc;

	CrdtFactoryImpl(CausalContext cc) {
		this.cc = cc;
	}
	
	public EWFlagImpl createEWFlag(){
		return new EWFlagImpl(cc);
	}

	public <V> MVRegisterImpl<V> createMVRegister() {
		return new MVRegisterImpl<V>(cc);
	}

	public DWFlagImpl createDWFlag() {
		return new DWFlagImpl(cc);
	}

}
