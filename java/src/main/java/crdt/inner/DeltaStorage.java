package crdt.inner;

import crdt.inner.causal.Causal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DeltaStorage {

    private final Map<String, Storage> storages = new ConcurrentHashMap<>();

    private static class Storage {
        private final List<Causal> storage = new ArrayList<>();
        private int newestDeltaCounter = 0;
        private final int oldestDeltaCounter = 0;


        public void store(Causal delta) {
            newestDeltaCounter++;
            storage.add(delta);
        }

        public Causal getDeltaInterval(int startIndex) {
            Causal deltaInterval = null;
            for (int i = startIndex; i < newestDeltaCounter; i++) {
                Causal delta = storage.get(i);
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


    public void store(String key, Causal delta) {
        storages.compute(key, (k, v) -> {
            if (v == null) {
                v = new Storage();
            }
            v.store(delta);
            return v;
        });
    }

    public Causal getDeltaInterval(String key, int startIndex) {
        return storages.get(key).getDeltaInterval(startIndex);
    }

    public int getNewestDeltaCounter(String key) {
        return storages.get(key).getNewestDeltaCounter();
    }

    public int getOldestDeltaCounter(String key) {
        return storages.get(key).getOldestDeltaCounter();
    }

    public Set<String> keySet() {
        return new HashSet<>(storages.keySet());
    }
}
