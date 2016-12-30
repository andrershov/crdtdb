package crdt.inner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DeltaStorage {
	
	private Map<String, Storage> storages = new ConcurrentHashMap<>();
	
	private static class Storage {
		private List<ModelState> storage = new ArrayList<>();
		private int newestDeltaCounter = 0;
		private int oldestDeltaCounter = 0;

		
		public void store(ModelState delta) {
			newestDeltaCounter++;
			storage.add(delta);
		}

		public ModelState getDeltaInterval(int startIndex) {
			ModelState deltaInterval = null;
			for (int i = startIndex; i < newestDeltaCounter; i++) {
				ModelState delta = storage.get(i);
				if (deltaInterval == null) {
					deltaInterval = delta;
				} else {
					deltaInterval.join(delta);
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

	
	public void store(ModelState delta) {
		storages.compute(delta.getKey(), (k,v) -> {
			if (v == null) {
				v = new Storage();
			} 
			v.store(delta);
			return v;
		});
	}

	public ModelState getDeltaInterval(String key, int startIndex) {
		return storages.get(key).getDeltaInterval(startIndex);
	}

	public int getNewestDeltaCounter(String key) {
		return storages.get(key).getNewestDeltaCounter();
	}

	public int getOldestDeltaCounter(String key) {
		return storages.get(key).getOldestDeltaCounter();
	}
	
	public Set<String> keySet(){
		return new HashSet<>(storages.keySet());
	}
}
