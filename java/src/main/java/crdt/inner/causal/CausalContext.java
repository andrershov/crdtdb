package crdt.inner.causal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CausalContext {
	private Set<Dot> dotSet;
	
	public CausalContext(){
		this.dotSet = new HashSet<>();
	}
	
	
	public CausalContext(Set<Dot> dots, Dot newDot){
		this.dotSet = dots;
		this.dotSet.add(newDot);
	}
	
	public CausalContext(CausalContext that){
		this.dotSet = new HashSet<>(that.dotSet);
	}


	public boolean dotin(Dot dot) {
		return dotSet.contains(dot);
	}

	public Dot next(String nodeId) {
		Optional<Dot> maxDot = max(nodeId);
		if (maxDot.isPresent()){
			return new Dot(nodeId, maxDot.get().getCounter()+1);
		} else {
			return new Dot(nodeId, 1);
		}
	}
	

	public void join(CausalContext that) {
		this.dotSet.addAll(that.dotSet);
	}
	
	
	@Override
	public String toString() {
		return "CausalContext [dotSet=" + dotSet + "]";
	}

	public Optional<Dot> max(String nodeId) {
		return dotSet.stream().filter(dot -> dot.getNodeId().equals(nodeId)).reduce((acc, dot) -> dot.getCounter() > acc.getCounter() ? dot : acc);
	}
	
	//jackson section
	@JsonCreator
	public CausalContext(@JsonProperty("dotSet") Set<Dot> dots) {
		this.dotSet = dots;
	}
	
	@JsonProperty("dotSet")
	public Set<Dot> getDotSet() {
		return dotSet;
	}
	
	
}
