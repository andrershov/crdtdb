package crdt.inner.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.AWMap;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.types.abstr.DotStoreCrdt;

public class AWMapImpl<K> implements AWMap<K>, DotStoreCrdt {
	@JsonProperty("map")
	private Map<K, DotStoreCrdt> map;
	@JsonIgnore
	private CausalContext cc;
	@JsonIgnore
	private AWMapImpl<K> delta;

	public AWMapImpl(CausalContext cc, Map<K, DotStoreCrdt> map) {
		this.cc = cc;
		this.map = map;
	}

	public AWMapImpl(CausalContext cc) {
		this(cc, new HashMap<>());
	}

	@JsonCreator
	public AWMapImpl(@JsonProperty("map") Map<K, DotStoreCrdt> map) {
		this(null, map);
	}

	@Override
	public boolean join(CRDT that) {
		if (that == null)
			return false;
		if (!(that instanceof AWMapImpl))
			throw new RuntimeException("CRDT types do not match");
		AWMapImpl<K> thatMap = (AWMapImpl<K>) that;

		Set<K> keySet = new HashSet<>();
		keySet.addAll(this.map.keySet());
		keySet.addAll(thatMap.map.keySet());
		Map<K, DotStoreCrdt> newMap = new HashMap<>();

		boolean changed = false;

		for (K key : keySet) {
			DotStoreCrdt thisVal = this.map.get(key);
			DotStoreCrdt thatVal = thatMap.map.get(key);
			if (thisVal == null) {
				thisVal = thatVal.createEmpty(this.getCausalContext());
			}
			if (thatVal == null) {
				thatVal = thisVal.createEmpty(thatMap.getCausalContext());
			}
			if (!thisVal.isEmpty() || !thatVal.isEmpty()) {
				changed |= thisVal.join(thatVal);
				if (!thisVal.isEmpty()) {
					newMap.put(key, thisVal);
				}
			}
		}
		this.map = newMap;
		return changed;
	}

	@Override
	public <V extends DotStoreCrdt> V get(K key) {
		return (V) map.get(key);
	}

	@Override
	public <V extends DotStoreCrdt> void put(K key, V value) {
		map.put(key, value);

	}

	@JsonIgnore
	@Override
	public CRDT getDelta() {
		AWMapImpl<K> modifyDeltas = getModifyDeltas();
		if (delta != null) {
			delta.join(modifyDeltas);
		} else {
			delta = modifyDeltas;
		}
		return delta;
	}

	private AWMapImpl<K> getModifyDeltas() {
		Map<K, DotStoreCrdt> deltas = new HashMap<>();
		CausalContext deltaCC = CausalContext.fromScratch(cc.getNodeId());
		for (K key : map.keySet()) {
			DotStoreCrdt delta = (DotStoreCrdt) map.get(key).getDelta();
			if (delta != null) {
				deltas.put(key, delta);
				deltaCC.join(delta.getCausalContext());
			}

		}
		return new AWMapImpl<>(deltaCC, deltas);
	}

	protected AWMapImpl<K> createAndMergeDelta(Map<K, DotStoreCrdt> newMap, CausalContext newCC) {
		AWMapImpl<K> currentDelta = new AWMapImpl<>(cc, newMap);
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}

		return currentDelta;
	}

	@Override
	public void remove(K key) {
		this.join(removeDelta(key));

	}

	private AWMapImpl<K> removeDelta(K key) {
		Map<K, DotStoreCrdt> newMap = new HashMap<>();
		Set<Dot> dotSet = new HashSet<>();
		if (map.get(key) != null) {
			dotSet.addAll(map.get(key).dots());
		}

		CausalContext newCC = new CausalContext(cc, dotSet);
		return createAndMergeDelta(newMap, newCC);
	}

	@Override
	public CRDT clone(CausalContext cc) {
		Map<K, DotStoreCrdt> thatMap = new HashMap<>();
		for (Entry<K, DotStoreCrdt> entry : this.map.entrySet()) {
			thatMap.put(entry.getKey(), (DotStoreCrdt) entry.getValue().clone(cc));
		}
		return new AWMapImpl<>(cc, thatMap);
	}

	@JsonIgnore
	@Override
	public CausalContext getCausalContext() {
		return cc;
	}

	@Override
	public DotStoreCrdt createEmpty(CausalContext cc) {
		return new AWMapImpl<>(cc);
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<Dot> dots() {
		Set<Dot> dotSet = new HashSet<>();
		for (DotStoreCrdt crdt : map.values()) {
			dotSet.addAll(crdt.dots());
		}
		return dotSet;
	}

	@Override
	public String toString() {
		return map.toString();
	}
	
	private String getMapString(){
		Iterator<Entry<K, DotStoreCrdt>> i = map.entrySet().iterator();
		if (!i.hasNext())
			return "{}";

		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			Entry<K, DotStoreCrdt> e = i.next();
			K key = e.getKey();
			DotStoreCrdt value = e.getValue();
			sb.append(key == this ? "(this Map)" : key);
			sb.append('=');
			sb.append(value == this ? "(this Map)" : value.innerToString());
			if (!i.hasNext())
				return sb.append('}').toString();
			sb.append(',').append(' ');
		}

	}
	
	@Override
	public String innerToString() {
		return "AWMapImpl [map=" + getMapString() + ", cc=" + cc + ", delta=" + delta + "]";
	}
	
}
