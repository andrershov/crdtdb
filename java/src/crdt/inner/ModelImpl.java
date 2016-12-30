package crdt.inner;

import com.fasterxml.jackson.annotation.JsonIgnore;

import crdt.api.Crdt;
import crdt.api.CrdtFactory;
import crdt.api.Model;
import crdt.inner.causal.CausalContext;

public class ModelImpl implements Model {
	private Crdt root;
	private CausalContext cc;
	private String key;
	private String nodeId;
	
	
	public ModelImpl(String nodeId, ModelState state){
		this.key = state.getKey();
		this.nodeId = nodeId;
		this.cc = new CausalContext(state.getCc());
		this.root = state.getCrdtState().createCrdt(nodeId, cc);
	}
	
	public ModelImpl(String nodeId, String key){
		this.cc = new CausalContext();
		this.key = key;
		this.nodeId = nodeId;
	}
	

	public ModelState getDelta(){
		return new ModelState(key, root.getDelta(), root.getCausalContextDelta());
	}

	@JsonIgnore
	public <V extends Crdt> V getRoot() {
		return (V)root;
	}

	public CrdtFactory factory() {
		return new CrdtFactoryImpl(nodeId, cc);
	}

	public void setRoot(Crdt root) {
		this.root = root;
	}
}
