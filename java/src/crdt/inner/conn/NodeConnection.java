package crdt.inner.conn;

import crdt.inner.DeltaExchanger;
import crdt.inner.ModelImpl;

public interface NodeConnection {

	String getName();

	void send(ModelImpl deltaInterval, int counter);

	void setDeltaExchanger(DeltaExchanger deltaExchanger);

	void sendAck(int counter);

	void breakConn();

	void fixConn();

}