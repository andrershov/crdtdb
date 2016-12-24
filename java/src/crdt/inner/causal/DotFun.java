package crdt.inner.causal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import crdt.inner.serializers.DotDeserializer;

public class DotFun<V> implements DotStore {
	@JsonProperty
	@JsonDeserialize(keyUsing = DotDeserializer.class)
	private Map<Dot, V> dotFun;
	
	
	
	public DotFun(){
		dotFun = new HashMap<>();
	}
	
	public DotFun(Dot dot, V value){
		this();
		dotFun.put(dot, value);
	}
	
	
	@SuppressWarnings("unchecked")
	private Map<Dot, V> intersect(DotFun<V> that){
		Map<Dot, V> newMap = new HashMap<>();
		dotFun.forEach((dot, thisValue) -> {
			V thatValue = that.dotFun.get(dot);
			if (thatValue != null) {
				V newValue;
				if (thisValue instanceof Lattice && thatValue instanceof Lattice) {
					newValue = (V) ((Lattice) thisValue).join((Lattice) thatValue);
				} else {
					if (thisValue.equals(thatValue)){
						newValue = thisValue;
					} else {
						throw new RuntimeException("DotFun values should either implement Lattice interface or be equal for same dot");
					}
				}
				
				
				newMap.put(dot, newValue);
			}
		});
		
		return newMap;
	}
	
	
	public Map<Dot, V> minus(CausalContext cc) {
		Map<Dot, V> newMap = new HashMap<>();
		
		dotFun.forEach((dot, value) ->{
			String nodeId = dot.nodeId;
			Integer ccCounter = cc.causalContext.get(nodeId);
		    if (ccCounter == null || ccCounter < dot.counter){
		    	newMap.put(dot, value);
		    }
		});
		return newMap;
	}
	
	public void put(Dot dot, V value){
		this.dotFun.put(dot, value);
	}
	
	@SuppressWarnings("unchecked")
	public boolean join(DotStore thatDotStore, CausalContext thisContext, CausalContext thatContext){
		DotFun<V> that = (DotFun<V>)thatDotStore;
		Map<Dot, V> newDotFun = this.intersect(that);
		newDotFun.putAll(this.minus(thatContext));
		newDotFun.putAll(that.minus(thisContext));
		if (this.dotFun.equals(newDotFun)) return false;
		this.dotFun = newDotFun;
		return true;
	}
	
	@Override
	public DotFun<V> copy() {
		DotFun<V> that = new DotFun<>();
		that.dotFun.putAll(this.dotFun);
		return that;
	}

	public Collection<V> values(){
		return dotFun.values();
	}

	@JsonIgnore
	public boolean isEmpty() {
		return dotFun.isEmpty();
	}

	@Override
	public String toString() {
		return "DotMap [dotMap=" + dotFun + "]";
	}

	public V get(Dot dot) {
		return dotFun.get(dot);
	}
}
