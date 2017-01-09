package crdt.inner.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import crdt.api.Crdt;
import crdt.api.types.AWMap;
import crdt.inner.CrdtState;
import crdt.inner.causal.Causal;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotStore;
import crdt.inner.causal.EmptyDotStore;

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
		DotStore dotStore = state.get(key);
		if (dotStore instanceof EmptyDotStore) return null;
		CrdtState keyState = (CrdtState) dotStore;
		crdt = keyState.createCrdt(nodeId, cc);
		crdtsToScan.put(key, (V)crdt);
		return (V) crdt;
	}

	@Override
	public <V extends Crdt> void put(K key, V value) {
		crdtsToScan.put(key, value);
	}

	@Override
	public Causal getDelta() {
		AWMapState<K> modifyStateDeltas = new AWMapState<>();
		CausalContext modifyCCdeltas = new CausalContext();
		
		
		for (K key : crdtsToScan.keySet()) {
			Crdt crdt = crdtsToScan.get(key);
			Causal delta =  crdt.getDelta();
			if (delta != null) {
				modifyStateDeltas.put(key, delta.getState());
				modifyCCdeltas.join(delta.getCc());
			}
		}
		
		Causal modifyDeltas = new Causal(modifyCCdeltas, modifyStateDeltas);
		if (delta == null){
			delta = modifyDeltas;
		} else {
			delta.join(modifyDeltas);
		}
		
		return delta;
	}
	

	@Override
	public void remove(K key) {
		AWMapState<K> currentStateDelta = new AWMapState<>();
		Set<Dot> dotSet = new HashSet<>();
		DotStore store;
		if ((store = state.get(key)) != null) {
			dotSet.addAll(store.dots());
		}
		Crdt crdt;
		if ((crdt = crdtsToScan.get(key)) != null){
			dotSet.addAll(crdt.getCausal().getState().dots());
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
