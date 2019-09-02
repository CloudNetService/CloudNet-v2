/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.interfaces;

public interface Initable extends Runnable {

    default void run() {
        init();
    }

    void init();

}
