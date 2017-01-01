
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import crdt.api.Model;
import crdt.api.types.Flag;

public abstract class FlagTest<T extends Flag> extends TestBase<T> {

	public FlagTest() {
		super();
	}

	@Test
	public void testSingleWrite() {
		Model m = createCrdt(db1, NODE_A);
		root(m).enable();
		assertTrue(root(m).read());
	}

	@Test
	public void testSingleWriteAndStore() {
		Model m = createCrdt(db1, NODE_A);
		root(m).enable();
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		assertTrue(root(m).read());
	}

	@Test
	public void testOverride() {
		Model m = createCrdt(db1, NODE_A);
		root(m).enable();
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		root(m).disable();
		assertFalse(root(m).read());
	}

	@Test
	public void testReplication() {
		conns.fixConn();
		Model mA = createCrdt(db1, NODE_A);
		root(mA).enable();
		db1.store(mA);
		Model mB = loadCrdt(db2, NODE_B);
		assertTrue(root(mB).read());
	}

}