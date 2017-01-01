import org.junit.Before;

import crdt.api.Crdt;
import crdt.api.CrdtDb;
import crdt.api.Model;
import crdt.inner.CrdtDbImpl;
import crdt.inner.conn.LocalNodeJsonConnections;

public abstract class TestBase<T extends Crdt> {

	protected CrdtDb db1;
	protected CrdtDb db2;
	protected LocalNodeJsonConnections conns;
	protected static final String NODE_A = "nodeA";
	protected static final String NODE_B = "nodeB";

	public TestBase() {
		super();
	}

	@Before
	public void setUp() {
		conns = new LocalNodeJsonConnections(NODE_A, NODE_B);
		db1 = new CrdtDbImpl(conns.getConn1());
		db2 = new CrdtDbImpl(conns.getConn2());
		conns.breakConn();
	}

	protected Model createCrdt(CrdtDb db, String nodeId) {
		Model model = db.load(nodeId, "entity");
		T crdt = createCrdt(model);
		model.setRoot(crdt);
		return model;
	}

	protected Model loadCrdt(CrdtDb db, String nodeId) {
		Model model = db.load(nodeId, "entity");
		return model;
	}

	protected T root(Model m) {
		return m.getRoot();
	}
	
	protected abstract T createCrdt(Model model);
}