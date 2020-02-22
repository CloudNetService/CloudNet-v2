package de.dytanic.cloudnetcore.permissions.config;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
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

    private static final Type COLLECTION_PERMISSION_GROUP_TYPE = TypeToken.getParameterized(Collection.class, PermissionGroup.class)
                                                                          .getType();
    private final Path path = Paths.get("local/perms.yml");

    private Configuration cache;

    public ConfigPermissions() throws Exception {
        if (!Files.exists(path)) {
            Files.createFile(path);

            Configuration configuration = new Configuration();
            configuration.set("enabled", true);
            configuration.set("groups", new Configuration());

            final Map<String, List<String>> defaultServerGroupPermissions = new HashMap<>();
            defaultServerGroupPermissions.put("Lobby", Collections.singletonList("test.permission.for.group.lobby"));
            final Map<String, Boolean> defaultAdminPermissions = Collections.singletonMap("*", true);

            PermissionGroup member = new PermissionGroup("default",
                                                         "&8",
                                                         "§eMember §7▎ ",
                                                         "§f",
                                                         "§e",
                                                         9999,
                                                         0,
                                                         true,
                                                         new HashMap<>(),
                                                         defaultServerGroupPermissions,
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
                                                        defaultAdminPermissions,
                                                        defaultServerGroupPermissions,
                                                        new HashMap<>(),
                                                        new ArrayList<>());
            write(admin, configuration);

            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, writer);
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
            perms.add((entry.getValue() ? "" : "-") + entry.getKey());
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
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            this.cache = ConfigurationProvider.getProvider(YamlConfiguration.class).load(reader);
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
                if (entry.startsWith("-")) {
                    permissions.put(entry.substring(1), false);
                } else {
                    permissions.put(entry, true);
                }
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
                                                                  group.getSection("options").getSelf(),
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
