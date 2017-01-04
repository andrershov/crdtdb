package crdt.inner.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.inner.causal.Causal;

public class DeltaMessage implements CrdtMessage {
	@JsonProperty("counter")
	public int counter;
	@JsonProperty("key")
	public String key;
	@JsonProperty("deltaInterval")
	public Causal deltaInterval;

	@JsonCreator
	public DeltaMessage(@JsonProperty("key") String key, @JsonProperty("deltaInterval") Causal deltaInterval, @JsonProperty("counter") int counter) {
		this.key = key;
		this.deltaInterval = deltaInterval;
		this.counter = counter;
	}

}
