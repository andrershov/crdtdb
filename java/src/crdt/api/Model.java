package crdt.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import crdt.inner.ModelImpl;

@JsonSubTypes({ 
	@Type(value = ModelImpl.class), 
})
public interface Model {
	CRDT getRoot();
	void setRoot(CRDT root);
	CrdtFactory factory();
}
