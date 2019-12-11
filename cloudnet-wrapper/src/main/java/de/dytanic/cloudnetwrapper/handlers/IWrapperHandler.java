/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.handlers;

import de.dytanic.cloudnetwrapper.CloudNetWrapper;

import java.util.function.Consumer;

public interface IWrapperHandler extends Consumer<CloudNetWrapper> {

    default Runnable toExecutor() {
        return () -> accept(CloudNetWrapper.getInstance());
    }
}
