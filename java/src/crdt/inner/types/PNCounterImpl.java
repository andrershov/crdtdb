package crdt.inner.types;

import java.util.Collections;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.PNCounter;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotFun;
import crdt.inner.causal.Lattice;

public class PNCounterImpl implements PNCounter {
	public static class Pair implements Lattice {
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
	
	@JsonProperty
	private DotFun<Pair> dotMap;
	@JsonIgnore
	private CausalContext cc;
	@JsonIgnore
	private PNCounterImpl delta;
	
	private PNCounterImpl(CausalContext cc, DotFun<Pair> dotMap) {
		this.cc = cc;
		this.dotMap = dotMap;
	}
	
	@JsonCreator
	public PNCounterImpl(@JsonProperty("dotMap") DotFun<Pair> dotMap) {
		this.dotMap = dotMap;
	}

	public PNCounterImpl(CausalContext cc) {
		this(cc, new DotFun<>());
	}

	private PNCounterImpl incrementDelta(int count) {
		return updateDelta(Pair.inc(count));
	}
	
	private PNCounterImpl decrementDelta(int count) {
		return updateDelta(Pair.dec(count));
	}
	
	private PNCounterImpl createAndMergeDelta(DotFun<Pair> newDotMap, CausalContext newCC) {
		PNCounterImpl currentDelta =  new PNCounterImpl(newCC, newDotMap);
		if (delta != null) {
			delta.join(currentDelta);
		} else {
			delta = currentDelta;
		}
		
		return currentDelta;
	}
	
	private PNCounterImpl updateDelta(Pair updatePair) {
		Dot dot = cc.max().orElse(cc.next());
		Pair p = dotMap.get(dot);
		DotFun<Pair> newDotMap;
		CausalContext newCC;
		if (p != null) {
			Pair newPair = p.add(updatePair);
			newDotMap = new DotFun<>(dot, newPair);
			newCC = new CausalContext(cc, newDotMap.dots());
			return createAndMergeDelta(newDotMap, cc);
		} else {
			dot = cc.next();
			newDotMap = new DotFun<Pair>(dot, updatePair);
			HashSet<Dot> dots = new HashSet<>();
			dots.add(dot);
			newCC = new CausalContext(cc, dots);
			return createAndMergeDelta(newDotMap, newCC);
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
	
		if (dotMap.join(thatCounter.dotMap, cc, thatCounter.cc)){
			cc.join(thatCounter.cc);
			return true;
		}
		return false;
	}

	@Override
	public PNCounterImpl clone(CausalContext cc) {
		return new PNCounterImpl(cc, dotMap.copy());
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
		return createAndMergeDelta(new DotFun<>(), cc);
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
