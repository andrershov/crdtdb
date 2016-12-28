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
	@JsonProperty
	private String key;

	private ModelImpl() {
	}

	@JsonCreator
	public static ModelImpl fromCCandCrdt(@JsonProperty("cc") CausalContext cc, @JsonProperty("crdt") CRDT crdt) {
		ModelImpl m = new ModelImpl();
		m.crdt = crdt.clone(cc);
		m.cc = cc;
		return m;
	}

	public static ModelImpl fromScratch(String nodeId, String key) {
		ModelImpl m = new ModelImpl();
		m.cc = CausalContext.fromScratch(nodeId);
		m.key = key;
		return m;
	}

	public static ModelImpl fromExistingAndNewNodeId(String nodeId, ModelImpl that) {
		ModelImpl m = new ModelImpl();
		m.key = that.key;
		m.cc = CausalContext.fromExistingAndNewNodeId(that.cc, nodeId);
		m.crdt = that.getRoot().clone(m.cc);
		return m;
	}

	@JsonIgnore
	public <V extends CRDT> V getRoot() {
		return (V)crdt;
	}

	public CrdtFactory factory() {
		return new CrdtFactoryImpl(cc);
	}

	public void setRoot(CRDT crdt) {
		this.crdt = crdt;
	}

	@JsonIgnore
	public ModelImpl getDelta() {
		ModelImpl m = new ModelImpl();
		m.crdt = crdt.getDelta();
		m.cc = m.crdt.getCausalContext();
		m.key = key;
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
		return crdt.toString();
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String innerToString() {
		return "ModelImpl [crdt=" + crdt.innerToString() + ", cc=" + cc + ", key=" + key + "]";
	}
}
