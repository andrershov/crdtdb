package crdt.inner.types;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.MVRegister;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotMap;

public class MVRegisterImpl<V> implements MVRegister<V>  {
	@JsonProperty
	private DotMap<V> dotMap;
	@JsonIgnore
	private CausalContext cc;
	@JsonIgnore
	private MVRegisterImpl<V> delta;
	
	public MVRegisterImpl(CausalContext cc, DotMap<V> dotMap){
		this.dotMap = dotMap;
		this.cc = cc;
	}
	
	public MVRegisterImpl(CausalContext cc){
		this(cc, new DotMap<>());
	}
	
	@JsonCreator
	public MVRegisterImpl(@JsonProperty("dotMap") DotMap<V> dotMap) {
		this.dotMap = dotMap;
	}
	
	private MVRegisterImpl<V> createAndMergeDelta(DotMap<V> newDotMap, CausalContext newCC) {
		MVRegisterImpl<V> currentDelta =  new MVRegisterImpl<>(newCC, newDotMap);
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}
		
		return currentDelta;
	}
	
	private MVRegisterImpl<V> writeDelta(V value){
		Dot dot = cc.next();
		DotMap<V> newDotMap = new DotMap<>(dot, value);
		return createAndMergeDelta(newDotMap, cc.addDot(dot));
	}
	
	private MVRegisterImpl<V> clearDelta(){
		DotMap<V> dotMap = new DotMap<>();
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
	public CRDT clone(CausalContext cc) {
		return new MVRegisterImpl<V>(cc, new DotMap<>(dotMap));
	}

	@Override
	public CRDT getDelta() {
		return delta;
	}
	
	
}
