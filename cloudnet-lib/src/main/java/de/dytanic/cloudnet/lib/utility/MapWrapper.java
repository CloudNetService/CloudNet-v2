/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.utility;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public final class MapWrapper {

    private MapWrapper() {
    }

    public static <K, V> Map<K, V> collectionCatcherHashMap(Collection<V> key, Catcher<K, V> catcher) {
        HashMap<K, V> kvHashMap = new HashMap<>();
        for (V value : key) {
            kvHashMap.put(catcher.doCatch(value), value);
        }
        return kvHashMap;
    }

    public static <K, V, NK, VK> Map<NK, VK> transform(Map<K, V> values, Catcher<NK, K> keyCatcher, Catcher<VK, V> valueCatcher) {
        Map<NK, VK> nkvkMap = new HashMap<>();
        for (Map.Entry<K, V> entry : values.entrySet()) {
            nkvkMap.put(keyCatcher.doCatch(entry.getKey()), valueCatcher.doCatch(entry.getValue()));
        }
        return nkvkMap;
    }

}
