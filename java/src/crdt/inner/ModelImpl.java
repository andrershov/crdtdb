package crdt.inner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.CrdtFactory;
import crdt.api.Model;
import crdt.inner.causal.CausalContext;

public class ModelImpl implements Model {
	@JsonProperty
	private CRDT crdt;
	@JsonProperty
	private CausalContext cc;
	
	private ModelImpl() {
	}
	
	public static ModelImpl fromScratch(String nodeId){
		ModelImpl m = new ModelImpl();
		m.cc = CausalContext.fromScratch(nodeId);
		return m;
	}
	
	public static ModelImpl fromExistingAndNewNodeId(String nodeId, ModelImpl that){
		ModelImpl m = new ModelImpl();
		m.cc =CausalContext.fromExistingAndNewNodeId(that.cc, nodeId);
		m.crdt = that.getRoot().clone(m.cc);
		return m;
	}
	
	@JsonCreator
	public static ModelImpl fromCCandCrdt(@JsonProperty("cc") CausalContext cc, @JsonProperty("crdt") CRDT crdt){
		ModelImpl m = new ModelImpl();
		m.crdt = crdt.clone(cc);
		m.cc = cc;
		return m;
	}
	
	
	public ModelImpl(CausalContext cc, CRDT crdt) {
		this.cc = cc;
		this.crdt = crdt;
	}

	@JsonIgnore
	public CRDT getRoot(){
		return crdt;
	}
	
	public CrdtFactory factory(){
		return new CrdtFactoryImpl(cc);
	}

	public void setRoot(CRDT crdt) {
		this.crdt = crdt;
	}
	
	@JsonIgnore
	public ModelImpl getDelta(){
		ModelImpl m = new ModelImpl();
		m.cc = cc;
		m.crdt = crdt.getDelta();
		return m;
	}

	
	public boolean joinDelta(ModelImpl delta) {
		if (crdt.join(delta.getRoot())) {
			cc.join(delta.cc);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Model [crdt=" + crdt + ", cc=" + cc + "]";
	}
}
