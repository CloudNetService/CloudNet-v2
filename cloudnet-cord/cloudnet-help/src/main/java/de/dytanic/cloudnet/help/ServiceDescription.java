/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.help;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class to store a brief and a detailed usage description.
 */
@Getter
@AllArgsConstructor
public class ServiceDescription {

    /**
     * Brief usage description.
     */
    private String usage;

    /**
     * Detailed description.
     */
    private String description;

}
