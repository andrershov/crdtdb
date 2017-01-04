package crdt.inner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crdt.inner.causal.Causal;
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
			Causal causal = db.loadFullState(key);
			node.send(key, causal, newestDeltaCounter);
		} else {
			Causal deltaInterval = storage.getDeltaInterval(key, newestAckCounter);
			if (deltaInterval != null) {
				node.send(key, deltaInterval, newestDeltaCounter);
			}
		}
	}
	
	public synchronized void  onReceive(NodeConnection node, String key, Causal deltaInterval, int counter) {
		db.storeDelta(key, deltaInterval);
		node.sendAck(key, counter);
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
