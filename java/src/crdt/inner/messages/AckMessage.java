package crdt.inner.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AckMessage implements CrdtMessage {
	@JsonProperty("key")
	public String key;
	@JsonProperty("counter")
	public int counter;
	
	@JsonCreator
	public AckMessage(@JsonProperty("key") String key, @JsonProperty("counter") int counter){
		this.key = key;
		this.counter = counter;
	}
}
