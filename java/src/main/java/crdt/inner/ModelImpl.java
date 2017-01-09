package crdt.inner;

import com.fasterxml.jackson.annotation.JsonIgnore;

import crdt.api.Crdt;
import crdt.api.CrdtFactory;
import crdt.api.Model;
import crdt.inner.causal.Causal;
import crdt.inner.causal.CausalContext;

public class ModelImpl implements Model {
	private String key;
	private String nodeId;
	private CausalContext cc;
	private Crdt root;
	
	
	public ModelImpl(String nodeId, String key, Causal causal){
		this.key = key;
		this.nodeId = nodeId;
		this.cc = causal.getCc();
		this.root = causal.createCrdt(nodeId);
	}
	
	public ModelImpl(String nodeId, String key){
		this.cc = new CausalContext();
		this.key = key;
		this.nodeId = nodeId;
	}
	
	public String getKey(){
		return key;
	}

	public Causal getDelta(){
		return root.getDelta();
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
