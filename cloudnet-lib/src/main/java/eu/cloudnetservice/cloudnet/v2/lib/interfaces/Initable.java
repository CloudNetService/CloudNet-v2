package eu.cloudnetservice.cloudnet.v2.lib.interfaces;

public interface Initable extends Runnable {

    default void run() {
        init();
    }

    void init();

}
