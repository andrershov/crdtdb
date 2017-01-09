package crdt.inner.causal;

public interface Lattice {
    Lattice join(Lattice that);
}
