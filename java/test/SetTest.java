
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import crdt.api.Model;
import crdt.api.types.CrdtSet;

public abstract class SetTest<T extends CrdtSet<String>> extends TestBase<T> {

	protected void assertSetValues(T set, String... expectedValues) {
		Set<String> expectedSet = new HashSet<String>(Arrays.asList(expectedValues));
		assertEquals(expectedSet, set.elements());
	}

	public SetTest() {
		super();
	}

	@Test
	public void testSingleWrite() {
		Model m = createCrdt(db1, NODE_A);
		root(m).add("e1");
		assertSetValues(root(m), "e1");
	}

	@Test
	public void testSingleWriteAndStore() {
		Model m = createCrdt(db1, NODE_A);
		root(m).add("e1");
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		assertSetValues(root(m), "e1");
	}

	@Test
	public void testAddTwoElements() {
		Model m = createCrdt(db1, NODE_A);
		root(m).add("e1");
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		root(m).add("e2");
		assertSetValues(root(m), "e1", "e2");
	}

	@Test
	public void testAddAndRemove() {
		Model m = createCrdt(db1, NODE_A);
		root(m).add("e1");
		root(m).add("e2");
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		root(m).remove("e2");
		assertSetValues(root(m), "e1");
	}

	@Test
	public void testConcurrentCreate() {
		Model mA = createCrdt(db1, NODE_A);
		root(mA).add("e1");
		Model mB = createCrdt(db1, NODE_B);
		root(mB).add("e2");
		db1.store(mA);
		db1.store(mB);
		mA = loadCrdt(db1, NODE_A);
		assertSetValues(root(mA), "e1", "e2");
	}

	@Test
	public void testReplication() {
		conns.fixConn();
		Model mA = createCrdt(db1, NODE_A);
		root(mA).add("e1");
		db1.store(mA);
		Model mB = loadCrdt(db2, NODE_B);
		assertSetValues(root(mB), "e1");
	}

}