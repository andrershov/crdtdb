
import static org.junit.Assert.*;

import org.junit.Test;

import crdt.api.Model;
import crdt.api.types.AWMap;
import crdt.api.types.EWFlag;
import crdt.api.types.MVRegister;

public class AWMapTest extends TestBase<AWMap<String>> {

	public AWMapTest() {
		super();
	}
	
	@Test
	public void testNonExistingKey() {
		Model m = createCrdt(db1, NODE_A);
		MVRegister<String> reg = root(m).get("reg");
		assertNull(reg);
	}

	@Test
	public void testPutAndGet() {
		Model m = createCrdt(db1, NODE_A);
		MVRegister<String> reg = m.factory().createMVRegister();
		reg.write("val");
		root(m).put("reg", reg);
		reg = root(m).get("reg");
		assertRegisterValues(reg, "val");
	}
	
	@Test
	public void testPutStoreGet(){
		Model m = createCrdt(db1, NODE_A);
		
		MVRegister<String> reg = m.factory().createMVRegister();
		reg.write("val");
		root(m).put("reg", reg);
		
		db1.store(m);

		m = loadCrdt(db1, NODE_A);
		reg = root(m).get("reg");
		assertRegisterValues(reg, "val");
	}
	
	@Test
	public void test2PutStoreGet(){
		Model m = createCrdt(db1, NODE_A);
		
		MVRegister<String> reg = m.factory().createMVRegister();
		reg.write("val");
		root(m).put("reg", reg);
		
		EWFlag flag = m.factory().createEWFlag();
		flag.enable();
		root(m).put("flag", flag);
		
		db1.store(m);

		m = loadCrdt(db1, NODE_A);
		reg = root(m).get("reg");
		assertRegisterValues(reg, "val");
		
		flag = root(m).get("flag");
		assertTrue(flag.read());
	}
	
	
	@Test
	public void testOverrideEntryValue(){
		Model m = createCrdt(db1, NODE_A);
		
		MVRegister<String> reg = m.factory().createMVRegister();
		reg.write("val");
		root(m).put("reg", reg);
		db1.store(m);

		m = loadCrdt(db1, NODE_A);
		reg = root(m).get("reg");
		reg.write("val1");
		db1.store(m);
		
		assertRegisterValues(reg, "val1");
	}
	
	@Test
	public void testConcurrentCreate(){
		Model mA = createCrdt(db1, NODE_A);
		MVRegister<String> regA = mA.factory().createMVRegister();
		regA.write("valA");
		root(mA).put("reg", regA);
		

		Model mB = createCrdt(db1, NODE_B);
		MVRegister<String> regB = mB.factory().createMVRegister();
		regB.write("valB");
		root(mB).put("reg", regB);
		
		db1.store(mA);
		db1.store(mB);
		
		
		mA = loadCrdt(db1, NODE_A);
		regA = root(mA).get("reg");
		assertRegisterValues(regA, "valA", "valB");
	}
	
	
	@Test
	public void testResolveConflictInEntry(){
		Model mA = createCrdt(db1, NODE_A);
		MVRegister<String> regA = mA.factory().createMVRegister();
		regA.write("valA");
		root(mA).put("reg", regA);
		

		Model mB = createCrdt(db1, NODE_B);
		MVRegister<String> regB = mB.factory().createMVRegister();
		regB.write("valB");
		root(mB).put("reg", regB);
		
		db1.store(mA);
		db1.store(mB);
		
		
		mA = loadCrdt(db1, NODE_A);
		regA = root(mA).get("reg");
		regA.write("valRes");
		db1.store(mA);
		
		mB = loadCrdt(db1, NODE_B);
		regB = root(mB).get("reg");
		assertRegisterValues(regB, "valRes");
	}
	
	
	
	@Test
	public void testRemoveFromTransient(){
		Model m = createCrdt(db1, NODE_A);
		MVRegister<String> reg = m.factory().createMVRegister();
		reg.write("val");
		root(m).put("reg", reg);
		root(m).remove("reg");
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		assertNull(root(m).get("reg"));
	}
	
	@Test
	public void test2PutStore1RemoveGet(){
		Model m = createCrdt(db1, NODE_A);
		MVRegister<String> reg = m.factory().createMVRegister();
		reg.write("val");
		root(m).put("reg", reg);
		
		EWFlag flag = m.factory().createEWFlag();
		flag.enable();
		root(m).put("flag", flag);
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		root(m).remove("reg");
		db1.store(m);
		
		m = loadCrdt(db1, NODE_A);
		reg = root(m).get("reg");
		assertNull(reg);
		flag = root(m).get("flag");
		assertTrue(flag.read());
	}
	
	@Test
	public void addWinsMVRegister(){
		Model mA = createCrdt(db1, NODE_A);
		MVRegister<String> regA = mA.factory().createMVRegister();
		regA.write("valA");
		root(mA).put("reg", regA);
		db1.store(mA);
		
		Model mB = loadCrdt(db1, NODE_B);
		MVRegister<String> regB = root(mB).get("reg");
		regB.write("valB");

		mA = loadCrdt(db1, NODE_A);
		root(mA).remove("reg");
		
		db1.store(mB);
		db1.store(mA);
		
		mB = loadCrdt(db1, NODE_B);
		regB = root(mB).get("reg");
		assertRegisterValues(regB, "valB"); //Please note, in AW delta-based map only last delta is preserved in case of concurrent remove/update
	}
	
	@Test
	public void testReplication(){
		conns.fixConn();
		
		Model mA = createCrdt(db1, NODE_A);
		MVRegister<String> regA = mA.factory().createMVRegister();
		regA.write("valA");
		root(mA).put("reg", regA);
		db1.store(mA);
		
		Model mB = loadCrdt(db2, NODE_B);
		MVRegister<String> regB = root(mB).get("reg");
		assertRegisterValues(regB, "valA");
	}
	
	@Test
	public void testMapInMapReplication(){
		conns.fixConn();
		
		Model mA = createCrdt(db1, NODE_A);
		AWMap<String> innerMapA = mA.factory().createAWMap();
		MVRegister<String> regA = mA.factory().createMVRegister();
		regA.write("valA");
		innerMapA.put("reg", regA);
		root(mA).put("map", innerMapA);
		db1.store(mA);
		
		Model mB = loadCrdt(db2, NODE_B);
		AWMap<String> innerMapB = root(mB).get("map");
		MVRegister<String> regB = innerMapB.get("reg");
		assertRegisterValues(regB, "valA");
	}
	
	@Override
	protected AWMap<String> createCrdt(Model model) {
		return model.factory().createAWMap();
	}

}