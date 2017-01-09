package crdt.inner.causal;

import java.util.function.BiFunction;

public interface JoinFunction<V> extends BiFunction<V, V, V> {
}
