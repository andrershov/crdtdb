package crdt.inner.types;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.MVRegister;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotFun;
import crdt.inner.causal.JoinFunction;

public class MVRegisterImpl<V> implements MVRegister<V>  {
	@JsonProperty
	private DotFun<V> dotMap;
	@JsonIgnore
	private CausalContext cc;
	@JsonIgnore
	private MVRegisterImpl<V> delta;
	
	public MVRegisterImpl(CausalContext cc, DotFun<V> dotMap){
		this.dotMap = dotMap;
		this.cc = cc;
	}
	
	public MVRegisterImpl(CausalContext cc){
		this(cc, new DotFun<>());
	}
	
	@JsonCreator
	public MVRegisterImpl(@JsonProperty("dotMap") DotFun<V> dotMap) {
		this.dotMap = dotMap;
	}
	
	private MVRegisterImpl<V> createAndMergeDelta(DotFun<V> newDotMap, CausalContext newCC) {
		MVRegisterImpl<V> currentDelta =  new MVRegisterImpl<>(newCC, newDotMap);
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}
		
		return currentDelta;
	}
	
	private MVRegisterImpl<V> writeDelta(V value){
		Dot dot = cc.current();
		DotFun<V> newDotMap = new DotFun<>(dot, value);
		return createAndMergeDelta(newDotMap, cc.addDot(dot));
	}
	
	private MVRegisterImpl<V> clearDelta(){
		DotFun<V> dotMap = new DotFun<>();
		return createAndMergeDelta(dotMap, cc);
	}
	
	public void write(V value){
		this.join(writeDelta(value));
	}
	
	public void clear(){
		this.join(clearDelta());
	}

	
	public Collection<V> values(){
		return dotMap.values();
	}
	

	@Override
	public String toString() {
		return "MVRegister [dotMap=" + dotMap + ", cc=" + cc + ", delta=" + delta + "]";
	}

	@Override
	public boolean join(CRDT that) {
		if (that == null) return false;
		if (!(that instanceof MVRegisterImpl)) throw new RuntimeException("CRDT types do not match");
		@SuppressWarnings("unchecked")
		MVRegisterImpl<V> thatReg = (MVRegisterImpl<V>)that;
	
		if (dotMap.join(thatReg.dotMap, cc, thatReg.cc)){
			cc.join(thatReg.cc);
			return true;
		}
		return false;
	}

	@Override
	public MVRegisterImpl<V> clone(CausalContext cc) {
		return new MVRegisterImpl<V>(cc, dotMap.copy());
	}

	@Override
	public MVRegisterImpl<V> getDelta() {
		return delta;
	}
	
	
}
