package crdt.inner.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.inner.causal.Lattice;

public class Pair implements Lattice {
	@JsonProperty("inc")
	int inc;
	@JsonProperty("dec")
	int dec;
	
	public static Pair inc(int count) {
		Pair p = new Pair();
		p.inc = count;
		return p;
	}
	
	public static Pair dec(int count) {
		Pair p = new Pair();
		p.dec = count;
		return p;
	}

	public Pair add(Pair that) {
		Pair p = new Pair();
		p.inc = this.inc + that.inc;
		p.dec = this.dec + that.dec;
		return p;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dec;
		result = prime * result + inc;
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
		Pair other = (Pair) obj;
		if (dec != other.dec)
			return false;
		if (inc != other.inc)
			return false;
		return true;
	}

	@Override
	public Lattice join(Lattice thatL) {
		Pair that = (Pair)thatL;
		Pair p = new Pair();
		p.inc = Math.max(this.inc, that.inc);
		p.dec = Math.max(this.dec, that.dec);
		return p;
	}
}