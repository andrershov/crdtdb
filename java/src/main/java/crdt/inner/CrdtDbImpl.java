package crdt.inner;

import crdt.api.CrdtDb;
import crdt.api.Model;
import crdt.inner.causal.Causal;
import crdt.inner.conn.NodeConnection;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class CrdtDbImpl implements CrdtDb {

    private final ConcurrentHashMap<String, Causal> map = new ConcurrentHashMap<>();
    private final DeltaStorage deltaStorage = new DeltaStorage();
    private final DeltaExchanger deltaExchanger;

    public CrdtDbImpl(List<? extends NodeConnection> nodes) {
        deltaExchanger = new DeltaExchanger(this, deltaStorage, nodes);
    }

    public CrdtDbImpl() {
        this(Collections.emptyList());
    }

    public CrdtDbImpl(NodeConnection node) {
        this(Collections.singletonList(node));
    }

    public Causal loadFullState(String key) {
        return map.get(key);
    }


    public ModelImpl load(String nodeId, String key) {
        Causal causal = map.get(key);
        if (causal == null) {
            return new ModelImpl(nodeId, key);
        }
        return new ModelImpl(nodeId, key, new Causal(causal));
    }

    public void store(Model model) {
        storeDelta(model.getKey(), model.getDelta());
    }

    public void storeDelta(String key, Causal delta) {
        AtomicBoolean storeDelta = new AtomicBoolean(true);
        map.compute(key, (ig, oldModel) -> {
            if (oldModel == null) {
                return delta;
            }
            storeDelta.set(oldModel.join(delta));
            return oldModel;
        });
        if (storeDelta.get()) {
            deltaStorage.store(key, delta);
            deltaExchanger.shipState(key);
        }
    }

    @Override
    public DeltaStorage getDeltaStorage() {
        return deltaStorage;
    }
}
