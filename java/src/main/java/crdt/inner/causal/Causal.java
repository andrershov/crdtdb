package crdt.inner.causal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import crdt.api.Crdt;
import crdt.inner.CrdtState;

public class Causal {
    private final CausalContext cc;
    private final CrdtState state;


    public Causal(Causal that) {
        this.cc = new CausalContext(that.cc);
        this.state = (CrdtState) that.state.copy();
    }


    public boolean join(Causal that) {
        if (state.join(that.state, cc, that.cc)) {
            this.cc.join(that.cc);
            return true;
        }
        return false;
    }


    public Crdt createCrdt(String nodeId) {
        return state.createCrdt(nodeId, cc);
    }

    //jackson section
    @JsonCreator
    public Causal(@JsonProperty("cc") CausalContext cc, @JsonProperty("state") CrdtState state) {
        this.cc = cc;
        this.state = state;
    }

    @JsonProperty("cc")
    public CausalContext getCc() {
        return cc;
    }


    @JsonProperty("state")
    public CrdtState getState() {
        return state;
    }


}
