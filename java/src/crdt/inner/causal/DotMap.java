package crdt.inner.causal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import crdt.inner.serializers.DotDeserializer;

public class DotMap<V> {
	@JsonProperty
	@JsonDeserialize(keyUsing = DotDeserializer.class)
	private Map<Dot, V> dotMap;
	
	
	public DotMap(DotMap<V> dotMap){
		this();
		this.dotMap.putAll(dotMap.dotMap);
	}
	
	public DotMap(){
		dotMap = new HashMap<>();
	}
	
	public DotMap(Dot dot, V value){
		this();
		dotMap.put(dot, value);
	}
	
	
	private Map<Dot, V> intersect(DotMap<V> that, JoinFunction<V> joinFn){
		Map<Dot, V> newMap = new HashMap<>();
		dotMap.forEach((dot, thisValue) -> {
			V thatValue = that.dotMap.get(dot);
			if (thatValue != null) {
				V newValue = joinFn.apply(thisValue, thatValue);
				newMap.put(dot, newValue);
			}
		});
		
		return newMap;
	}
	
	
	public Map<Dot, V> minus(CausalContext cc) {
		Map<Dot, V> newMap = new HashMap<>();
		
		dotMap.forEach((dot, value) ->{
			String nodeId = dot.nodeId;
			Integer ccCounter = cc.causalContext.get(nodeId);
		    if (ccCounter == null || ccCounter < dot.counter){
		    	newMap.put(dot, value);
		    }
		});
		return newMap;
	}
	
	public void put(Dot dot, V value){
		this.dotMap.put(dot, value);
	}
	
	public boolean join(DotMap<V> that, CausalContext thisContext, CausalContext thatContext, JoinFunction<V> joinFn){
		Map<Dot, V> newMap = this.intersect(that, joinFn);
		newMap.putAll(this.minus(thatContext));
		newMap.putAll(that.minus(thisContext));
		if (this.dotMap.equals(newMap)) return false;
		this.dotMap = newMap;
		return true;
	}
	
	

	public Collection<V> values(){
		return dotMap.values();
	}

	@JsonIgnore
	public boolean isEmpty() {
		return dotMap.isEmpty();
	}

	@Override
	public String toString() {
		return "DotMap [dotMap=" + dotMap + "]";
	}

	public V get(Dot dot) {
		return dotMap.get(dot);
	}
}
