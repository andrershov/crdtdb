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
		Map<Dot, V> newMap = new HashMap<>();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IntersectResult<V> intersect(DotMap<V> that){
		IntersectResult<V> res = new IntersectResult<>();
		dotMap.forEach((dot, thisValue) -> {
			V thatValue = that.dotMap.get(dot);
			if (thatValue != null) {
				if (!(thisValue instanceof Comparable)) throw new RuntimeException("If both DotMaps contain same dot, V should implement Comparable interface");
				if (!(thatValue instanceof Comparable)) throw new RuntimeException("If both DotMaps contain same dot, V should implement Comparable interface");
				
				V value = null;
				if (((Comparable)thisValue).compareTo(thatValue) >=0){
					value = thisValue;
				} else {
					res.thisContainsThat = false;
					value = thatValue;
				}
				
				res.newMap.put(dot, value);
			}
		});
		
		return res;
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
		IntersectResult<V> intersectResult = this.intersect(that);
		if (intersectResult.thisContainsThat && thisContext.contains(thatContext)) return false;
		Map<Dot, V> newMap = intersectResult.newMap;
		newMap.putAll(this.minus(thatContext));
		newMap.putAll(that.minus(thisContext));
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
