/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Tareko on 17.09.2017.
 */
public final class Maps {

    private Maps() {
    }

    public static class CollectionMap<K, V> extends ConcurrentHashMap<K, Collection<V>> {}

    public static class ArrayMap<K, V> extends ConcurrentHashMap<K, V[]> {}

    public static class CollectionHashMap<K, V> extends HashMap<K, Collection<V>> {}

    public static class ArrayHashMap<K, V> extends HashMap<K, V[]> {}
}
