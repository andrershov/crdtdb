package crdt.inner;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import crdt.api.Crdt;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.DotStore;
import crdt.inner.types.AWMapState;
import crdt.inner.types.AWSetState;
import crdt.inner.types.DWFlagState;
import crdt.inner.types.EWFlagState;
import crdt.inner.types.MVRegisterState;
import crdt.inner.types.PNCounterState;
import crdt.inner.types.RWSetState;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ 
	@Type(value = MVRegisterState.class, name = "MVRegister"), 
	@Type(value = EWFlagState.class, name = "EWFlag"),
	@Type(value = DWFlagState.class, name = "DWFlag"),
	@Type(value = PNCounterState.class, name = "PNCounter"),
	@Type(value = AWSetState.class, name = "AWSet"),
	@Type(value = RWSetState.class, name = "RWSet"),
	@Type(value = AWMapState.class, name = "AWMap")
})
public interface CrdtState extends DotStore {
	Crdt createCrdt(String nodeId, CausalContext cc);
}
