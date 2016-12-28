package crdt.inner.types;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.MVRegister;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotFun;
import crdt.inner.types.abstr.DotFunCrdt;
import crdt.inner.types.abstr.DotStoreCrdt;

public class MVRegisterImpl<V> extends DotFunCrdt<V> implements MVRegister<V>  {
	public MVRegisterImpl(CausalContext cc, DotFun<V> dotFun){
		super(cc, dotFun);
	}
	
	public MVRegisterImpl(CausalContext cc){
		super(cc);
	}
	
	@JsonCreator
	public MVRegisterImpl(@JsonProperty("dotFun") DotFun<V> dotFun) {
		super(dotFun);
	}
	
	
	private DotStoreCrdt writeDelta(V value){
		Dot dot = cc.next();
		DotFun<V> newDotFun = new DotFun<>(dot, value);
		CausalContext newCC = new CausalContext(cc, dots());
		newCC.addDot(dot);
		return createAndMergeDelta(newDotFun, newCC);
	}
	
	private DotStoreCrdt clearDelta(){
		DotFun<V> newDotFun = new DotFun<>();
		CausalContext newCC = new CausalContext(cc, dots());
		return createAndMergeDelta(newDotFun, newCC);
	}
	
	public void write(V value){
		this.join(writeDelta(value));
	}
	
	public void clear(){
		this.join(clearDelta());
	}

	
	public Collection<V> values(){
		return dotFun.values();
	}
	

	@Override
	public String toString() {
		return values().toString();
	}

	@Override
	public boolean join(CRDT that) {
		if (that == null) return false;
		if (!(that instanceof MVRegisterImpl)) throw new RuntimeException("CRDT types do not match");
		@SuppressWarnings("unchecked")
		MVRegisterImpl<V> thatReg = (MVRegisterImpl<V>)that;
	
		return join(thatReg);
	}

	

	@Override
	protected DotFunCrdt<V> createCRDT(DotFun<V> newDotfun, CausalContext cc) {
		return new MVRegisterImpl<>(cc, newDotfun);
	}

	@Override
	public String innerToString() {
		return "MVRegisterImpl [cc=" + cc + ", dotFun=" + dotFun + ", delta=" + delta + "]";
	}

}
