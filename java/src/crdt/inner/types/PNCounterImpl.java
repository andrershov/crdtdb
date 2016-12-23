package crdt.inner.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.PNCounter;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotMap;

public class PNCounterImpl implements PNCounter {
	public static class Pair {
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
		
		public static Pair join(Pair p1, Pair p2){
			Pair p = new Pair();
			p.inc = Math.max(p1.inc, p2.inc);
			p.dec = Math.max(p1.dec, p2.dec);
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
	}
	
	@JsonProperty
	private DotMap<Pair> dotMap;
	@JsonIgnore
	private CausalContext cc;
	@JsonIgnore
	private PNCounterImpl delta;
	
	@JsonCreator
	public PNCounterImpl(@JsonProperty("cc") CausalContext cc, @JsonProperty("dotMap") DotMap<Pair> dotMap) {
		this.cc = cc;
		this.dotMap = dotMap;
	}

	public PNCounterImpl(CausalContext cc) {
		this(cc, new DotMap<>());
	}

	private PNCounterImpl incrementDelta(int count) {
		return updateDelta(Pair.inc(count));
	}
	
	private PNCounterImpl decrementDelta(int count) {
		return updateDelta(Pair.dec(count));
	}
	
	private PNCounterImpl createAndMergeDelta(DotMap<Pair> newDotMap, CausalContext newCC) {
		PNCounterImpl currentDelta =  new PNCounterImpl(newCC, newDotMap);
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}
		
		return currentDelta;
	}
	
	private PNCounterImpl updateDelta(Pair updatePair) {
		Dot dot = cc.current();
		Pair p = dotMap.get(dot);
		DotMap<Pair> newDotMap;
		if (p != null) {
			Pair newPair = p.add(updatePair);
			newDotMap = new DotMap<>(dot, newPair);
			return createAndMergeDelta(newDotMap, cc);
		} else {
			dot = cc.next();
			newDotMap = new DotMap<Pair>(dot, updatePair);
			return createAndMergeDelta(newDotMap, cc.addDot(dot));
		}
	}

	public void increment(int count){
		this.join(incrementDelta(count));
	}

	@Override
	public boolean join(CRDT that) {
		if (that == null) return false;
		if (!(that instanceof PNCounterImpl)) throw new RuntimeException("CRDT types do not match");
		PNCounterImpl thatCounter = (PNCounterImpl)that;
	
		if (dotMap.join(thatCounter.dotMap, cc, thatCounter.cc, Pair::join)){
			cc.join(thatCounter.cc);
			return true;
		}
		return false;
	}

	@Override
	public PNCounterImpl clone(CausalContext cc) {
		return new PNCounterImpl(cc, new DotMap<>(dotMap));
	}

	@Override
	@JsonIgnore
	public PNCounterImpl getDelta() {
		return delta;
	}

	@Override
	public void decrement(int count) {
		this.join(decrementDelta(count));
	}

	@Override
	public void reset() {
		this.join(resetDelta());
		
	}

	private PNCounterImpl resetDelta() {
		return createAndMergeDelta(new DotMap<>(), cc);
	}

	@Override
	public int value() {
		int sum = 0;
		for (Pair p: dotMap.values()){
			sum=sum+p.inc-p.dec;
		}
		return sum;
	}
}
