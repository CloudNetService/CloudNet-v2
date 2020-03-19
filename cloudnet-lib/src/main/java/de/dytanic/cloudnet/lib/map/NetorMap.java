package de.dytanic.cloudnet.lib.map;

import java.util.Set;

public interface NetorMap<Key> {

    void clear();

    int size();

    void remove(Key key);

    boolean contains(Key key);

    Set<Key> keySet();

}
