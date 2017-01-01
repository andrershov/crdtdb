
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import crdt.api.Model;
import crdt.api.types.EWFlag;

public class EWFlagTest extends FlagTest<EWFlag> {
	
	@Test
	public void testConcurrentCreate(){
		Model mA = createCrdt(db1, NODE_A);
		root(mA).enable();
		Model mB = createCrdt(db1, NODE_B);
		root(mB).disable();
		db1.store(mA);
		db1.store(mB);
		mA = loadCrdt(db1, NODE_A);
		assertTrue(root(mA).read());
	}
	
	
	@Override
	protected EWFlag createCrdt(Model model) {
		return model.factory().createEWFlag();
	}
}
