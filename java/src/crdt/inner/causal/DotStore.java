package crdt.inner.causal;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ 
	@Type(value = DotSet.class, name = "DotSet"), 
	@Type(value = DotFun.class, name = "DotFun"),
	@Type(value = DotMap.class, name = "DotMap")
})
public interface DotStore {
	public boolean join(DotStore dotStore, CausalContext thisContext, CausalContext thatContext);
	public boolean isEmpty();
	public DotStore copy();
	public DotStore createEmpty();
	public Set<Dot> dots();
}
