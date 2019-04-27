/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data class that stores information about a {@link Module}
 */
@Getter
@AllArgsConstructor
public class ModuleConfig {

    /**
     * The file this module is stored in.
     */
    private File file;

    /**
     * The name of this module, used for the configuration directory.
     */
    private String name;

    /**
     * The version string of this module.
     */
    private String version;

    /**
     * The author of this module.
     */
    private String author;

    /**
     * The path to the main class of this module.
     */
    private String main;

}
