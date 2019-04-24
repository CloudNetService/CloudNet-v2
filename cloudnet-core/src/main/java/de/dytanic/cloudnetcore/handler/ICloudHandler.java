/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.handler;

import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 16.08.2017.
 */
public interface ICloudHandler extends Runnable {

    void onHandle(CloudNet cloudNet);

    default void run() {
        onHandle(CloudNet.getInstance());
    }

    int getTicks();

}