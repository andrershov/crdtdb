package crdt.inner.causal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CausalContext {
	@JsonProperty
	Map<String, Integer> causalContext;
	@JsonIgnore
	String nodeId;
	
	public static CausalContext fromScratch(String nodeId){
		CausalContext cc = new CausalContext();
		cc.causalContext = new HashMap<>();
		cc.nodeId = nodeId;
		return cc;
	}
	
	@JsonCreator
	public static CausalContext fromMap(@JsonProperty("causalContext") Map<String, Integer> causalContext){
		CausalContext cc = new CausalContext();
		cc.causalContext = causalContext;
		return cc;
	}
	
	public static CausalContext fromExisting(CausalContext that){
		CausalContext cc = new CausalContext();
		cc.nodeId = that.nodeId;
		cc.causalContext = new HashMap<>(that.causalContext);
		return cc;
	}
	
	public static CausalContext fromExistingAndNewNodeId(CausalContext that, String nodeId){
		CausalContext cc = new CausalContext();
		cc.nodeId = nodeId;
		cc.causalContext = new HashMap<>(that.causalContext);
		return cc;
	}
	
	public boolean dotin(Dot dot) {
		String nodeId = dot.nodeId;
		if (causalContext.get(nodeId) >= dot.counter) {
			return true;
		}
		return false;
	}

	public Dot next() {
		Integer counter = causalContext.get(nodeId);
		if (counter == null)
			counter = 0;
		return new Dot(nodeId, counter + 1);
	}

	public CausalContext addDot(Dot dot) {
		CausalContext newCausalContext = fromExisting(this);
		newCausalContext.causalContext.put(dot.nodeId, dot.counter);
		return newCausalContext;
	}

	private static int max(Integer a, Integer b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return Math.max(a, b);
	}

	public void join(CausalContext that) {
		Set<String> nodeIdsSet = new HashSet<>(causalContext.keySet());
		nodeIdsSet.addAll(that.causalContext.keySet());
		Map<String, Integer> newCausalContext = new HashMap<>();
		nodeIdsSet.forEach(nodeId -> {
			newCausalContext.put(nodeId,
					max(this.causalContext.get(nodeId), that.causalContext.get(nodeId)));
		});
		causalContext = newCausalContext;
	}

	@Override
	public String toString() {
		return "CausalContext [causalContext=" + causalContext + ", nodeId=" + nodeId + "]";
	}

	public boolean contains(CausalContext that) {
		Set<String> nodeIdsSet = new HashSet<>(causalContext.keySet());
		nodeIdsSet.addAll(that.causalContext.keySet());
		for (String nodeId: nodeIdsSet){
			Integer thisCounter = this.causalContext.get(nodeId);
			Integer thatCounter = that.causalContext.get(nodeId);
			if (thisCounter==null || (thatCounter!=null && thatCounter > thisCounter)){
				return false;
			}
		}
		return true;
	}
	
	
}
