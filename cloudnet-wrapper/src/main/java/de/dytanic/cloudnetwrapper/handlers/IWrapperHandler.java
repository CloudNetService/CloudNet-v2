/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.handlers;

import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public interface IWrapperHandler extends Runnabled<CloudNetWrapper> {

    int getTicks();

    default Runnable toExecutor() {
        return new Runnable() {
            @Override
            public void run() {
                IWrapperHandler.this.run(CloudNetWrapper.getInstance());
            }
        };
    }
}
