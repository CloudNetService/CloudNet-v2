/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.cloudflare.listener;

import de.dytanic.cloudnet.cloudflare.CloudFlareService;
import de.dytanic.cloudnet.event.IEventListener;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.api.event.server.ProxyAddEvent;
import de.dytanic.cloudnetcore.cloudflare.CloudFlareModule;

/**
 * Created by Tareko on 20.10.2017.
 */
public class ProxyAddListener implements IEventListener<ProxyAddEvent> {

    @Override
    public void onCall(ProxyAddEvent event) {
        CloudFlareModule.getInstance().getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                CloudFlareService.getInstance().addProxy(event.getProxyServer().getProcessMeta(),
                                                         CloudFlareModule.getInstance().getCloudFlareDatabase());
                NetworkUtils.sleepUninterruptedly(500);
            }
        });
    }
}
