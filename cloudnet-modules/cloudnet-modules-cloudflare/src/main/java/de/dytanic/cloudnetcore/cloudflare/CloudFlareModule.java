package de.dytanic.cloudnetcore.cloudflare;

import de.dytanic.cloudnet.cloudflare.CloudFlareService;
import de.dytanic.cloudnet.cloudflare.database.CloudFlareDatabase;
import de.dytanic.cloudnet.lib.service.SimpleWrapperInfo;
import de.dytanic.cloudnetcore.api.CoreModule;
import de.dytanic.cloudnetcore.cloudflare.config.ConfigCloudFlare;
import de.dytanic.cloudnetcore.cloudflare.listener.ProxyAddListener;
import de.dytanic.cloudnetcore.cloudflare.listener.ProxyRemoveListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Tareko on 20.10.2017.
 */
public class CloudFlareModule extends CoreModule {

    private static CloudFlareModule instance;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ConfigCloudFlare configCloudFlare;
    private CloudFlareDatabase cloudFlareDatabase;

    public static CloudFlareModule getInstance() {
        return instance;
    }

    public ConfigCloudFlare getConfigCloudFlare() {
        return configCloudFlare;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public CloudFlareDatabase getCloudFlareDatabase() {
        return cloudFlareDatabase;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onBootstrap() {
        configCloudFlare = new ConfigCloudFlare();
        cloudFlareDatabase = new CloudFlareDatabase(getCloud().getDatabaseManager().getDatabase("cloudnet_internal_cfg"));
        try {

            CloudFlareService cloudFlareAPI = new CloudFlareService(configCloudFlare.load());

            final Map<String, SimpleWrapperInfo> wrapperInfo = new HashMap<>();
            getCloud().getWrappers().forEach((wrapperId, wrapper) -> {
                wrapperInfo.put(wrapperId, new SimpleWrapperInfo(wrapper.getServerId(), wrapper.getNetworkInfo().getHostName()));
            });
            cloudFlareAPI.bootstrap(wrapperInfo, getCloud().getProxyGroups(), cloudFlareDatabase);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        getCloud().getEventManager().registerListener(this, new ProxyAddListener());
        getCloud().getEventManager().registerListener(this, new ProxyRemoveListener());
    }

    @Override
    public void onShutdown() {

        executor.shutdownNow();

        try {
            CloudFlareService.getInstance().shutdown(cloudFlareDatabase);
        } catch (Exception ignored) {
        }
    }
}
