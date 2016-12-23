package crdt.inner.causal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DotSet {
	@JsonProperty("dotSet")
	private Set<Dot> dotSet;
	
	@JsonCreator
	public DotSet(@JsonProperty("dotSet") Set<Dot> dotSet){
		this.dotSet = dotSet;
	}
	
	public DotSet(DotSet that){
		this();
		dotSet.addAll(that.dotSet);
	}
	
	public DotSet(){
		dotSet = new HashSet<>();
	}
	
	public DotSet(Dot dot){
		this();
		dotSet.add(dot);
	}
	private Set<Dot> intersect(DotSet that){
		return dotSet.stream().filter(dot->that.dotSet.contains(dot)).collect(Collectors.toSet());
	}
	public void addDot(Dot dot){
		this.dotSet.add(dot);
	}
	
	public Set<Dot> minus(CausalContext cc) {
		Set<Dot> newSet = new HashSet<>();
		
		dotSet.forEach(dot ->{
			String nodeId = dot.nodeId;
			Integer ccCounter = cc.causalContext.get(nodeId);
		    if (ccCounter == null || ccCounter < dot.counter){
		    	newSet.add(dot);
		    }
		});
		return newSet;
	}
	public boolean join(DotSet that, CausalContext thisContext, CausalContext thatContext){
		Set<Dot> newDotset = this.intersect(that);
		newDotset.addAll(this.minus(thatContext));
		newDotset.addAll(that.minus(thisContext));
		if (this.dotSet.equals(newDotset)) return false;
		this.dotSet = newDotset;
		return true;
	}

	@JsonIgnore
	public boolean isEmpty() {
		return dotSet.isEmpty();
	}

	@Override
	public String toString() {
		return "DotSet [dotSet=" + dotSet + "]";
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
}
