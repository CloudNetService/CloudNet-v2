/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.proxylayout.DynamicFallback;
import de.dytanic.cloudnet.lib.proxylayout.ServerFallback;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
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
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.print.Doc;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Tareko on 16.09.2017.
 */
@Getter
public class CloudConfig {

    private static final ConfigurationProvider configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private final Path configPath = Paths.get("config.yml");

    private final Path servicePath = Paths.get("services.json");

    private final Path usersPath = Paths.get("users.json");

    private Collection<ConnectableAddress> addresses;

    private boolean autoUpdate;

    private String formatSplitter;

    private boolean notifyService;

    private String wrapperKey;

    private WebServerConfig webServerConfig;

    private List<WrapperMeta> wrappers;

    private boolean cloudDynamicServices;

    private boolean cloudDevServices;

    private Configuration config;

    private Document serviceDocument;

    private Document userDocument;

    private List<String> disabledModules;

    private List<String> cloudServerWrapperList;

    private Map<String, Object> networkProperties;

    public CloudConfig(ConsoleReader consoleReader) throws Exception
    {
        if (!Files.exists(Paths.get("local"))) Files.createDirectory(Paths.get("local"));
        if (!Files.exists(Paths.get("local/libs"))) Files.createDirectory(Paths.get("local/libs"));
        if (!Files.exists(Paths.get("local/templates"))) Files.createDirectory(Paths.get("local/templates"));
        if (!Files.exists(Paths.get("local/plugins"))) Files.createDirectory(Paths.get("local/plugins"));
        if (!Files.exists(Paths.get("local/cache"))) Files.createDirectory(Paths.get("local/cache"));
        if (!Files.exists(Paths.get("local/servers"))) Files.createDirectory(Paths.get("local/servers"));
        if (!Files.exists(Paths.get("local/servers/TestServer"))) Files.createDirectory(Paths.get("local/servers/TestServer"));
        if (!Files.exists(Paths.get("local/servers/TestServer/plugins"))) Files.createDirectory(Paths.get("local/servers/TestServer/plugins"));

        NetworkUtils.writeWrapperKey();

        defaultInit(consoleReader);
        defaultInitDoc(consoleReader);
        defaultInitUsers(consoleReader);
        load();
    }

    private void defaultInit(ConsoleReader consoleReader) throws Exception
    {
        if (Files.exists(configPath)) return;

        String hostName = NetworkUtils.getHostName();
        if (hostName.equals("127.0.0.1") || hostName.equals("127.0.1.1") || hostName.split("\\.").length != 4)
        {
            String input;
            System.out.println("Your IP address where located is 127.0.0.1 please write your service ip");
            while ((input = consoleReader.readLine()) != null)
            {
                if ((input.equals("127.0.0.1") || input.equals("127.0.1.1")) || input.split("\\.").length != 4)
                {
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
        configuration.set("general.cloudGameServer-wrapperList", Arrays.asList("Wrapper-1"));

        configuration.set("server.hostaddress", hostName);
        configuration.set("server.ports", Arrays.asList(1410));
        configuration.set("server.webservice.hostaddress", hostName);
        configuration.set("server.webservice.port", 1420);

        configuration.set("cloudnet-statistics.enabled", true);
        configuration.set("cloudnet-statistics.uuid", UUID.randomUUID().toString());

        configuration.set("networkproperties.test", true);

        if(Files.exists(Paths.get("config.properties")))
        {
            Properties properties = new Properties();
            try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(Paths.get("config.properties"))))
            {
                properties.load(inputStreamReader);
            }
            configuration.set("general.auto-update", Boolean.parseBoolean(properties.getProperty("autoupdate")));
            configuration.set("server.hostaddress", properties.getProperty("hostName"));
            configuration.set("server.webservice.hostaddress", properties.getProperty("hostName"));
            configuration.set("general.server-name-splitter", properties.getProperty("server-name-splitter"));
            Collection<Integer> integers = new ArrayList<>();
            for(JsonElement jsonElement : new JsonParser().parse(properties.getProperty("port")).getAsJsonArray()) integers.add(jsonElement.getAsInt());
            configuration.set("server.ports", integers);
            Files.deleteIfExists(Paths.get("config.properties"));
        }

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath), StandardCharsets.UTF_8))
        {
            configurationProvider.save(configuration, outputStreamWriter);
        }
    }

    private void defaultInitDoc(ConsoleReader consoleReader) throws Exception
    {
        if (Files.exists(servicePath)) return;

        String hostName = NetworkUtils.getHostName();
        if (hostName.equalsIgnoreCase("127.0.0.1") || hostName.equalsIgnoreCase("127.0.1.1") || hostName.split("\\.").length != 4)
        {
            String input;
            System.out.println("Please write the first Wrapper IP address:");
            while ((input = consoleReader.readLine()) != null)
            {
                if ((input.equalsIgnoreCase("127.0.0.1") || input.equalsIgnoreCase("127.0.0.1") || input.split("\\.").length != 4))
                {
                    System.out.println("Please write the real ip address :)");
                    continue;
                }

                hostName = input;
                break;
            }
        }

        if(Files.exists(Paths.get("groups.json")) && Files.exists(Paths.get("wrapper.json")))
        {
            Document service = new Document();

            {
                Document document = Document.loadDocument(Paths.get("wrapper.json"));
                Collection<WrapperMeta> wrapperMetas = new ArrayList<>();
                for(String wrapper : document.keys())
                {
                    wrapperMetas.add(new WrapperMeta(wrapper, document.getString(wrapper), "admin"));
                }
                service.append("wrapper", wrapperMetas);
            }

            {
                Document document = Document.loadDocument(Paths.get("groups.json"));
                Collection<ServerGroup> serverGroups = document.getObject("serverGroups", new TypeToken<Collection<ServerGroup>>(){}.getType());
                for(ServerGroup serverGroup : serverGroups) {
                    serverGroup.setAdvancedServerConfig(new AdvancedServerConfig(
                            false, false, false, !serverGroup.getGroupMode().equals(ServerGroupMode.STATIC)));
                    for(Template template : serverGroup.getTemplates())
                    {
                        template.setInstallablePlugins(Arrays.asList());
                        template.setProcessPreParameters(new String[]{});
                    }
                    serverGroup.setGlobalTemplate(new Template("globaltemplate", TemplateResource.LOCAL, null, new String[]{}, Arrays.asList()));
                }
                service.append("serverGroups", serverGroups);
                Collection<ProxyGroup> collection = document.getObject("proxyGroups", new TypeToken<Collection<ProxyGroup>>(){}.getType());
                for(ProxyGroup proxyGroup : collection)
                {
                    proxyGroup.getProxyConfig().setDynamicFallback(new DynamicFallback("Lobby", Arrays.asList(new ServerFallback("Lobby", null))));
                    proxyGroup.getTemplate().setProcessPreParameters(new String[]{});
                    proxyGroup.getTemplate().setInstallablePlugins(Arrays.asList());
                    proxyGroup.getProxyConfig().setCustomPayloadFixer(true);
                }
                service.append("proxyGroups", collection);
            }

            service.saveAsConfig(servicePath);
            Files.deleteIfExists(Paths.get("groups.json"));
            Files.deleteIfExists(Paths.get("wrapper.json"));
        }
        else
        {
            new Document("wrapper", Arrays.asList(new WrapperMeta("Wrapper-1", hostName, "admin")))
                    .append("serverGroups", Arrays.asList(new LobbyGroup()))
                    .append("proxyGroups", Arrays.asList(new BungeeGroup()))
                    .saveAsConfig(servicePath);
        }
    }

    private void defaultInitUsers(ConsoleReader consoleReader)
    {
        if (Files.exists(usersPath)) return;

        String password = NetworkUtils.randomString(8);
        System.out.println("\"admin\" Password: " + password);
        System.out.println(" ");
        new Document().append("users", Arrays.asList(new BasicUser("admin", password, Arrays.asList("*")))).saveAsConfig(usersPath);
    }

    public CloudConfig load() throws Exception
    {

        try (InputStream inputStream = Files.newInputStream(configPath); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        {
            Configuration configuration = configurationProvider.load(inputStreamReader);
            this.config = configuration;

            String host = configuration.getString("server.hostaddress");

            Collection<ConnectableAddress> addresses = new ArrayList<>();
            for (int value : configuration.getIntList("server.ports"))
            {
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
                    configuration.getInt("server.webservice.port")
            );
            this.formatSplitter = configuration.getString("general.server-name-splitter");
            this.networkProperties = configuration.getSection("networkproperties").self;
            //        configuration.set("general.disabled-modules", new ArrayList<>());
            if(!configuration.getSection("general").self.containsKey("disabled-modules"))
            {
                configuration.set("general.disabled-modules", new ArrayList<>());

                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath), StandardCharsets.UTF_8))
                {
                    configurationProvider.save(configuration, outputStreamWriter);
                }
            }

            if(!configuration.getSection("general").self.containsKey("cloudGameServer-wrapperList"))
            {
                configuration.set("general.cloudGameServer-wrapperList", Arrays.asList("Wrapper-1"));

                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath), StandardCharsets.UTF_8))
                {
                    configurationProvider.save(configuration, outputStreamWriter);
                }
            }

            this.disabledModules = configuration.getStringList("general.disabled-modules");
            this.cloudServerWrapperList = configuration.getStringList("general.cloudGameServer-wrapperList");
        }

        this.serviceDocument = Document.loadDocument(servicePath);

        this.wrappers = this.serviceDocument.getObject("wrapper", new TypeToken<List<WrapperMeta>>() {
        }.getType());

        this.userDocument = Document.loadDocument(usersPath);

        /* ============================================================== */
        return this;
    }

    public void createWrapper(WrapperMeta wrapperMeta)
    {
        Collection<WrapperMeta> wrapperMetas = this.serviceDocument.getObject("wrapper", new TypeToken<Collection<WrapperMeta>>(){}.getType());
        WrapperMeta is = CollectionWrapper.filter(wrapperMetas, new Acceptable<WrapperMeta>() {
            @Override
            public boolean isAccepted(WrapperMeta wrapperMeta_)
            {
                return wrapperMeta_.getId().equalsIgnoreCase(wrapperMeta.getId());
            }
        });
        if(is != null) wrapperMetas.remove(is);

        wrapperMetas.add(wrapperMeta);
        this.serviceDocument.append("wrapper", wrapperMetas).saveAsConfig(servicePath);
        CloudNet.getInstance().getWrappers().put(wrapperMeta.getId(), new Wrapper(wrapperMeta));
    }

    public Collection<User> getUsers()
    {
        if (this.userDocument == null) return null;
        return userDocument.getObject("users", new TypeToken<Collection<User>>() {
        }.getType());
    }

    public CloudConfig save(Collection<User> users)
    {
        if (userDocument != null)
            userDocument.append("users", users).saveAsConfig(usersPath);
        return this;
    }

    public void createGroup(@NonNull ServerGroup serverGroup)
    {
        Collection<ServerGroup> groups = this.serviceDocument.getObject("serverGroups", new TypeToken<Collection<ServerGroup>>() {
        }.getType());
        CollectionWrapper.checkAndRemove(groups, new Acceptable<ServerGroup>() {
            @Override
            public boolean isAccepted(ServerGroup value)
            {
                return value.getName().equals(serverGroup.getName());
            }
        });

        groups.add(serverGroup);
        this.serviceDocument.append("serverGroups", groups).saveAsConfig(servicePath);
    }

    public void createGroup(@NonNull ProxyGroup serverGroup)
    {
        Collection<ProxyGroup> groups = this.serviceDocument.getObject("proxyGroups", new TypeToken<Collection<ProxyGroup>>() {
        }.getType());
        CollectionWrapper.checkAndRemove(groups, new Acceptable<ProxyGroup>() {
            @Override
            public boolean isAccepted(ProxyGroup value)
            {
                return value.getName().equals(serverGroup.getName());
            }
        });

        groups.add(serverGroup);
        this.serviceDocument.append("proxyGroups", groups).saveAsConfig(servicePath);
    }

    public void deleteGroup(ServerGroup serverGroup)
    {
        Collection<ServerGroup> groups = this.serviceDocument.getObject("serverGroups", new TypeToken<Collection<ServerGroup>>() {
        }.getType());
        CollectionWrapper.checkAndRemove(groups, new Acceptable<ServerGroup>() {
            @Override
            public boolean isAccepted(ServerGroup value)
            {
                return value.getName().equals(serverGroup.getName());
            }
        });

        this.serviceDocument.append("serverGroups", groups).saveAsConfig(servicePath);
    }

    public void deleteGroup(ProxyGroup proxyGroup)
    {
        Collection<ProxyGroup> groups = this.serviceDocument.getObject("proxyGroups", new TypeToken<Collection<ProxyGroup>>() {
        }.getType());
        CollectionWrapper.checkAndRemove(groups, new Acceptable<ProxyGroup>() {
            @Override
            public boolean isAccepted(ProxyGroup value)
            {
                return value.getName().equals(proxyGroup.getName());
            }
        });
        this.serviceDocument.append("proxyGroups", groups).saveAsConfig(servicePath);
    }

    public java.util.Map<String, ServerGroup> getServerGroups()
    {
        Collection<ServerGroup> collection = serviceDocument.getObject("serverGroups", new TypeToken<Collection<ServerGroup>>() {
        }.getType());
        return MapWrapper.collectionCatcherHashMap(collection, new Catcher<String, ServerGroup>() {
            @Override
            public String doCatch(ServerGroup key)
            {
                return key.getName();
            }
        });
    }

    public Map<String, ProxyGroup> getProxyGroups()
    {
        Collection<ProxyGroup> collection = serviceDocument.getObject("proxyGroups", new TypeToken<Collection<ProxyGroup>>() {
        }.getType());

        boolean value = false;
        for(ProxyGroup proxyGroup : collection)
        {
            if(proxyGroup.getProxyConfig().getDynamicFallback() == null)
            {
                proxyGroup.getProxyConfig().setDynamicFallback(new DynamicFallback("Lobby", Arrays.asList(new ServerFallback("Lobby", null))));
                value = true;
            }
            if(proxyGroup.getProxyConfig().getCustomPayloadFixer() == null)
            {
                proxyGroup.getProxyConfig().setCustomPayloadFixer(false);
                value = true;
            }
        }

        if(value)
        {
            serviceDocument.append("proxyGroups", collection).saveAsConfig(servicePath);
        }

        return MapWrapper.collectionCatcherHashMap(collection, new Catcher<String, ProxyGroup>() {
            @Override
            public String doCatch(ProxyGroup key)
            {
                return key.getName();
            }
        });
    }

}