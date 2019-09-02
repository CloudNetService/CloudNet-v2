/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.handler;

import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 16.08.2017.
 */
public interface ICloudHandler extends Runnable {

    default void run() {
        onHandle(CloudNet.getInstance());
    }

    void onHandle(CloudNet cloudNet);

    int getTicks();

}
