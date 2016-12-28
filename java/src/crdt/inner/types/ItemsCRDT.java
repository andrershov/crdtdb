package crdt.inner.types;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import crdt.inner.causal.CausalContext;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class ItemsCRDT extends ObjectCRDT<ItemsCRDT> {
	@JsonProperty("item1")
	public ItemCRDT item1;
	@JsonProperty("item2")
	public ItemCRDT item2;
	@Override
	public String toString() {
		return "ItemsCRDT [item1=" + item1 + ", item2=" + item2 + "]";
	}
	
	@Override
	public CausalContext getCausalContext() {
		return null;
	}

	@Override
	public String innerToString() {
		return "ItemsCRDT [item1=" + item1 + ", item2=" + item2 + "]";
	}
	
	
}
