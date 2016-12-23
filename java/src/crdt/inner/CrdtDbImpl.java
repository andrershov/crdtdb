package crdt.inner;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import crdt.api.CrdtDb;
import crdt.api.Model;
import crdt.inner.conn.NodeConnection;

public class CrdtDbImpl implements CrdtDb {
	
	private ConcurrentHashMap<String, ModelImpl> map = new ConcurrentHashMap<>();
	private DeltaStorage deltaStorage = new DeltaStorage();
	private DeltaExchanger deltaExchanger;
	
	public CrdtDbImpl(List<? extends NodeConnection> nodes){
		deltaExchanger = new DeltaExchanger(this, deltaStorage, nodes);
	}
	public CrdtDbImpl(){
		this(Collections.emptyList());
	}
	
	public CrdtDbImpl(NodeConnection node){
		this(Collections.singletonList(node));
	}
	

	public ModelImpl load(String nodeId, String key) {
		ModelImpl model = map.get(key);
		if (model == null) {
			return ModelImpl.fromScratch(nodeId);
		}
		return ModelImpl.fromExistingAndNewNodeId(nodeId, model);
	}
	
	public void store(String key, Model model) {
		ModelImpl delta = ((ModelImpl)model).getDelta();
		storeDelta(key, delta);
	}

	public void storeDelta(String key, ModelImpl delta) {
		AtomicBoolean storeDelta = new AtomicBoolean(true);
		map.compute(key, (ig, oldModel) -> {
			if (oldModel == null) {
				return delta;
			} 
			storeDelta.set(oldModel.joinDelta(delta));
			return oldModel;
		});
		if (storeDelta.get()){
			deltaStorage.store(delta);
			deltaExchanger.shipState();
		}
	}

	@Override
	public DeltaStorage getDeltaStorage() {
		return deltaStorage;
	}
}
