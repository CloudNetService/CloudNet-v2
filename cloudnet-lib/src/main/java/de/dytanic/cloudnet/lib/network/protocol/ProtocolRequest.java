/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 09.09.2017.
 */
@Getter
@AllArgsConstructor
public class ProtocolRequest {

    private int id;

    private Object element;

}