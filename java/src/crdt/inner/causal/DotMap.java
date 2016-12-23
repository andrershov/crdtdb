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
	
	private static class IntersectResult<V> {
		boolean thisContainsThat = true;
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<Dot, V> intersect(DotMap<V> that){
		Map<Dot, V> newMap = new HashMap<>();
		dotMap.forEach((dot, thisValue) -> {
			V thatValue = that.dotMap.get(dot);
			if (thatValue != null) {
				if (!(thisValue instanceof Comparable)) throw new RuntimeException("If both DotMaps contain same dot, V should implement Comparable interface");
				if (!(thatValue instanceof Comparable)) throw new RuntimeException("If both DotMaps contain same dot, V should implement Comparable interface");
				
				V value = null;
				if (((Comparable)thisValue).compareTo(thatValue) >=0){
					value = thisValue;
				} else {
					value = thatValue;
				}
				
				newMap.put(dot, value);
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
	
	public boolean join(DotMap<V> that, CausalContext thisContext, CausalContext thatContext){
		Map<Dot, V> newMap = this.intersect(that);
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
}
