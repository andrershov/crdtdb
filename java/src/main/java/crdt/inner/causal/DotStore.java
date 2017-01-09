package crdt.inner.causal;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import crdt.inner.types.AWMapState;
import crdt.inner.types.AWSetState;
import crdt.inner.types.DWFlagState;
import crdt.inner.types.EWFlagState;
import crdt.inner.types.MVRegisterState;
import crdt.inner.types.PNCounterState;
import crdt.inner.types.RWSetState;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ 
	@Type(value = DotSet.class, name = "DotSet"), 
	@Type(value = DotFun.class, name = "DotFun"),
	@Type(value = DotMap.class, name = "DotMap"),
	
	//Types below are subclasses of CrdtState sub-interface, but we need to declare them here for AW/RWMap to work properly.
	@Type(value = MVRegisterState.class, name = "MVRegister"), 
	@Type(value = EWFlagState.class, name = "EWFlag"),
	@Type(value = DWFlagState.class, name = "DWFlag"),
	@Type(value = PNCounterState.class, name = "PNCounter"),
	@Type(value = AWSetState.class, name = "AWSet"),
	@Type(value = RWSetState.class, name = "RWSet"),
	@Type(value = AWMapState.class, name = "AWMap")
})
public interface DotStore {
	public boolean join(DotStore dotStore, CausalContext thisContext, CausalContext thatContext);
	public boolean isEmpty();
	public DotStore copy();
	public DotStore createEmpty();
	public Set<Dot> dots();
}
