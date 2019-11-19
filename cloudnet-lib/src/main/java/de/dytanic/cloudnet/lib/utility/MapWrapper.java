/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.utility;

import java.util.HashMap;
import java.util.Map;


public final class MapWrapper {

    private MapWrapper() {
    }

    public static <K, V, NK, VK> Map<NK, VK> transform(Map<K, V> values, Catcher<NK, K> keyCatcher, Catcher<VK, V> valueCatcher) {
        Map<NK, VK> nkvkMap = new HashMap<>();
        for (Map.Entry<K, V> entry : values.entrySet()) {
            nkvkMap.put(keyCatcher.doCatch(entry.getKey()), valueCatcher.doCatch(entry.getValue()));
        }
        return nkvkMap;
    }

}
