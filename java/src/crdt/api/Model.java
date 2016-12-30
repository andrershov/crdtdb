package crdt.api;

public interface Model {
	<V extends Crdt> V getRoot();
	void setRoot(Crdt root);
	CrdtFactory factory();
}
