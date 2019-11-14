/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.utility;

import java.util.Collection;
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

}
