/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.wrapper;

import de.dytanic.cloudnetcore.network.components.WrapperMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Tareko on 23.09.2017.
 */
@Getter
@AllArgsConstructor
public class WrapperSession {

    private UUID uniqueId;

    private WrapperMeta wrapperMeta;

    private long connected;

}