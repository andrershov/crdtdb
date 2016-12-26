package crdt.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import crdt.inner.causal.CausalContext;
import crdt.inner.types.AWSetImpl;
import crdt.inner.types.DWFlagImpl;
import crdt.inner.types.EWFlagImpl;
import crdt.inner.types.ItemCRDT;
import crdt.inner.types.ItemsCRDT;
import crdt.inner.types.MVRegisterImpl;
import crdt.inner.types.PNCounterImpl;
import crdt.inner.types.RWSetImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ 
	@Type(value = MVRegisterImpl.class, name = "MVRegister"), 
	@Type(value = EWFlagImpl.class, name = "EWFlag"),
	@Type(value = DWFlagImpl.class, name = "DWFlag"),
	@Type(value = ItemCRDT.class, name = "ItemCRDT"),
	@Type(value = ItemsCRDT.class, name = "ItemsCRDT"),
	@Type(value = PNCounterImpl.class, name = "PNCounter"),
	@Type(value = AWSetImpl.class, name = "AWSet"),
	@Type(value = RWSetImpl.class, name = "RWSet")
})
public interface CRDT {
	public boolean join(CRDT that);

	public CRDT clone(CausalContext cc);

	public CRDT getDelta();
	
}
