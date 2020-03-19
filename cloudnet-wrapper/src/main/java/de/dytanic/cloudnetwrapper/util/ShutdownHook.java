package de.dytanic.cloudnetwrapper.util;

import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class ShutdownHook implements Runnable {

    private CloudNetWrapper cloudNetWrapper;

    public ShutdownHook(CloudNetWrapper wrapper) {
        this.cloudNetWrapper = wrapper;
    }

    @Override
    public void run() {
        cloudNetWrapper.shutdown();
    }
}
