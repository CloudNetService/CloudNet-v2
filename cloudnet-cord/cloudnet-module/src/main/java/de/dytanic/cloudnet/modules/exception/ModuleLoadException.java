/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules.exception;

/**
 * Runtime exception that should be thrown when loading a {@link de.dytanic.cloudnet.modules.Module}.
 */
public class ModuleLoadException extends RuntimeException {

    /**
     * Constructs a new runtime exception with a given message.
     *
     * @param message the message that details the exception
     */
    public ModuleLoadException(String message) {
        super(message);
    }
}
