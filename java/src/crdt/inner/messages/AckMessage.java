package crdt.inner.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AckMessage implements CrdtMessage {
	@JsonProperty("counter")
	public int counter;
	
	@JsonCreator
	public AckMessage(@JsonProperty("counter") int counter){
		this.counter = counter;
	}
}
