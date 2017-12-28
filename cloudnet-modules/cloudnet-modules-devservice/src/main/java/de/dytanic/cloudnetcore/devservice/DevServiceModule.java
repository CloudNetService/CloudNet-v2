/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.devservice;

import de.dytanic.cloudnetcore.api.CoreModule;
import lombok.Getter;

/**
 * Created by Tareko on 21.10.2017.
 */
public class DevServiceModule extends CoreModule {

    @Getter
    private static DevServiceModule instance;

    @Override
    public void onLoad()
    {
        instance = this;
    }

    @Override
    public void onBootstrap()
    {
        getCloud().getNetworkManager().getModuleProperties().append("devservice", true);
    }
}