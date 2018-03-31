/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.utility;

import de.dytanic.cloudnet.lib.NetworkUtils;

import java.util.Collection;
import java.util.HashMap;


public final class MapWrapper {

    private MapWrapper() {}

    public static <K, V> java.util.Map collectionCatcherHashMap(Collection<V> key, Catcher<K, V> catcher)
    {
        HashMap<K, V> kvHashMap = new HashMap<>();
        for(V value : key)
        {
            kvHashMap.put(catcher.doCatch(value), value);
        }
        return kvHashMap;
    }

    public static <K, V> java.util.Map filter(java.util.Map<K, V> map, Acceptable<V> acceptable)
    {
        java.util.Map<K, V> filter = NetworkUtils.newConcurrentHashMap();
        for(java.util.Map.Entry<K, V> value : map.entrySet())
        {
            if(acceptable.isAccepted(value.getValue()))
            {
                filter.put(value.getKey(), value.getValue());
            }
        }
        return filter;
    }

    @SafeVarargs
    public static <K, V> java.util.Map valueableHashMap(Return<K, V>... returns)
    {
        java.util.HashMap<K, V> map = new HashMap<>();
        for(Return<K, V> kvReturn : returns)
        {
            map.put(kvReturn.getFirst(), kvReturn.getSecond());
        }
        return map;
    }

    public static <K, V, NK, VK> java.util.Map transform(java.util.Map<K, V> values, Catcher<NK, K> keyCatcher, Catcher<VK, V> valueCatcher)
    {
        java.util.Map<NK, VK> nkvkMap = new HashMap<>();
        for(java.util.Map.Entry<K, V> entry : values.entrySet())
        {
            nkvkMap.put(keyCatcher.doCatch(entry.getKey()), valueCatcher.doCatch(entry.getValue()));
        }
        return nkvkMap;
    }

}