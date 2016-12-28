package crdt.inner.types;

import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import crdt.api.CRDT;
import crdt.api.types.PNCounter;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;
import crdt.inner.causal.DotFun;
import crdt.inner.causal.DotMap;
import crdt.inner.types.abstr.DotFunCrdt;
import crdt.inner.types.abstr.DotMapCrdt;

public class PNCounterImpl extends DotFunCrdt<Pair> implements PNCounter {
	protected PNCounterImpl(CausalContext cc, DotFun<Pair> dotFun) {
		super(cc, dotFun);
	}
	
	public PNCounterImpl(CausalContext cc) {
		super(cc);
	}

	
	@JsonCreator
	public PNCounterImpl(@JsonProperty("dotFun") DotFun<Pair> dotFun) {
		super(dotFun);
	}

	
	private DotFunCrdt<Pair> incrementDelta(int count) {
		return updateDelta(Pair.inc(count));
	}
	
	private DotFunCrdt<Pair> decrementDelta(int count) {
		return updateDelta(Pair.dec(count));
	}
	
	
	private DotFunCrdt<Pair> updateDelta(Pair updatePair) {
		Dot dot = cc.max().orElse(cc.next());
		Pair p = dotFun.get(dot);
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
		
		return join(thatCounter);
	}

	@Override
	public void decrement(int count) {
		this.join(decrementDelta(count));
	}

	@Override
	public void reset() {
		this.join(resetDelta());
		
	}

	private DotFunCrdt<Pair> resetDelta() {
		return createAndMergeDelta(new DotFun<>(), cc);
	}

	@Override
	public int value() {
		int sum = 0;
		for (Pair p: dotFun.values()){
			sum=sum+p.inc-p.dec;
		}
		return sum;
	}
	
	
	@Override
	public String innerToString() {
		return "PNCounterImpl [dotFun=" + dotFun + ", cc=" + cc + ", delta=" + delta + "]";
	}

	@Override
	public String toString() {
		return Integer.toString(value());
	}

	@Override
	protected DotFunCrdt<Pair> createCRDT(DotFun<Pair> newDotfun, CausalContext cc) {
		return new PNCounterImpl(cc, newDotfun);
	}
}
