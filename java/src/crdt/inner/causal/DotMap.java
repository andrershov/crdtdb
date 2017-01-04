package crdt.inner.causal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import crdt.inner.serializers.PrimitiveKeyDeserializer;

public class DotMap<K> implements DotStore {
	@JsonProperty("dotMap")
	@JsonDeserialize(keyUsing = PrimitiveKeyDeserializer.class)
	public Map<K, DotStore> dotMap; //unfortunately custom key deserializer does not work in conjunction with @JsonCreator

	public DotMap(K key, DotStore dotStore) {
		this();
		dotMap.put(key, dotStore);
	}

	public DotMap() {
		dotMap = new HashMap<>();
	}
	
	public void put(K key, DotStore dotStore) {
		dotMap.put(key, dotStore);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public boolean join(DotStore that, CausalContext thisContext, CausalContext thatContext) {
		if (!that.getClass().equals(this.getClass())) {
			throw new RuntimeException(String.format("Invalid type. This class %s, that class %s", this.getClass(), that.getClass()));
		}
		DotMap<K> thatDotMap = (DotMap<K>) that;

		Set<K> keySet = new HashSet<>();
		keySet.addAll(this.dotMap.keySet());
		keySet.addAll(thatDotMap.dotMap.keySet());
		Map<K, DotStore> newDotMap = new HashMap<>();

		boolean changed = false;

		for (K key : keySet) {
			DotStore thisVal = this.dotMap.get(key);
			DotStore thatVal = thatDotMap.dotMap.get(key);
			if (thisVal == null){
				thisVal = thatVal.createEmpty();
			}
			if (thatVal == null){
				thatVal = thisVal.createEmpty();
			}
			if (!thisVal.isEmpty() || !thatVal.isEmpty()){
				changed |= thisVal.join(thatVal, thisContext, thatContext);
				if (!thisVal.isEmpty()) {
					newDotMap.put(key, thisVal);
				}
			}
		}
		this.dotMap = newDotMap;
		return changed;
	}

	@Override
	@JsonIgnore
	public boolean isEmpty() {
		return dotMap.isEmpty();
	}

	public Stream<Entry<K, DotStore>> nonEmptyEntries() {
		return dotMap.entrySet().stream().filter(entry -> !entry.getValue().isEmpty());
	}
	
	@Override
	public DotMap<K> copy() {
		DotMap<K> that = createEmpty();
		for (Entry<K, DotStore> entry :this.dotMap.entrySet()){
			that.dotMap.put(entry.getKey(), entry.getValue().copy());
		}
		return that;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [dotMap=" + dotMap + "]";
	}

	@Override
	public DotMap<K> createEmpty() {
		return new DotMap<>();
	}

	@Override
	public Set<Dot> dots() {
		Set<Dot> dots = new HashSet<>();
		for (DotStore dotStore : dotMap.values()){
			dots.addAll(dotStore.dots());
		}
		return dots;
	}

	public DotStore get(K key) {
		DotStore res = dotMap.get(key);
		if (res == null) {
			res = new EmptyDotStore();
		}
		return res;
	}

	
	
}
