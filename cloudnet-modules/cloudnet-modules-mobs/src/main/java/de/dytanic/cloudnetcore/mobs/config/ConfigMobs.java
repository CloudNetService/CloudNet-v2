/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.mobs.config;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobItemLayout;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import de.dytanic.cloudnet.lib.utility.Return;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.config.ILoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Tareko on 21.08.2017.
 */
public class ConfigMobs implements ILoader<MobConfig> {

    private final Path path = Paths.get("local/servermob_config.json");

    public ConfigMobs() {
        if (!Files.exists(path)) {
            new Document().append("mobConfig",
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
                                                MapWrapper.valueableHashMap(new Return<>(1,
                                                                                         new MobItemLayout(160,
                                                                                                           "BLACK_STAINED_GLASS_PANE",
                                                                                                           15,
                                                                                                           NetworkUtils.SPACE_STRING,
                                                                                                           Collections.singletonList(
                                                                                                               NetworkUtils.SPACE_STRING))),
                                                                            new Return<>(2,
                                                                                         new MobItemLayout(160,
                                                                                                           "BLACK_STAINED_GLASS_PANE",
                                                                                                           15,
                                                                                                           NetworkUtils.SPACE_STRING,
                                                                                                           Collections.singletonList(
                                                                                                               NetworkUtils.SPACE_STRING))),
                                                                            new Return<>(3,
                                                                                         new MobItemLayout(160,
                                                                                                           "BLACK_STAINED_GLASS_PANE",
                                                                                                           15,
                                                                                                           NetworkUtils.SPACE_STRING,
                                                                                                           Collections.singletonList(
                                                                                                               NetworkUtils.SPACE_STRING))),
                                                                            new Return<>(5,
                                                                                         new MobItemLayout(160,
                                                                                                           "BLACK_STAINED_GLASS_PANE",
                                                                                                           15,
                                                                                                           NetworkUtils.SPACE_STRING,
                                                                                                           Collections.singletonList(
                                                                                                               NetworkUtils.SPACE_STRING))),
                                                                            new Return<>(6,
                                                                                         new MobItemLayout(160,
                                                                                                           "BLACK_STAINED_GLASS_PANE",
                                                                                                           15,
                                                                                                           NetworkUtils.SPACE_STRING,
                                                                                                           Collections.singletonList(
                                                                                                               NetworkUtils.SPACE_STRING))),
                                                                            new Return<>(7,
                                                                                         new MobItemLayout(160,
                                                                                                           "BLACK_STAINED_GLASS_PANE",
                                                                                                           15,
                                                                                                           NetworkUtils.SPACE_STRING,
                                                                                                           Collections.singletonList(
                                                                                                               NetworkUtils.SPACE_STRING))),
                                                                            new Return<>(8,
                                                                                         new MobItemLayout(160,
                                                                                                           "BLACK_STAINED_GLASS_PANE",
                                                                                                           15,
                                                                                                           NetworkUtils.SPACE_STRING,
                                                                                                           Collections.singletonList(
                                                                                                               NetworkUtils.SPACE_STRING))),
                                                                            new Return<>(9,
                                                                                         new MobItemLayout(160,
                                                                                                           "BLACK_STAINED_GLASS_PANE",
                                                                                                           15,
                                                                                                           NetworkUtils.SPACE_STRING,
                                                                                                           Collections.singletonList(
                                                                                                               NetworkUtils.SPACE_STRING))))))
                          .saveAsConfig(path);
        }
    }

    @Override
    public MobConfig load() {
        return Document.loadDocument(path).getObject("mobConfig", new TypeToken<MobConfig>() {}.getType());
    }

}
