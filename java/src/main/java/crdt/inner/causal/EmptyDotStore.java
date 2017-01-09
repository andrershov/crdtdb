package crdt.inner.causal;

import java.util.HashSet;
import java.util.Set;

public class EmptyDotStore implements DotStore {

    @Override
    public boolean join(DotStore dotStore, CausalContext thisContext, CausalContext thatContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public DotStore copy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DotStore createEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Dot> dots() {
        return new HashSet<>();
    }

}
