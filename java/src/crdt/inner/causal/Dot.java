package crdt.inner.causal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dot {
	public Dot(String nodeId, int counter) {
		this.nodeId = nodeId;
		this.counter = counter;
	}
	@JsonProperty
	public final String nodeId;
	@JsonProperty
	public final int counter;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + counter;
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dot other = (Dot) obj;
		if (counter != other.counter)
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return nodeId+","+counter;
	}
	
	
	
}