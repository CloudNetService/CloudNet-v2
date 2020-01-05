package de.dytanic.cloudnetcore.mobs.config;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobItemLayout;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.config.ILoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Tareko on 21.08.2017.
 */
public class ConfigMobs implements ILoader<MobConfig> {

    private final Path path = Paths.get("local/servermob_config.json");

    public ConfigMobs() {
        if (!Files.exists(path)) {
            final List<String> lore = Collections.singletonList(NetworkUtils.SPACE_STRING);
            final MobItemLayout defaultLayout = new MobItemLayout(160,
                                                                  "BLACK_STAINED_GLASS_PANE",
                                                                  15,
                                                                  NetworkUtils.SPACE_STRING,
                                                                  lore);
            final Map<Integer, MobItemLayout> defaultInventory = new HashMap<>();
            for (int i = 1; i < 10; i++) {
                defaultInventory.put(i, defaultLayout);
            }
            Document document = new Document(
                "mobConfig",
                new MobConfig(54,
                              10,
                              new MobItemLayout(388,
                                                "EMERALD",
                                                0,
                                                "§6%server%",
                                                Arrays.asList(NetworkUtils.SPACE_STRING,
                                                              "§e%state%",
                                                              "§e%online_players% §8/§e%max_players%",
                                                              "§e%motd%")),
                              defaultInventory));
            document.saveAsConfig(path);
        }
    }

    @Override
    public MobConfig load() {
        return Document.loadDocument(path).getObject("mobConfig", MobConfig.TYPE);
    }

}
