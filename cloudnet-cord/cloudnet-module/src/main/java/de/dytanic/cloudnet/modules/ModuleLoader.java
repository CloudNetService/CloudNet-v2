/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

/**
 * Interface for {@link ClassLoader}s that load {@link Module}s.
 */
public interface ModuleLoader {

    /**
     * Load the module that is attached to this loader.
     *
     * @return the module that was loaded
     *
     * @throws Exception when an error loading the module happened
     */
    Module loadModule() throws Exception;

}
