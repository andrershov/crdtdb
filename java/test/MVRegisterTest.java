
import org.junit.Test;

import crdt.api.Model;
import crdt.api.types.MVRegister;

public class MVRegisterTest extends TestBase<MVRegister<String>> {

	@Test
	public void testSingleWrite() {
		Model m = createCrdt(db1, NODE_A);
		root(m).write("val");
		assertRegisterValues(root(m), "val");
	}
	
	@Test
	public void testSingleWriteAndStore(){
		Model m = createCrdt(db1, NODE_A);
		root(m).write("val");
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		assertRegisterValues(root(m), "val");
	}
	
	@Test
	public void testOverride(){
		Model m = createCrdt(db1, NODE_A);
		root(m).write("val1");
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		root(m).write("val2");
		assertRegisterValues(root(m), "val2");
	}
	
	@Test
	public void testConcurrentCreate(){
		Model mA = createCrdt(db1, NODE_A);
		root(mA).write("valA");
		Model mB = createCrdt(db1, NODE_B);
		root(mB).write("valB");
		db1.store(mA);
		db1.store(mB);
		mA = loadCrdt(db1, NODE_A);
		assertRegisterValues(root(mA), "valA", "valB");
	}
	
	@Test
	public void testConcurrentCreateAndOverride(){
		testConcurrentCreate();
		Model mA = loadCrdt(db1, NODE_A);
		root(mA).write("newValue");
		db1.store(mA);
		
		mA = loadCrdt(db1, NODE_A);
		assertRegisterValues(root(mA), "newValue");
	}
	
	@Test
	public void testReplication(){
		conns.fixConn();
		Model mA = createCrdt(db1, NODE_A);
		root(mA).write("val");
		db1.store(mA);
		Model mB = loadCrdt(db2, NODE_B);
		assertRegisterValues(root(mB), "val");
	}


	@Override
	protected MVRegister<String> createCrdt(Model model) {
		return model.factory().createMVRegister();
	}
}
