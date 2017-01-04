package crdt.inner.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AckMessage implements CrdtMessage {
	private String key;
	private int counter;
	
	@JsonCreator
	public AckMessage(@JsonProperty("key") String key, @JsonProperty("counter") int counter){
		this.key = key;
		this.counter = counter;
	}

	@JsonProperty("key")
	public String getKey() {
		return key;
	}

	@JsonProperty("counter")
	public int getCounter() {
		return counter;
	}
	
	
}
