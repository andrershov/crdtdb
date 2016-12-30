package crdt.inner.conn;

import crdt.inner.DeltaExchanger;
import crdt.inner.ModelImpl;
import crdt.inner.ModelState;
import crdt.inner.messages.AckMessage;
import crdt.inner.messages.CrdtMessage;
import crdt.inner.messages.DeltaMessage;
import crdt.inner.serializers.JsonSerializer;

public class LocalNodeJsonConnection implements NodeConnection {
	private String nodeId;
	private DeltaExchanger deltaExchanger;
	private JsonSerializer serializer = new JsonSerializer();
	private LocalNodeJsonConnection remoteConnection;
	private boolean broken = false;
	
	
	public LocalNodeJsonConnection(String nodeId){
		this.nodeId = nodeId;
	}
	
	public void setRemoteConnection(LocalNodeJsonConnection remoteConnection){
		this.remoteConnection = remoteConnection;
	}

	/* (non-Javadoc)
	 * @see crdt.inner.conn.NodeConnection#getNodeId()
	 */
	@Override
	public String getName() {
		return nodeId;
	}

	/* (non-Javadoc)
	 * @see crdt.inner.conn.NodeConnection#send(crdt.inner.ModelImpl, int)
	 */
	@Override
	public void send(ModelState deltaInterval, int counter) {
		if (broken) return;
		String msg = serializer.serialize(new DeltaMessage(deltaInterval, counter));
		System.out.println("Sending delta "+nodeId+" msg: "+msg);
		remoteConnection.receive(msg);
	}

	/* (non-Javadoc)
	 * @see crdt.inner.conn.NodeConnection#setDeltaExchanger(crdt.inner.DeltaExchanger)
	 */
	@Override
	public void setDeltaExchanger(DeltaExchanger deltaExchanger) {
		this.deltaExchanger = deltaExchanger;
	}
	
	public void receive(String msg){
		CrdtMessage crdtMsg = serializer.deserialize(msg, CrdtMessage.class);
		if (crdtMsg instanceof AckMessage){
			AckMessage ackMsg = (AckMessage) crdtMsg;
			System.out.println("Received ack msg from node "+nodeId+" msg: "+msg);
			deltaExchanger.onAck(this, ackMsg.key, ackMsg.counter);
		} else if (crdtMsg instanceof DeltaMessage){
			DeltaMessage deltaMsg = (DeltaMessage) crdtMsg;
			System.out.println("Received delta msg from node "+nodeId+" msg: "+msg);
			deltaExchanger.onReceive(this, deltaMsg.deltaInterval, deltaMsg.counter);
		}
	}
	
	/* (non-Javadoc)
	 * @see crdt.inner.conn.NodeConnection#sendAck(int)
	 */
	@Override
	public void sendAck(String key, int counter) {
		if (broken) return;
		String msg = serializer.serialize(new AckMessage(key, counter));
		System.out.println("Sending ack to node "+nodeId+" msg: "+msg);
		remoteConnection.receive(msg);
	}
	
	/* (non-Javadoc)
	 * @see crdt.inner.conn.NodeConnection#breakConn()
	 */
	@Override
	public void breakConn(){
		broken = true;
	}
	
	/* (non-Javadoc)
	 * @see crdt.inner.conn.NodeConnection#fixConn()
	 */
	@Override
	public void fixConn(){
		broken = false;
		deltaExchanger.connectionRestored(this);
	}

}
