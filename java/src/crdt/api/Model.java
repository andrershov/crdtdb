package crdt.api;

public interface Model {
	<V extends CRDT> V getRoot();
	void setRoot(CRDT root);
	String getKey();
	CrdtFactory factory();
	String innerToString();
}
