/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.service;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 13.09.2017.
 */
@Getter
@AllArgsConstructor
public class SimpledWrapperInfo implements Nameable {

    private String name;

    private String hostName;

}