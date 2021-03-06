package crdt.inner.causal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import crdt.inner.serializers.DotDeserializer;

import java.util.*;

public class DotFun<V> implements DotStore {
    private Map<Dot, V> dotFun;


    protected DotFun() {
        dotFun = new HashMap<>();
    }


    protected DotFun(Dot dot, V value) {
        this();
        dotFun.put(dot, value);
    }


    @SuppressWarnings("unchecked")
    private Map<Dot, V> intersect(DotFun<V> that) {
        Map<Dot, V> newMap = new HashMap<>();
        dotFun.forEach((dot, thisValue) -> {
            V thatValue = that.dotFun.get(dot);
            if (thatValue != null) {
                V newValue;
                if (thisValue instanceof Lattice && thatValue instanceof Lattice) {
                    newValue = (V) ((Lattice) thisValue).join((Lattice) thatValue);
                } else {
                    if (thisValue.equals(thatValue)) {
                        newValue = thisValue;
                    } else {
                        throw new RuntimeException("DotFun values should either implement Lattice interface or be equal for same dot: thisVal = " + thisValue + ", thatValue=" + thatValue);
                    }
                }


                newMap.put(dot, newValue);
            }
        });

        return newMap;
    }


    public Map<Dot, V> minus(CausalContext cc) {
        Map<Dot, V> map = new HashMap<>();
        map.putAll(this.dotFun);
        map.keySet().removeAll(cc.getDotSet());
        return map;
    }

    @SuppressWarnings("unchecked")
    public boolean join(DotStore thatDotStore, CausalContext thisContext, CausalContext thatContext) {
        if (!thatDotStore.getClass().equals(this.getClass())) {
            throw new RuntimeException(String.format("Invalid type. This class %s, that class %s", this.getClass(), thatDotStore.getClass()));
        }
        DotFun<V> that = (DotFun<V>) thatDotStore;
        Map<Dot, V> newDotFun = this.intersect(that);
        newDotFun.putAll(this.minus(thatContext));
        newDotFun.putAll(that.minus(thisContext));
        if (this.dotFun.equals(newDotFun)) return false;
        this.dotFun = newDotFun;
        return true;
    }

    @Override
    public DotFun<V> copy() {
        DotFun<V> that = createEmpty();
        that.dotFun.putAll(this.dotFun);
        return that;
    }

    public Collection<V> values() {
        return dotFun.values();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [dotFun=" + dotFun + "]";
    }

    public V get(Dot dot) {
        return dotFun.get(dot);
    }

    @Override
    public DotFun<V> createEmpty() {
        return new DotFun<>();
    }

    @Override
    public Set<Dot> dots() {
        return new HashSet<>(dotFun.keySet());
    }

    //jackson section
    //Key deserializer does not work in conjuction with @JsonCreate, that's why default constructor and setter are used
    @JsonProperty("dotFun")
    @JsonDeserialize(keyUsing = DotDeserializer.class)
    public void setDotFun(Map<Dot, V> dotFun) {
        this.dotFun = dotFun;
    }

    @JsonProperty("dotFun")
    public Map<Dot, V> getDotFun() {
        return dotFun;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return dotFun.isEmpty();
    }
}
