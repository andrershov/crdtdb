package crdt.inner;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import crdt.api.CrdtDb;
import crdt.api.Model;
import crdt.inner.conn.NodeConnection;

public class CrdtDbImpl implements CrdtDb {
	
	private ConcurrentHashMap<String, ModelState> map = new ConcurrentHashMap<>();
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
	
	public ModelState loadFullState(String key){
		return map.get(key);
	}
	

	public ModelImpl load(String nodeId, String key) {
		ModelState state = map.get(key);
		if (state == null) {
			return new ModelImpl(nodeId, key);
		}
		return new ModelImpl(nodeId, state);
	}
	
	public void store(Model model) {
		ModelState delta = ((ModelImpl)model).getDelta();
		storeDelta(delta);
	}

	public void storeDelta(ModelState delta) {
		AtomicBoolean storeDelta = new AtomicBoolean(true);
		map.compute(delta.getKey(), (ig, oldModel) -> {
			if (oldModel == null) {
				return delta;
			} 
			storeDelta.set(oldModel.join(delta));
			return oldModel;
		});
		if (storeDelta.get()){
			deltaStorage.store(delta);
			deltaExchanger.shipState(delta.getKey());
		}
	}

	@Override
	public DeltaStorage getDeltaStorage() {
		return deltaStorage;
	}
}
