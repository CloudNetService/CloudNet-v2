package de.dytanic.cloudnet.lib.interfaces;

public interface Initable extends Runnable {

    default void run() {
        init();
    }

    void init();

}
