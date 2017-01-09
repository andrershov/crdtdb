package crdt.inner.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "msg")
@JsonSubTypes({
        @Type(value = DeltaMessage.class, name = "delta"),
        @Type(value = AckMessage.class, name = "ack")
})
public interface CrdtMessage {

}
