/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

import java.io.File;

/**
 * Data class that stores information about a {@link Module}
 */
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

    public ModuleConfig(File file, String name, String version, String author, String main) {
        this.file = file;
        this.name = name;
        this.version = version;
        this.author = author;
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public String getMain() {
        return main;
    }
}
