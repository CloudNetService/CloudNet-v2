package de.dytanic.cloudnet.lib.map;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NetorHashMap<Key, VF, VS> implements NetorMap<Key> {

    private ConcurrentHashMap<Key, NetorSet<VF, VS>> values = new ConcurrentHashMap<>();

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public void remove(Key key) {
        values.remove(key);
    }

    @Override
    public boolean contains(Key key) {
        return values.containsKey(key) ? true : false;
    }

    @Override
    public Set<Key> keySet() {
        return values.keySet();
    }

    public void add(Key key, VF valueF, VS valueS) {
        values.put(key, new NetorSet<>(valueF, valueS));
    }

    public VF getF(Key key) {
        return values.get(key).getFirstValue();
    }

    public VS getS(Key key) {
        return values.get(key).getSecondValue();
    }

    public void updateF(Key key, VF value) {
        values.get(key).updateFirst(value);
    }

    public void updateS(Key key, VS value) {
        values.get(key).updateSecond(value);
    }

}
