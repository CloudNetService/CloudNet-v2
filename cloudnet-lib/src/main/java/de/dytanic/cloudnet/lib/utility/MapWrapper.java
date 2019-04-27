/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.utility;

import de.dytanic.cloudnet.lib.NetworkUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;


public final class MapWrapper {

    private MapWrapper()
    {
    }

    public static <K, V> Map<K, V> collectionCatcherHashMap(Collection<V> key, Function<V, K> catcher)
    {
        HashMap<K, V> kvHashMap = new HashMap<>();
        for (V value : key)
        {
            kvHashMap.put(catcher.apply(value), value);
        }
        return kvHashMap;
    }

    public static <K, V> Map<K, V> filter(Map<K, V> map, Predicate<V> acceptable)
    {
        Map<K, V> filter = NetworkUtils.newConcurrentHashMap();
        for (Map.Entry<K, V> value : map.entrySet())
        {
            if (acceptable.test(value.getValue()))
            {
                filter.put(value.getKey(), value.getValue());
            }
        }
        return filter;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> valueableHashMap(Return<K, V>... returns)
    {
        HashMap<K, V> map = new HashMap<>();
        for (Return<K, V> kvReturn : returns)
        {
            map.put(kvReturn.getFirst(), kvReturn.getSecond());
        }
        return map;
    }

    public static <K, V, NK, VK> Map<NK, VK> transform(Map<K, V> values, Function<K, NK> keyCatcher, Function<V, VK> valueCatcher)
    {
        Map<NK, VK> nkvkMap = new HashMap<>();
        for (Map.Entry<K, V> entry : values.entrySet())
        {
            nkvkMap.put(keyCatcher.apply(entry.getKey()), valueCatcher.apply(entry.getValue()));
        }
        return nkvkMap;
    }

}