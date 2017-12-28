/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components.screen;

import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 25.08.2017.
 */
@Getter
@AllArgsConstructor
public class EnabledScreen {

    private ServiceId serviceId;

    private Wrapper wrapper;

}