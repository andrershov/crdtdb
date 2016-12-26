package crdt.inner.causal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CausalContext {
	@JsonProperty
	Set<Dot> dotSet;
	@JsonIgnore
	String nodeId;
	
	public CausalContext(CausalContext that, Set<Dot> dots) {
		this.dotSet = dots;
		this.nodeId = that.nodeId;
	}
	
	private CausalContext(){
		
	}
		
	public static CausalContext fromScratch(String nodeId){
		CausalContext cc = new CausalContext();
		cc.dotSet = new HashSet<>();
		cc.nodeId = nodeId;
		return cc;
	}
	
	@JsonCreator
	public static CausalContext fromMap(@JsonProperty("dotSet") Set<Dot> dotSet){
		CausalContext cc = new CausalContext();
		cc.dotSet = dotSet;
		return cc;
	}
	
	public static CausalContext fromExisting(CausalContext that){
		CausalContext cc = new CausalContext();
		cc.nodeId = that.nodeId;
		cc.dotSet = new HashSet<>(that.dotSet);
		return cc;
	}
	
	public static CausalContext fromExistingAndNewNodeId(CausalContext that, String nodeId){
		CausalContext cc = new CausalContext();
		cc.nodeId = nodeId;
		cc.dotSet = new HashSet<>(that.dotSet);
		return cc;
	}
	
	public boolean dotin(Dot dot) {
		return dotSet.contains(dot);
	}

	public Dot next() {
		Optional<Dot> maxDot = max();
		if (maxDot.isPresent()){
			return new Dot(nodeId, maxDot.get().counter+1);
		} else {
			return new Dot(nodeId, 1);
		}
	}
	
	public void addDot(Dot dot) {
		dotSet.add(dot);
	}


	public void join(CausalContext that) {
		this.dotSet.addAll(that.dotSet);
	}


	@Override
	public String toString() {
		return "CausalContext [dotSet=" + dotSet + ", nodeId=" + nodeId + "]";
	}

	
	public Optional<Dot> max() {
		return dotSet.stream().filter(dot -> dot.nodeId.equals(nodeId)).reduce((acc, dot) -> dot.counter > acc.counter ? dot : acc);
	}
	
	
}
