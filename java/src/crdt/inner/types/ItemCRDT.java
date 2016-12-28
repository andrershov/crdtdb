package crdt.inner.types;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import crdt.inner.causal.CausalContext;


@JsonInclude(Include.NON_NULL)
public class ItemCRDT extends ObjectCRDT<ItemCRDT> {
	@JsonProperty("text")
	public MVRegisterImpl<String> text;
	@JsonProperty("done")
	public EWFlagImpl done;
	
	
	@Override
	public String toString() {
		return "ItemCRDT [done = "+done.read()+", text = "+text.values()+"]";
	}
	@Override
	public CausalContext getCausalContext() {
		return null;
	}
	@Override
	public String innerToString() {
		return "ItemCRDT [done = "+done.read()+", text = "+text.values()+"]";
	}
	
}
