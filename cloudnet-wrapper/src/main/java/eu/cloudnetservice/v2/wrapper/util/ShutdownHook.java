package eu.cloudnetservice.v2.wrapper.util;

import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;

public class ShutdownHook implements Runnable {

    private final CloudNetWrapper cloudNetWrapper;

    public ShutdownHook(CloudNetWrapper wrapper) {
        this.cloudNetWrapper = wrapper;
    }

    @Override
    public void run() {
        cloudNetWrapper.shutdown();
    }
}
