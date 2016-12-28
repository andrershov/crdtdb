package crdt.inner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crdt.inner.conn.NodeConnection;

public class DeltaExchanger {
	private DeltaStorage storage;
	private List<? extends NodeConnection> nodes;
	
	private CrdtDbImpl db;
	
	
	private Map<String, Map<String, Integer>> globalAckMap = new HashMap<>();
	
	public DeltaExchanger(CrdtDbImpl db, DeltaStorage storage, List<? extends NodeConnection> nodes) {
		this.storage = storage;
		this.db = db;
		this.nodes = nodes;
		for (NodeConnection node: nodes){
			node.setDeltaExchanger(this);
		}
	}
	
	public synchronized void shipState(String key){
		nodes.forEach(node->shipState(key, node));
	}
	
	private synchronized void shipState(String key, NodeConnection node){
		int newestDeltaCounter = storage.getNewestDeltaCounter(key);
		Map<String, Integer> ackMap = globalAckMap.computeIfAbsent(key, k -> new HashMap<>());
		
		Integer newestAckCounter = ackMap.get(node.getName());
		if (newestAckCounter == null || storage.getOldestDeltaCounter(key) > newestAckCounter){
			ModelImpl model = db.load(null, key);
			if (model.getRoot() != null) {
				node.send(model, newestDeltaCounter);
			}
		} else {
			ModelImpl deltaInterval = storage.getDeltaInterval(key, newestAckCounter);
			if (deltaInterval != null) {
				node.send(deltaInterval, newestDeltaCounter);
			}
		}
	}
	
	public synchronized void  onReceive(NodeConnection node, ModelImpl deltaInterval, int counter) {
		db.storeDelta(deltaInterval);
		node.sendAck(deltaInterval.getKey(), counter);
	}

	public synchronized void onAck(NodeConnection nodeConnection, String key, int ackCounter) {
		Map<String, Integer> ackMap = globalAckMap.computeIfAbsent(key, k -> new HashMap<>());
		ackMap.compute(nodeConnection.getName(), (k, currCounter)->{
			if (currCounter == null || currCounter < ackCounter){
				return ackCounter;
			}
			return currCounter;
		});
	}

	public void connectionRestored(NodeConnection localNodeConnection) {
		storage.keySet().forEach(key -> shipState(key, localNodeConnection));
	}
}
