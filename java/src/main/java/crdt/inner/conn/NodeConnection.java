package crdt.inner.conn;

import crdt.inner.DeltaExchanger;
import crdt.inner.causal.Causal;

public interface NodeConnection {

    String getName();

    void send(String key, Causal deltaInterval, int counter);

    void setDeltaExchanger(DeltaExchanger deltaExchanger);

    void breakConn();

    void fixConn();

    void sendAck(String key, int counter);


}