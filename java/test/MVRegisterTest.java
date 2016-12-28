
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import crdt.api.CrdtDb;
import crdt.api.Model;
import crdt.api.types.MVRegister;
import crdt.inner.CrdtDbImpl;

public class MVRegisterTest {

	private CrdtDb db1;
	private Model modelA;
	private Model modelB;
	
	
	@Before
	public void setUp(){
		db1 = new CrdtDbImpl();
	}
	
	private MVRegister<String> createRegisterA(){
		modelA = db1.load("nodeA", "reg1");
		MVRegister<String> reg = modelA.factory().createMVRegister();
		modelA.setRoot(reg);
		return reg;
	}
	
	private MVRegister<String> createRegisterB(){
		modelB = db1.load("nodeB", "reg1");
		MVRegister<String> reg = modelB.factory().createMVRegister();
		modelB.setRoot(reg);
		return reg;
	}
	
	private MVRegister<String> loadRegisterA() {
		modelA = db1.load("nodeA", "reg1");
		return modelA.getRoot();
	}
	
	private MVRegister<String> loadRegisterB() {
		modelB = db1.load("nodeB", "reg1");
		return modelB.getRoot();
	}
	
	private static void assertRegisterValues(MVRegister<String> reg, String... expectedValues){
		Set<String> regVal = new HashSet<>(reg.values());
		Set<String> expectedVal = new HashSet<String>(Arrays.asList(expectedValues));
		assertEquals(expectedVal, regVal);
	}
	

	@Test
	public void testSingleWrite() {
		MVRegister<String> reg = createRegisterA();
		reg.write("val");
		assertRegisterValues(reg, "val");
	}
	
	@Test
	public void testSingleWriteAndStore(){
		MVRegister<String> reg = createRegisterA();
		reg.write("val");
		db1.store(modelA);
		
		reg = loadRegisterA();
		assertRegisterValues(reg, "val");
	}
	
	@Test
	public void testOverride(){
		MVRegister<String> reg = createRegisterA();
		reg.write("val1");
		db1.store(modelA);
		
		reg = loadRegisterA();
		reg.write("val2");
		assertRegisterValues(reg, "val2");
	}
	
	@Test
	public void testConcurrentCreate(){
		MVRegister<String> regA = createRegisterA();
		regA.write("valA");
		MVRegister<String> regB = createRegisterB();
		regB.write("valB");
		db1.store(modelA);
		db1.store(modelB);
		regA = loadRegisterA();
		assertRegisterValues(regA, "valA", "valB");
	}
	
	@Test
	public void testConcurrentCreateAndOverride(){
		testConcurrentCreate();
		MVRegister<String> regA = loadRegisterA();
		regA.write("newValue");
		db1.store(modelA);
		
		regA = loadRegisterA();
		assertRegisterValues(regA, "newValue");
	}
	
	

	
	
	

}
