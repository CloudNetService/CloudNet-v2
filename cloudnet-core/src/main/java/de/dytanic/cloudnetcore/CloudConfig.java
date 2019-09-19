/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.user.BasicUser;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.util.WebServerConfig;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;
import de.dytanic.cloudnetcore.util.defaults.BungeeGroup;
import de.dytanic.cloudnetcore.util.defaults.LobbyGroup;
import jline.console.ConsoleReader;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/**
 * Created by Tareko on 16.09.2017.
 */
public class CloudConfig {

    private static final ConfigurationProvider CONFIGURATION_PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private final Path configPath = Paths.get("config.yml"), servicePath = Paths.get("services.json"), usersPath = Paths.get("users.json");

    private Collection<ConnectableAddress> addresses;

    private boolean autoUpdate, notifyService, cloudDynamicServices, cloudDevServices;

    private String formatSplitter, wrapperKey;

    private WebServerConfig webServerConfig;

    private List<WrapperMeta> wrappers;

    private Configuration config;

    private Document serviceDocument, userDocument;

    private List<String> disabledModules, cloudServerWrapperList;

    private Map<String, Object> networkProperties;

    private List<String> hasteServer;

    public CloudConfig(ConsoleReader consoleReader) throws Exception {

        for (File directory : new File[] {new File("local/servers"), new File("local/templates"), new File("local/plugins"), new File(
            "local/servers"), new File("local/cache"), new File("groups"), new File("modules")}) {
            directory.mkdirs();
        }

        NetworkUtils.writeWrapperKey();

        defaultInit(consoleReader);
        defaultInitDoc(consoleReader);
        defaultInitUsers(consoleReader);
        load();
    }

    private void defaultInit(ConsoleReader consoleReader) throws Exception {
        if (Files.exists(configPath)) {
            return;
        }

        String hostName = NetworkUtils.getHostName();
        if (hostName.equals("127.0.0.1") || hostName.equals("127.0.1.1") || hostName.split("\\.").length != 4) {
            String input;
            System.out.println("Your IP address where located is 127.0.0.1 please write your service ip");
            while ((input = consoleReader.readLine()) != null) {
                if ((input.equals("127.0.0.1") || input.equals("127.0.1.1")) || input.split("\\.").length != 4) {
                    System.out.println("Please write your real ip address :)");
                    continue;
                }
                hostName = input;
                break;
            }
        }

        Configuration configuration = new Configuration();

        configuration.set("general.auto-update", false);
        configuration.set("general.dynamicservices", false);
        configuration.set("general.server-name-splitter", "-");
        configuration.set("general.notify-service", true);
        configuration.set("general.disabled-modules", new ArrayList<>());
        configuration.set("general.cloudGameServer-wrapperList", Collections.singletonList("Wrapper-1"));

        configuration.set("general.haste.server",
                          Arrays.asList("https://hastebin.com",
                                        "https://hasteb.in",
                                        "https://haste.llamacloud.io",
                                        "https://paste.dsyn.ga"));

        configuration.set("server.hostaddress", hostName);
        configuration.set("server.ports", Collections.singletonList(1410));
        configuration.set("server.webservice.hostaddress", hostName);
        configuration.set("server.webservice.port", 1420);

        configuration.set("cloudnet-statistics.enabled", true);
        configuration.set("cloudnet-statistics.uuid", UUID.randomUUID().toString());
        configuration.set("networkproperties.test", true);

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath), StandardCharsets.UTF_8)) {
            CONFIGURATION_PROVIDER.save(configuration, outputStreamWriter);
        }
    }

    private void defaultInitDoc(ConsoleReader consoleReader) throws Exception {
        if (Files.exists(servicePath)) {
            return;
        }

        String hostName = NetworkUtils.getHostName();
        if (hostName.equals("127.0.0.1") || hostName.equalsIgnoreCase("127.0.1.1") || hostName.split("\\.").length != 4) {
            String input;
            System.out.println("Please write the first Wrapper IP address:");
            while ((input = consoleReader.readLine()) != null) {
                if ((input.equals("127.0.0.1") || input.equalsIgnoreCase("127.0.1.1") || input.split("\\.").length != 4)) {
                    System.out.println("Please write the real ip address :)");
                    continue;
                }

                hostName = input;
                break;
            }
        }
        new Document("wrapper", Collections.singletonList(new WrapperMeta("Wrapper-1", hostName, "admin"))).append("proxyGroups",
                                                                                                                   Collections.singletonList(
                                                                                                                       new BungeeGroup()))
                                                                                                           .saveAsConfig(servicePath);

        new Document("group", new LobbyGroup()).saveAsConfig(Paths.get("groups/Lobby.json"));
    }

    private void defaultInitUsers(ConsoleReader consoleReader) {
        if (Files.exists(usersPath)) {
            return;
        }

        String password = NetworkUtils.randomString(32);
        System.out.println("\"admin\" Password: " + password);
        System.out.println(NetworkUtils.SPACE_STRING);
        new Document().append("users", Collections.singletonList(new BasicUser("admin", password, Collections.singletonList("*"))))
                      .saveAsConfig(usersPath);
    }

    public CloudConfig load() throws Exception {

        try (InputStream inputStream = Files.newInputStream(configPath); InputStreamReader inputStreamReader = new InputStreamReader(
            inputStream,
            StandardCharsets.UTF_8)) {
            Configuration configuration = CONFIGURATION_PROVIDER.load(inputStreamReader);
            this.config = configuration;

            String host = configuration.getString("server.hostaddress");

            Collection<ConnectableAddress> addresses = new ArrayList<>();
            for (int value : configuration.getIntList("server.ports")) {
                addresses.add(new ConnectableAddress(host, value));
            }
            this.addresses = addresses;

            this.wrapperKey = NetworkUtils.readWrapperKey();
            this.autoUpdate = configuration.getBoolean("general.auto-update");
            this.notifyService = configuration.getBoolean("general.notify-service");
            this.cloudDevServices = configuration.getBoolean("general.devservices");
            this.cloudDynamicServices = configuration.getBoolean("general.dynamicservices");
            this.webServerConfig = new WebServerConfig(true,
                                                       configuration.getString("server.webservice.hostaddress"),
                                                       configuration.getInt("server.webservice.port"));
            this.formatSplitter = configuration.getString("general.server-name-splitter");
            this.networkProperties = configuration.getSection("networkproperties").self;

            if (!configuration.getSection("general").self.containsKey("disabled-modules")) {
                configuration.set("general.disabled-modules", new ArrayList<>());

                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath),
                                                                                    StandardCharsets.UTF_8)) {
                    CONFIGURATION_PROVIDER.save(configuration, outputStreamWriter);
                }
            }
            if (!configuration.getSection("general").self.containsKey("haste")) {
                configuration.set("general.haste.server",
                                  Arrays.asList("https://hastebin.com",
                                                "https://hasteb.in",
                                                "https://haste.llamacloud.io",
                                                "https://paste.dsyn.ga"));

                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath),
                                                                                    StandardCharsets.UTF_8)) {
                    CONFIGURATION_PROVIDER.save(configuration, outputStreamWriter);
                }
            }

            if (!configuration.getSection("general").self.containsKey("cloudGameServer-wrapperList")) {
                configuration.set("general.cloudGameServer-wrapperList", Collections.singletonList("Wrapper-1"));

                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath),
                                                                                    StandardCharsets.UTF_8)) {
                    CONFIGURATION_PROVIDER.save(configuration, outputStreamWriter);
                }
            }

            this.hasteServer = configuration.getStringList("general.haste.server");

            this.disabledModules = configuration.getStringList("general.disabled-modules");
            this.cloudServerWrapperList = configuration.getStringList("general.cloudGameServer-wrapperList");
        }

        this.serviceDocument = Document.loadDocument(servicePath);

        this.wrappers = this.serviceDocument.getObject("wrapper", new TypeToken<List<WrapperMeta>>() {}.getType());

        this.userDocument = Document.loadDocument(usersPath);

        return this;
    }

    public void createWrapper(WrapperMeta wrapperMeta) {
        Collection<WrapperMeta> wrapperMetas = this.serviceDocument.getObject("wrapper",
                                                                              new TypeToken<Collection<WrapperMeta>>() {}.getType());
        WrapperMeta is = CollectionWrapper.filter(wrapperMetas, new Acceptable<WrapperMeta>() {
            @Override
            public boolean isAccepted(WrapperMeta wrapperMeta_) {
                return wrapperMeta_.getId().equalsIgnoreCase(wrapperMeta.getId());
            }
        });
        if (is != null) {
            wrapperMetas.remove(is);
        }

        wrapperMetas.add(wrapperMeta);
        this.serviceDocument.append("wrapper", wrapperMetas).saveAsConfig(servicePath);
        CloudNet.getInstance().getWrappers().put(wrapperMeta.getId(), new Wrapper(wrapperMeta));
    }

    public void deleteWrapper(WrapperMeta wrapperMeta) {
        this.serviceDocument.append("wrapper", this.wrappers = this.deleteWrapper0(wrapperMeta)).saveAsConfig(this.servicePath);
        CloudNet.getInstance().getWrappers().remove(wrapperMeta.getId());
    }

    private List<WrapperMeta> deleteWrapper0(WrapperMeta wrapperMeta) {
        List<WrapperMeta> wrapperMetas = this.serviceDocument.getObject("wrapper", new TypeToken<Collection<WrapperMeta>>() {}.getType());
        WrapperMeta is = CollectionWrapper.filter(wrapperMetas, wrapperMeta_ -> wrapperMeta_.getId().equalsIgnoreCase(wrapperMeta.getId()));
        if (is != null) {
            wrapperMetas.remove(is);
        }
        return wrapperMetas;
    }

    public Collection<User> getUsers() {
        if (this.userDocument == null) {
            return null;
        }
        return userDocument.getObject("users", new TypeToken<Collection<User>>() {}.getType());
    }

    public CloudConfig save(Collection<User> users) {
        if (userDocument != null) {
            userDocument.append("users", users).saveAsConfig(usersPath);
        }
        return this;
    }

    public void createGroup(ProxyGroup serverGroup) {
        Collection<ProxyGroup> groups = this.serviceDocument.getObject("proxyGroups", new TypeToken<Collection<ProxyGroup>>() {}.getType());
        CollectionWrapper.checkAndRemove(groups, new Acceptable<ProxyGroup>() {
            @Override
            public boolean isAccepted(ProxyGroup value) {
                return value.getName().equals(serverGroup.getName());
            }
        });

        groups.add(serverGroup);
        this.serviceDocument.append("proxyGroups", groups).saveAsConfig(servicePath);
    }

    public void deleteGroup(ServerGroup serverGroup) {
        new File("groups/" + serverGroup.getName() + ".json").delete();
    }

    public void deleteGroup(ProxyGroup proxyGroup) {
        Collection<ProxyGroup> groups = this.serviceDocument.getObject("proxyGroups", new TypeToken<Collection<ProxyGroup>>() {}.getType());
        CollectionWrapper.checkAndRemove(groups, new Acceptable<ProxyGroup>() {
            @Override
            public boolean isAccepted(ProxyGroup value) {
                return value.getName().equals(proxyGroup.getName());
            }
        });
        this.serviceDocument.append("proxyGroups", groups).saveAsConfig(servicePath);
    }

    public java.util.Map<String, ServerGroup> getServerGroups() {
        Map<String, ServerGroup> groups = NetworkUtils.newConcurrentHashMap();

        if (serviceDocument.contains("serverGroups")) {

            Collection<ServerGroup> collection = serviceDocument.getObject("serverGroups",
                                                                           new TypeToken<Collection<ServerGroup>>() {}.getType());

            for (ServerGroup serverGroup : collection) {
                createGroup(serverGroup);
            }

            serviceDocument.remove("serverGroups");
            serviceDocument.saveAsConfig(servicePath);
        }

        File groupsDirectory = new File("groups");
        Document entry;

        if (groupsDirectory.isDirectory()) {
            File[] files = groupsDirectory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".json")) {
                        try {
                            entry = Document.$loadDocument(file);
                            ServerGroup serverGroup = entry.getObject("group", ServerGroup.TYPE);
                            groups.put(serverGroup.getName(), serverGroup);
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                            System.out.println("Cannot load servergroup file [" + file.getName() + ']');
                        }
                    }
                }
            }
        }

        return groups;
    }

    public void createGroup(ServerGroup serverGroup) {

        new Document("group", serverGroup).saveAsConfig(Paths.get("groups/" + serverGroup.getName() + ".json"));

    }

    public Map<String, ProxyGroup> getProxyGroups() {
        Collection<ProxyGroup> collection = serviceDocument.getObject("proxyGroups", new TypeToken<Collection<ProxyGroup>>() {}.getType());

        return MapWrapper.collectionCatcherHashMap(collection, new Catcher<String, ProxyGroup>() {
            @Override
            public String doCatch(ProxyGroup key) {
                return key.getName();
            }
        });
    }


    public Path getConfigPath() {
        return this.configPath;
    }

    public Path getServicePath() {
        return this.servicePath;
    }

    public Path getUsersPath() {
        return this.usersPath;
    }

    public Collection<ConnectableAddress> getAddresses() {
        return this.addresses;
    }

    public boolean isAutoUpdate() {
        return this.autoUpdate;
    }

    public boolean isNotifyService() {
        return this.notifyService;
    }

    public boolean isCloudDynamicServices() {
        return this.cloudDynamicServices;
    }

    public boolean isCloudDevServices() {
        return this.cloudDevServices;
    }

    public String getFormatSplitter() {
        return this.formatSplitter;
    }

    public String getWrapperKey() {
        return this.wrapperKey;
    }

    public WebServerConfig getWebServerConfig() {
        return this.webServerConfig;
    }

    public List<WrapperMeta> getWrappers() {
        return this.wrappers;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public Document getServiceDocument() {
        return this.serviceDocument;
    }

    public Document getUserDocument() {
        return this.userDocument;
    }

    public List<String> getDisabledModules() {
        return this.disabledModules;
    }

    public List<String> getCloudServerWrapperList() {
        return this.cloudServerWrapperList;
    }

    public Map<String, Object> getNetworkProperties() {
        return this.networkProperties;
    }

    public List<String> getHasteServer() {
        return this.hasteServer;
    }
}
