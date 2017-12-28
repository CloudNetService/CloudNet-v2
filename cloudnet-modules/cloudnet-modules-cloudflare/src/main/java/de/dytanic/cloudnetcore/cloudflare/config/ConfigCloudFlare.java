/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.cloudflare.config;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.cloudflare.CloudFlareConfig;
import de.dytanic.cloudnet.cloudflare.CloudFlareProxyGroup;
import de.dytanic.cloudnetcore.config.ConfigAbstract;
import de.dytanic.cloudnetcore.config.ILoader;

import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by Tareko on 26.08.2017.
 */
public class ConfigCloudFlare extends ConfigAbstract implements ILoader<CloudFlareConfig> {

    public ConfigCloudFlare()
    {
        super(new Document("cloudflare", new CloudFlareConfig(false, "example@gmail.com", "token", "dytanic.de", "zone", Arrays.asList(new CloudFlareProxyGroup("Bungee", "server")))),
                Paths.get("local/cloudflare.json"));
    }

    @Override
    public CloudFlareConfig load()
    {
        return Document.loadDocument(path).getObject("cloudflare", new TypeToken<CloudFlareConfig>(){}.getType());
    }

}