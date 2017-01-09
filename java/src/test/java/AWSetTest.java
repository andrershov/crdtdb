import crdt.api.Model;
import crdt.api.types.AWSet;
import org.junit.Test;

public class AWSetTest extends SetTest<AWSet<String>> {
    @Test
    public void testAddWins() {
        Model mA = createCrdt(db1, NODE_A);
        root(mA).add("e1");
        Model mB = createCrdt(db1, NODE_B);
        root(mB).remove("e1");
        db1.store(mA);
        db1.store(mB);
        mA = loadCrdt(db1, NODE_A);
        assertSetValues(root(mA), "e1");
    }


    @Override
    protected AWSet<String> createCrdt(Model model) {
        return model.factory().createAWSet();
    }
}
