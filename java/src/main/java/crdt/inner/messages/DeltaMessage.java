package crdt.inner.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import crdt.inner.causal.Causal;

public class DeltaMessage implements CrdtMessage {
    private final int counter;
    private final String key;
    private final Causal deltaInterval;

    @JsonCreator
    public DeltaMessage(@JsonProperty("key") String key, @JsonProperty("deltaInterval") Causal deltaInterval, @JsonProperty("counter") int counter) {
        this.key = key;
        this.deltaInterval = deltaInterval;
        this.counter = counter;
    }

    @JsonProperty("counter")
    public int getCounter() {
        return counter;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("deltaInterval")
    public Causal getDeltaInterval() {
        return deltaInterval;
    }


}
