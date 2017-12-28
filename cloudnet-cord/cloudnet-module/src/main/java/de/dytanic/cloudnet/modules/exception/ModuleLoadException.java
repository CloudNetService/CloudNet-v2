/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules.exception;

/**
 * Created by Tareko on 02.11.2017.
 */
public class ModuleLoadException extends RuntimeException {

    public ModuleLoadException(String message)
    {
        super(message);
    }
}