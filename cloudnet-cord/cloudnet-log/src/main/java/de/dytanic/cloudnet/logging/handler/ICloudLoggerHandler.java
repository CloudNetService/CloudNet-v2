/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.logging.handler;

/**
 * Interface for classes that handle console messages of the cloud.
 */
public interface ICloudLoggerHandler {

    /**
     * Handle console messages
     *
     * @param input the string that should be handled
     */
    void handleConsole(String input);

}
