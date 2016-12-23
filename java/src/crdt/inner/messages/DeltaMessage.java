package crdt.inner.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.inner.ModelImpl;

public class DeltaMessage implements CrdtMessage {
	@JsonProperty("counter")
	public int counter;
	@JsonProperty("deltaInterval")
	public ModelImpl deltaInterval;

	@JsonCreator
	public DeltaMessage(@JsonProperty("deltaInterval") ModelImpl deltaInterval, @JsonProperty("counter") int counter) {
		this.deltaInterval = deltaInterval;
		this.counter = counter;
	}

}
