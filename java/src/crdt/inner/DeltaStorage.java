package crdt.inner;

import java.util.ArrayList;
import java.util.List;

public class DeltaStorage {

	private List<ModelImpl> storage = new ArrayList<>();
	private int newestDeltaCounter = 0;
	private int oldestDeltaCounter = 0;

	public void store(ModelImpl delta) {
		newestDeltaCounter++;
		storage.add(delta);
	}

	public ModelImpl getDeltaInterval(int startIndex) {
		ModelImpl deltaInterval = null;
		
		for (int i = startIndex; i < newestDeltaCounter; i++) {
			ModelImpl delta = storage.get(i);
			if (deltaInterval == null) {
				deltaInterval = delta;
			} else {
				deltaInterval.joinDelta(delta);
			}
		}
		return deltaInterval;
	}

	public int getNewestDeltaCounter() {
		return newestDeltaCounter;
	}

	public int getOldestDeltaCounter() {
		return oldestDeltaCounter;
	}

}
