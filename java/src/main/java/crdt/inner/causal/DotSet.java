package crdt.inner.causal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DotSet implements DotStore {
    private Set<Dot> dotSet;

    public DotSet() {
        dotSet = new HashSet<>();
    }

    public DotSet(Dot dot) {
        this();
        dotSet.add(dot);
    }

    private Set<Dot> intersect(DotSet that) {
        return dotSet.stream().filter(dot -> that.dotSet.contains(dot)).collect(Collectors.toSet());
    }

    public void addDot(Dot dot) {
        this.dotSet.add(dot);
    }

    public Set<Dot> minus(CausalContext cc) {
        Set<Dot> newSet = new HashSet<>();
        newSet.addAll(dotSet);
        newSet.removeAll(cc.getDotSet());
        return newSet;
    }

    public boolean join(DotStore thatStore, CausalContext thisContext, CausalContext thatContext) {
        if (!thatStore.getClass().equals(this.getClass())) {
            throw new RuntimeException(String.format("Invalid type. This class %s, that class %s", this.getClass(), thatStore.getClass()));
        }
        DotSet that = (DotSet) thatStore;
        Set<Dot> newDotset = this.intersect(that);
        newDotset.addAll(this.minus(thatContext));
        newDotset.addAll(that.minus(thisContext));
        if (this.dotSet.equals(newDotset)) return false;

        this.dotSet = newDotset;
        return true;
    }

    @Override
    public DotSet copy() {
        DotSet that = createEmpty();
        that.dotSet.addAll(this.dotSet);
        return that;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [dotSet=" + dotSet + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dotSet == null) ? 0 : dotSet.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DotSet other = (DotSet) obj;
        if (dotSet == null) {
            if (other.dotSet != null)
                return false;
        } else if (!dotSet.equals(other.dotSet))
            return false;
        return true;
    }


    @Override
    public DotSet createEmpty() {
        return new DotSet();
    }


    @Override
    public Set<Dot> dots() {
        return new HashSet<>(dotSet);
    }

    //jackson section
    @JsonProperty("dotSet")
    public Set<Dot> getDotSet() {
        return dotSet;
    }

    @JsonCreator
    public DotSet(@JsonProperty("dotSet") Set<Dot> dotSet) {
        this.dotSet = dotSet;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return dotSet.isEmpty();
    }
}
