/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.notifyservice;

import de.dytanic.cloudnetcore.api.CoreModule;

/**
 * Created by Tareko on 23.10.2017.
 */
public class NotifyServiceModule extends CoreModule {

    @Override
    public void onLoad() {

    }

    @Override
    public void onBootstrap() {
        getCloud().getNetworkManager().getModuleProperties().append("notifyService", getCloud().getConfig().isNotifyService());
    }
}
