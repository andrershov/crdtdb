package crdt.inner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.Crdt;
import crdt.inner.causal.CausalContext;

public class ModelState {
	private CausalContext cc;
	private CrdtState crdtState;
	private String key;
	
	@JsonCreator
	public ModelState(@JsonProperty("key") String key, @JsonProperty("crdtState") CrdtState crdtState, @JsonProperty("cc") CausalContext cc) {
		this.key = key;
		this.crdtState = crdtState;
		this.cc = cc;
	}

	public boolean join(ModelState that){
		if (this.crdtState.join(that.crdtState, this.cc, that.cc)){
			this.cc.join(that.cc);
			return true;
		}
		return false;
	}
	
	@JsonProperty("cc")
	public CausalContext getCc() {
		return cc;
	}
	
	@JsonProperty("crdtState")
	public CrdtState getCrdtState() {
		return crdtState;
	}

	@JsonProperty("key")
	public String getKey() {
		return key;
	}
}