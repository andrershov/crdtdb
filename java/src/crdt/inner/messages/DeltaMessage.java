package crdt.inner.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.inner.ModelState;

public class DeltaMessage implements CrdtMessage {
	@JsonProperty("counter")
	public int counter;
	@JsonProperty("deltaInterval")
	public ModelState deltaInterval;

	@JsonCreator
	public DeltaMessage(@JsonProperty("deltaInterval") ModelState deltaInterval, @JsonProperty("counter") int counter) {
		this.deltaInterval = deltaInterval;
		this.counter = counter;
	}

}
