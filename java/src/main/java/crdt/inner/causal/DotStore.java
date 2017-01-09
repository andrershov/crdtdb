package crdt.inner.causal;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import crdt.inner.types.*;

import java.util.Set;


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
    boolean join(DotStore dotStore, CausalContext thisContext, CausalContext thatContext);

    boolean isEmpty();

    DotStore copy();

    DotStore createEmpty();

    Set<Dot> dots();
}
