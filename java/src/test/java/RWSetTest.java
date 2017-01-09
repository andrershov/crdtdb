import crdt.api.Model;
import crdt.api.types.RWSet;
import org.junit.Test;

public class RWSetTest extends SetTest<RWSet<String>> {
    @Test
    public void testRemoveWins() {
        Model mA = createCrdt(db1, NODE_A);
        root(mA).add("e1");
        Model mB = createCrdt(db1, NODE_B);
        root(mB).remove("e1");
        db1.store(mA);
        db1.store(mB);
        mA = loadCrdt(db1, NODE_A);
        assertSetValues(root(mA));
    }


    @Override
    protected RWSet<String> createCrdt(Model model) {
        return model.factory().createRWSet();
    }
}
