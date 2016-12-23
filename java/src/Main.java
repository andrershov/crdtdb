import java.io.IOException;
import java.util.Arrays;

import crdt.api.*;
import crdt.api.types.DWFlag;
import crdt.api.types.EWFlag;
import crdt.api.types.MVRegister;
import crdt.inner.CrdtDbImpl;
import crdt.inner.DeltaStorage;
import crdt.inner.conn.LocalNodeJsonConnections;
import crdt.inner.types.EWFlagImpl;
import crdt.inner.types.ItemCRDT;
import crdt.inner.types.ItemsCRDT;
import crdt.inner.types.MVRegisterImpl;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		// testFlags();
		// testMVReg();

		//testReplication();
		
		testItems();
	}
	
	private static ItemCRDT createItem(Model model, String str, boolean enabled){
		ItemCRDT item = new ItemCRDT();
		MVRegister<String> text = model.factory().createMVRegister();
		text.write(str);
		item.text = (MVRegisterImpl<String>) text;
		
		EWFlag done = model.factory().createEWFlag();
		if (enabled) {
			done.enable();
		} else {
			done.disable();
		}
		item.done = (EWFlagImpl) done;
		return item;
	}
	

	private static void testItems() throws InterruptedException {
		LocalNodeJsonConnections conn12 = new LocalNodeJsonConnections("node1", "node2");
		CrdtDbImpl db1 = new CrdtDbImpl(conn12.getConn1());
		CrdtDbImpl db2 = new CrdtDbImpl(conn12.getConn2());
		
		conn12.breakConn();
		
		Model modelA = db1.load("node1", "reg");
		ItemCRDT item1 = createItem(modelA, "item1_1", true);
		ItemCRDT item2 = createItem(modelA, "item2_1", false);
		ItemsCRDT items = new ItemsCRDT();
		items.item1 = item1;
		items.item2 = item2;
		modelA.setRoot(items);
		db1.store("reg", modelA);
		
		Model modelB= db2.load("node2", "reg");
		ItemCRDT item1B = createItem(modelB, "item1_2", false);
		ItemCRDT item2B = createItem(modelB, "item2_2", true);
		ItemsCRDT itemsB = new ItemsCRDT();
		itemsB.item1 = item1B;
		itemsB.item2 = item2B;
		modelB.setRoot(itemsB);
		db2.store("reg", modelB);
		
		conn12.fixConn();
		
		Thread.sleep(1000L);
		
		modelA = db1.load("node1", "reg");
		items = (ItemsCRDT) modelA.getRoot();
		items.item1.done.disable();
		System.out.println(items);
		
		db1.store("reg", modelA);
		
		
		
		Thread.sleep(100L);
		modelB = db2.load("node2", "reg");
		itemsB = (ItemsCRDT) modelB.getRoot();
		
		System.out.println(itemsB);
		
	}

	private static void testReplication() throws InterruptedException {

		LocalNodeJsonConnections conn12 = new LocalNodeJsonConnections("node1", "node2");
		LocalNodeJsonConnections conn13 = new LocalNodeJsonConnections("node1", "node3");
		LocalNodeJsonConnections conn23 = new LocalNodeJsonConnections("node2", "node3");
		conn12.breakConn();
		conn13.breakConn();
		conn23.breakConn();

		CrdtDbImpl db1 = new CrdtDbImpl(Arrays.asList(conn12.getConn1(), conn13.getConn1()));
		CrdtDbImpl db2 = new CrdtDbImpl(Arrays.asList(conn12.getConn2(), conn23.getConn1()));
		CrdtDbImpl db3 = new CrdtDbImpl(Arrays.asList(conn13.getConn2(), conn23.getConn2()));

		Model modelA = db1.load("node1", "reg");
		MVRegister<String> regA = (MVRegister<String>) modelA.getRoot();
		regA = modelA.factory().createMVRegister();
		modelA.setRoot(regA);
		regA.write("node1_1");
		db1.store("reg", modelA);
		Thread.sleep(100);

		Model modelB = db2.load("node2", "reg");
		MVRegister<String> regB = (MVRegister<String>) modelB.getRoot();
		regB = modelB.factory().createMVRegister();
		modelB.setRoot(regB);
		regB.write("node2_1");
		db2.store("reg", modelB);
		Thread.sleep(100);

		Model modelC = db3.load("node3", "reg");
		MVRegister<String> regC = (MVRegister<String>) modelC.getRoot();
		regC = modelC.factory().createMVRegister();
		modelC.setRoot(regC);
		regC.write("node3_1");
		db3.store("reg", modelC);
		Thread.sleep(100);

		conn13.fixConn();

		Thread.sleep(100);

		modelC = db3.load("node3", "reg");
		regC = (MVRegister<String>) modelC.getRoot();
		System.out.println(regC.values());

		conn23.fixConn();

		Thread.sleep(100);

		modelC = db3.load("node3", "reg");
		regC = (MVRegister<String>) modelC.getRoot();
		System.out.println(regC.values());

		Thread.sleep(10000000L);
	}

	@SuppressWarnings("unchecked")
	private static void testMVReg() throws IOException {
		CrdtDb db = new CrdtDbImpl();
		Model modelA = db.load("nodeA", "reg");

		MVRegister<String> regA = (MVRegister<String>) modelA.getRoot();
		regA = modelA.factory().createMVRegister();
		modelA.setRoot(regA);
		regA.write("A1");
		db.store("reg", modelA);

		modelA = db.load("nodeA", "reg");
		regA = (MVRegister<String>) modelA.getRoot();
		regA.write("A2");

		Model modelB = db.load("nodeB", "reg");
		MVRegister<String> regB = (MVRegister<String>) modelB.getRoot();
		regB.write("B1");

		Model modelC = db.load("nodeC", "reg");
		MVRegister<String> regC = (MVRegister<String>) modelC.getRoot();
		regC.write("C1");

		db.store("reg", modelA);
		db.store("reg", modelB);
		db.store("reg", modelC);

		modelA = db.load("nodeA", "reg");
		regA = (MVRegister<String>) modelA.getRoot();
		regA.write("A2");
		db.store("reg", modelA);

		modelA = db.load("nodeA", "reg");
		regA = (MVRegister<String>) modelA.getRoot();
		regA.write("A3");

		System.out.println(regA.getDelta());

		db.store("reg", modelA);

		DeltaStorage ds = db.getDeltaStorage();
		// System.out.println(ds.getDeltaInterval());
	}

	public static void testFlags() {
		CrdtDb db = new CrdtDbImpl();
		Model modelA = db.load("nodeA", "flag");

		DWFlag flagA = (DWFlag) modelA.getRoot();

		if (flagA == null) {
			flagA = modelA.factory().createDWFlag();
			modelA.setRoot(flagA);
		}

		flagA.enable();

		db.store("flag", modelA); // Flag is initially enabled

		modelA = db.load("nodeA", "flag");
		flagA = (DWFlag) modelA.getRoot();
		flagA.disable();

		Model modelB = db.load("nodeB", "flag");
		DWFlag flagB = (DWFlag) modelB.getRoot();
		flagB.disable();
		flagB.enable();

		db.store("flag", modelA); // now flag is disabled

		db.store("flag", modelB); // flag was concurrently modified by B, it was
									// enabled on B. And enabled wins => now
									// flag is enabled

		modelA = db.load("nodeA", "flag");
		flagA = (DWFlag) modelA.getRoot();
		System.out.println(flagA.read());

		modelA = db.load("nodeA", "concurrentFlag");
		flagA = modelA.factory().createDWFlag();
		modelA.setRoot(flagA);
		flagA.disable();

		modelB = db.load("nodeB", "concurrentFlag");
		flagB = modelB.factory().createDWFlag();
		modelB.setRoot(flagB);
		flagB.enable();

		db.store("concurrentFlag", modelA);
		db.store("concurrentFlag", modelB);

		modelA = db.load("nodeA", "concurrentFlag");
		flagA = (DWFlag) modelA.getRoot();
		System.out.println(flagA.read());
	}

}
