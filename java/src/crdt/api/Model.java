package crdt.api;

public interface Model {
	CRDT getRoot();
	void setRoot(CRDT root);
	String getKey();
	CrdtFactory factory();
}
