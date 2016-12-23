package crdt.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import crdt.inner.causal.CausalContext;
import crdt.inner.types.DWFlagImpl;
import crdt.inner.types.EWFlagImpl;
import crdt.inner.types.MVRegisterImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ 
	@Type(value = MVRegisterImpl.class, name = "MVRegister"), 
	@Type(value = EWFlagImpl.class, name = "EWFlag"),
	@Type(value = DWFlagImpl.class, name = "DWFlag") 
})
public interface CRDT {
	public boolean join(CRDT that);

	public CRDT clone(CausalContext cc);

	public CRDT getDelta();
}
