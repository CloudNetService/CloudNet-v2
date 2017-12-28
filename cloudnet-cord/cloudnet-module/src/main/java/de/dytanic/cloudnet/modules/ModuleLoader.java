/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

/**
 * Created by Tareko on 02.11.2017.
 */
public interface ModuleLoader {

    Module loadModule() throws Exception;

}