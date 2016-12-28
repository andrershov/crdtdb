package crdt.inner.types.abstr;

import java.util.Set;

import crdt.api.CRDT;
import crdt.inner.causal.CausalContext;
import crdt.inner.causal.Dot;

public interface DotStoreCrdt extends CRDT {

	DotStoreCrdt createEmpty(CausalContext cc);

	boolean isEmpty();
	
	Set<Dot> dots();

}
