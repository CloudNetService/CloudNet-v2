package de.dytanic.cloudnetcore.permissions.config;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import de.dytanic.cloudnet.lib.utility.Return;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Tareko on 11.11.2017.
 */
public class ConfigPermissions {

    private final Path path = Paths.get("local/perms.yml");

    private Configuration cache;

    public ConfigPermissions() throws Exception {
        if (!Files.exists(path)) {
            Files.createFile(path);

            Configuration configuration = new Configuration();
            configuration.set("enabled", true);
            configuration.set("groups", new Configuration());

            if (!Files.exists(Paths.get("local/permissions.yml"))) {
                PermissionGroup member = new PermissionGroup("default",
                                                             "&8",
                                                             "§eMember §7▎ ",
                                                             "§f",
                                                             "§e",
                                                             9999,
                                                             0,
                                                             true,
                                                             new HashMap<>(),
                                                             MapWrapper.valueableHashMap(new Return<>("Lobby",
                                                                                                      Arrays.asList(
                                                                                                          "test.permission.for.group.Lobby"))),
                                                             new HashMap<>(),
                                                             new ArrayList<>());
                write(member, configuration);

                PermissionGroup admin = new PermissionGroup("Admin",
                                                            "&c",
                                                            "§cAdmin §7▎ ",
                                                            "§f",
                                                            "§c",
                                                            0,
                                                            100,
                                                            false,
                                                            MapWrapper.valueableHashMap(new Return<>("*", true)),
                                                            MapWrapper.valueableHashMap(new Return<>("Lobby",
                                                                                                     Arrays.asList(
                                                                                                         "test.permission.for.group.Lobby"))),
                                                            new HashMap<>(),
                                                            new ArrayList<>());
                write(admin, configuration);
            } else {
                Document document = Document.loadDocument(Paths.get("local/permissions.yml"));
                Collection<PermissionGroup> groups = document.getObject("groups",
                                                                        new TypeToken<Collection<PermissionGroup>>() {}.getType());
                Map<String, PermissionGroup> maps = MapWrapper.collectionCatcherHashMap(groups, new Catcher<String, PermissionGroup>() {
                    @Override
                    public String doCatch(PermissionGroup key) {
                        return key.getName();
                    }
                });

                configuration.set("enabled", document.getBoolean("enabled"));

                for (PermissionGroup value : maps.values()) {
                    write(value, configuration);
                }

                Files.deleteIfExists(Paths.get("local/permissions.yml"));

            }

            try (OutputStream outputStream = Files.newOutputStream(path); OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                outputStream,
                StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
            }
        }

        loadCache();

    }

    public void write(PermissionGroup permissionGroup, Configuration configuration) {
        Configuration section = configuration.getSection("groups");

        Configuration group = new Configuration();
        group.set("prefix", permissionGroup.getPrefix());
        group.set("suffix", permissionGroup.getSuffix());
        group.set("display", permissionGroup.getDisplay());
        group.set("color", permissionGroup.getColor());
        group.set("tagId", permissionGroup.getTagId());
        group.set("joinPower", permissionGroup.getJoinPower());
        group.set("defaultGroup", permissionGroup.isDefaultGroup());

        Collection<String> perms = new CopyOnWriteArrayList<>();
        for (Map.Entry<String, Boolean> entry : permissionGroup.getPermissions().entrySet()) {
            perms.add((!entry.getValue() ? "-" : "") + entry.getKey());
        }
        group.set("permissions", perms);

        Configuration permsCfg = new Configuration();
        for (Map.Entry<String, List<String>> keys : permissionGroup.getServerGroupPermissions().entrySet()) {
            permsCfg.set(keys.getKey(), keys.getValue());
        }
        group.set("serverGroupPermissions", permsCfg);

        if (permissionGroup.getOptions().size() == 0) {
            permissionGroup.getOptions().put("test_option", true);
        }

        group.set("options", permissionGroup.getOptions());
        group.set("implements", permissionGroup.getImplementGroups());

        section.set(permissionGroup.getName(), null);
        section.set(permissionGroup.getName(), group);
    }

    private void loadCache() {
        try (InputStream inputStream = Files.newInputStream(path); InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                                                                                                                               StandardCharsets.UTF_8)) {
            this.cache = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePermissionGroup(PermissionGroup permissionGroup) {
        if (this.cache == null) {
            loadCache();
        }

        write(permissionGroup, this.cache);

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cache, outputStreamWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, PermissionGroup> loadAll0() {
        loadCache();

        return read(this.cache);
    }

    public Map<String, PermissionGroup> read(Configuration configuration) {
        Map<String, PermissionGroup> maps = new LinkedHashMap<>();

        Configuration section = configuration.getSection("groups");

        for (String key : section.getKeys()) {
            Configuration group = section.getSection(key);

            HashMap<String, Boolean> permissions = new HashMap<>();
            List<String> permissionSection = group.getStringList("permissions");

            for (String entry : permissionSection) {
                permissions.put(entry.replaceFirst("-", ""), (!entry.startsWith("-")));
            }

            HashMap<String, List<String>> permissionsGroups = new HashMap<>();
            Configuration permissionSectionGroups = group.getSection("serverGroupPermissions");

            for (String entry : permissionSectionGroups.getKeys()) {
                permissionsGroups.put(entry, permissionSectionGroups.getStringList(entry));
            }

            PermissionGroup permissionGroup = new PermissionGroup(key,
                                                                  group.getString("color"),
                                                                  group.getString("prefix"),
                                                                  group.getString("suffix"),
                                                                  group.getString("display"),
                                                                  group.getInt("tagId"),
                                                                  group.getInt("joinPower"),
                                                                  group.getBoolean("defaultGroup"),
                                                                  permissions,
                                                                  permissionsGroups,
                                                                  group.getSection("options").self,
                                                                  group.getStringList("implements"));
            maps.put(permissionGroup.getName(), permissionGroup);
        }

        return maps;
    }

    public Map<String, PermissionGroup> loadAll() {
        if (this.cache == null) {
            loadCache();
        }

        return read(this.cache);
    }

    public boolean isEnabled() {
        loadCache();
        return this.cache.getBoolean("enabled");

    }

    public boolean isEnabled0() {
        if (this.cache == null) {
            loadCache();
        }
        return this.cache.getBoolean("enabled");

    }

}
