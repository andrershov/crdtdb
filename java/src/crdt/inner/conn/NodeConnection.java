package crdt.inner.conn;

import crdt.inner.DeltaExchanger;
import crdt.inner.ModelState;

public interface NodeConnection {

	String getName();

	void send(ModelState deltaInterval, int counter);

	void setDeltaExchanger(DeltaExchanger deltaExchanger);

	void breakConn();

	void fixConn();

	void sendAck(String key, int counter);

}