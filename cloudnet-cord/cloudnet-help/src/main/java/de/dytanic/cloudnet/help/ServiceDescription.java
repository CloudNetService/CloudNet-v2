/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.help;

/**
 * Class to store a brief and a detailed usage description.
 */
public class ServiceDescription {

    /**
     * Brief usage description.
     */
    private String usage;

    /**
     * Detailed description.
     */
    private String description;

    public ServiceDescription(String usage, String description) {
        this.usage = usage;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }
}
