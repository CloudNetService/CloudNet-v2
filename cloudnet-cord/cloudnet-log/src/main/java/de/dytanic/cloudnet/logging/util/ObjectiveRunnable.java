/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.logging.util;

import lombok.Getter;

/**
 * Created by Tareko on 30.08.2017.
 */
@Getter
public abstract class ObjectiveRunnable<E> implements Runnable {

    private E element;

    public ObjectiveRunnable(E element)
    {
        this.element = element;
    }

    public abstract void run(E e);

    @Override
    public void run()
    {
        run(element);
    }
}