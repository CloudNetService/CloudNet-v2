/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.utility;

import de.dytanic.cloudnet.lib.utility.threading.Runnabled;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CollectionWrapper {

    private CollectionWrapper() {
    }

    public static <E, X> Collection<X> transform(Collection<E> collection, Catcher<X, E> catcher) {
        Collection<X> xCollection = newCopyOnWriteArrayList();
        for (E e : collection) {
            xCollection.add(catcher.doCatch(e));
        }
        return xCollection;
    }

    public static <E> java.util.List<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<>();
    }

    public static <E> Collection<E> filterMany(Collection<E> elements, Acceptable<E> acceptable) {
        Collection<E> collection = new LinkedList<>();
        for (E element : elements) {
            if (acceptable.isAccepted(element)) {
                collection.add(element);
            }
        }
        return collection;
    }

    public static <E> E filter(Collection<E> elements, Acceptable<E> acceptable) {
        for (E element : elements) {
            if (acceptable.isAccepted(element)) {
                return element;
            }
        }
        return null;
    }

    public static <E> CopyOnWriteArrayList<E> transform(Collection<E> defaults) {
        return new CopyOnWriteArrayList<>(defaults);
    }

    public static Collection<String> toCollection(String input, String splitter) {
        return new CopyOnWriteArrayList<>(input.split(splitter));
    }

    public static <E> void iterator(Collection<E> collection, Runnabled<E>... runnableds) {
        for (E el : collection) {
            for (Runnabled<E> runnabled : runnableds) {
                runnabled.run(el);
            }
        }
    }

    public static <E> void iterator(E[] collection, Runnabled<E>... runnableds) {
        for (E el : collection) {
            for (Runnabled<E> runnabled : runnableds) {
                runnabled.run(el);
            }
        }
    }

    public static <E, X, C> Collection<E> getCollection(java.util.Map<X, C> map, Catcher<E, C> catcher) {
        Collection<E> collection = new LinkedList<>();
        for (C values : map.values()) {
            collection.add(catcher.doCatch(values));
        }
        return collection;
    }

    public static <E> void checkAndRemove(Collection<E> collection, Acceptable<E> acceptable) {
        E e = null;
        for (E element : collection) {
            if (acceptable.isAccepted(element)) {
                e = element;
            }
        }

        if (e != null) {
            collection.remove(e);
        }

    }

    public static <E> void iterator(E[] values, Runnabled<E> handled) {
        for (E value : values) {
            handled.run(value);
        }
    }

    public static <E> boolean equals(E[] array, E value) {
        for (E a : array) {
            if (a.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <E> int filled(E[] array) {
        int i = 0;
        for (E element : array) {
            if (element != null) {
                i++;
            }
        }
        return i;
    }

    public static <E> boolean isEmpty(E[] array) {
        for (E element : array) {
            if (element != null) {
                return false;
            }
        }
        return true;
    }

    public static <E> void remove(E[] array, E element) {
        int i = index(array, element);
        array[i] = null;
    }

    public static <E> int index(E[] array, E element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                return i;
            }
        }
        return 0;
    }

    public static <E> E[] dynamicArray(Class<E> clazz) {
        return (E[]) Array.newInstance(clazz, Integer.MAX_VALUE);
    }
}
