
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import crdt.api.Model;
import crdt.api.types.PNCounter;

public class PNCounterTest extends TestBase<PNCounter> {

	@Test
	public void testSingleWrite() {
		Model m = createCrdt(db1, NODE_A);
		root(m).increment(10);
		assertEquals(10, root(m).value());
	}
	
	@Test
	public void testSingleWriteAndStore(){
		Model m = createCrdt(db1, NODE_A);
		root(m).increment(10);
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		assertEquals(10, root(m).value());
	}
	
	@Test
	public void testOverride(){
		Model m = createCrdt(db1, NODE_A);
		root(m).increment(10);
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		root(m).decrement(5);
		assertEquals(5, root(m).value());
	}
	
	@Test
	public void testConcurrentCreate(){
		Model mA = createCrdt(db1, NODE_A);
		root(mA).increment(10);
		Model mB = createCrdt(db1, NODE_B);
		root(mB).increment(20);
		db1.store(mA);
		db1.store(mB);
		mA = loadCrdt(db1, NODE_A);
		assertEquals(30, root(mA).value());
	}
	
	
	@Test
	public void testReplication(){
		conns.fixConn();
		Model mA = createCrdt(db1, NODE_A);
		root(mA).increment(10);
		db1.store(mA);
		Model mB = loadCrdt(db2, NODE_B);
		assertEquals(10, root(mB).value());
	}

	
	@Override
	protected PNCounter createCrdt(Model model) {
		return model.factory().createPNCounter();
	}
}
