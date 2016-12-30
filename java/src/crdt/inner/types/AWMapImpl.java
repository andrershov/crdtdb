package crdt.inner.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import crdt.api.Crdt;
import crdt.api.types.AWMap;
import crdt.inner.CrdtState;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;

public class AWMapImpl<K> extends CrdtBase<AWMapState<K>> implements AWMap<K> {
	private Map<K, Crdt> crdtsToScan = new HashMap<>();
	
	
	public AWMapImpl(String nodeId, AWMapState<K> state, CausalContext cc){
		super(nodeId, state, cc);
	}
	
	public AWMapImpl(String nodeId, CausalContext cc){
		super(nodeId, new AWMapState<>(), cc);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <V extends Crdt> V get(K key) {
		Crdt crdt;
		if ((crdt = crdtsToScan.get(key))!=null){
			return (V) crdt;
		}
		CrdtState keyState = (CrdtState)state.get(key);
		if (keyState == null) return null;
		crdt = keyState.createCrdt(nodeId, cc);
		crdtsToScan.put(key, (V)crdt);
		return (V) crdt;
	}

	@Override
	public <V extends Crdt> void put(K key, V value) {
		crdtsToScan.put(key, value);
	}

	@Override
	public AWMapState<K> getDelta() {
		AWMapState<K> modifyDeltas = new AWMapState<>();
		CausalContext modifyCCdeltas = new CausalContext();
		for (K key : crdtsToScan.keySet()) {
			Crdt crdt = crdtsToScan.get(key);
			CrdtState delta =  crdt.getDelta();
			if (delta != null) {
				modifyDeltas.put(key, delta);
				modifyCCdeltas.join(crdt.getCausalContextDelta());
			}
		}
		if (stateDelta == null){
			stateDelta = modifyDeltas;
		} else {
			stateDelta.join(modifyDeltas, ccDelta, modifyCCdeltas);
		}
		
		return stateDelta;
	}
	

	@Override
	public void remove(K key) {
		//TODO what if crdt to scan is removed?
		AWMapState<K> currentStateDelta = new AWMapState<>();
		Set<Dot> dotSet = new HashSet<>();
		if (state.get(key) != null) {
			dotSet.addAll(state.get(key).dots());
		}

		CausalContext currentCCDelta = new CausalContext(dotSet);
		joinDelta(currentStateDelta, currentCCDelta);

	}


	@Override
	public String toString() {
		return state.toString();
	}
	
	
	@Override
	public String innerToString() {
		return null;
	}
}
