package crdt.api;

public interface Model {
	CRDT getRoot();
	void setRoot(CRDT root);
	CrdtFactory factory();
}
