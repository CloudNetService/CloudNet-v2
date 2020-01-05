package de.dytanic.cloudnetcore.cloudflare.config;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.cloudflare.CloudFlareConfig;
import de.dytanic.cloudnet.cloudflare.CloudFlareProxyGroup;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.config.ConfigAbstract;
import de.dytanic.cloudnetcore.config.ILoader;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Tareko on 26.08.2017.
 */
public class ConfigCloudFlare extends ConfigAbstract implements ILoader<Collection<CloudFlareConfig>> {

    private static final Type CLOUDFLARECONFIG_COLLECTION_TYPE = TypeToken.getParameterized(Collection.class, CloudFlareConfig.class)
                                                                          .getType();

    public ConfigCloudFlare() {
        super(new Document("configurations", Collections.singletonList(
            new CloudFlareConfig(false, "example@gmail.com", "token", "example.com", "zone",
                                 Collections.singletonList(new CloudFlareProxyGroup("Bungee", "server"))))),
              Paths.get("local/cloudflare_cfg.json"));
    }

    @Override
    public Collection<CloudFlareConfig> load() {
        File old = new File("local/cloudflare.json");

        if (old.exists()) {
            CloudFlareConfig cloudFlareConfig = Document.loadDocument(old).getObject("cloudflare", CloudFlareConfig.TYPE);

            new Document().append("configurations", Collections.singletonList(cloudFlareConfig)).saveAsConfig(path);
            old.delete();
        }

        return Document.loadDocument(path).getObject("configurations", CLOUDFLARECONFIG_COLLECTION_TYPE);
    }

}
