package crdt.inner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crdt.inner.conn.NodeConnection;

public class DeltaExchanger {
	private DeltaStorage storage;
	private List<? extends NodeConnection> nodes;
	private Map<String, Integer> ackMap = new HashMap<>();
	private CrdtDbImpl db;
	
	public DeltaExchanger(CrdtDbImpl db, DeltaStorage storage, List<? extends NodeConnection> nodes) {
		this.storage = storage;
		this.db = db;
		this.nodes = nodes;
		for (NodeConnection node: nodes){
			node.setDeltaExchanger(this);
		}
	}
	
	public synchronized void shipState(){
		nodes.forEach(node->shipState(node));
	}
	
	private synchronized void shipState(NodeConnection node){
		int newestDeltaCounter = storage.getNewestDeltaCounter();
		Integer newestAckCounter = ackMap.get(node.getName());
		if (newestAckCounter == null || storage.getOldestDeltaCounter() > newestAckCounter){
			ModelImpl model = db.load(null, "reg");
			if (model.getRoot() != null) {
				node.send(model, newestDeltaCounter);
			}
		} else {
			ModelImpl deltaInterval = storage.getDeltaInterval(newestAckCounter);
			if (deltaInterval != null) {
				node.send(deltaInterval, newestDeltaCounter);
			}
		}
	}
	
	public synchronized void  onReceive(NodeConnection node, ModelImpl deltaInterval, int counter) {
		db.storeDelta("reg", deltaInterval);
		node.sendAck(counter);
	}

	public synchronized void onAck(NodeConnection nodeConnection, int ackCounter) {
		ackMap.compute(nodeConnection.getName(), (k, currCounter)->{
			if (currCounter == null || currCounter < ackCounter){
				return ackCounter;
			}
			return currCounter;
		});
	}

	public void connectionRestored(NodeConnection localNodeConnection) {
		shipState(localNodeConnection);
	}
	
}
