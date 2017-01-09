import crdt.api.Model;
import crdt.api.types.DWFlag;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class DWFlagTest extends FlagTest<DWFlag> {

    @Test
    public void testConcurrentCreate() {
        Model mA = createCrdt(db1, NODE_A);
        root(mA).enable();
        Model mB = createCrdt(db1, NODE_B);
        root(mB).disable();
        db1.store(mA);
        db1.store(mB);
        mA = loadCrdt(db1, NODE_A);
        assertFalse(root(mA).read());
    }


    @Override
    protected DWFlag createCrdt(Model model) {
        return model.factory().createDWFlag();
    }
}
