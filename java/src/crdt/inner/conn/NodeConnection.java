package crdt.inner.conn;

import crdt.inner.DeltaExchanger;
import crdt.inner.ModelImpl;

public interface NodeConnection {

	String getName();

	void send(ModelImpl deltaInterval, int counter);

	void setDeltaExchanger(DeltaExchanger deltaExchanger);

	void breakConn();

	void fixConn();

	void sendAck(String key, int counter);

}