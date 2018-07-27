package de.dytanic.cloudnet.lib.map;

import java.util.Set;

public abstract interface NetorMap<Key> {

    public void clear();

    public int size();

    public void remove(Key key);

    public boolean contains(Key key);

    public Set<Key> keySet();

}
