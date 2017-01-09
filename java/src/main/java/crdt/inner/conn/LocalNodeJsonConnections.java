package crdt.inner.conn;

public class LocalNodeJsonConnections {
	private LocalNodeJsonConnection conn1;
	private LocalNodeJsonConnection conn2;

	public LocalNodeJsonConnections(String node1, String node2){
		this.conn1 = new LocalNodeJsonConnection(node1+" --> "+node2);
		this.conn2 = new LocalNodeJsonConnection(node2+" --> "+node1);
		conn1.setRemoteConnection(conn2);
		conn2.setRemoteConnection(conn1);
	}

	public NodeConnection getConn1() {
		return conn1;
	}

	public NodeConnection getConn2() {
		return conn2;
	}
	
	public void breakConn(){
		conn1.breakConn();
		conn2.breakConn();
	}
	
	public void fixConn(){
		conn1.fixConn();
		conn2.fixConn();
	}
}
