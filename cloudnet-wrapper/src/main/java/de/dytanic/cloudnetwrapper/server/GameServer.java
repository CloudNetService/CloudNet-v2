package de.dytanic.cloudnetwrapper.server;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.MasterTemplateLoader;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateLoader;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.PluginResourceType;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutAddServer;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutRemoveServer;
import de.dytanic.cloudnetwrapper.screen.AbstractScreenService;
import de.dytanic.cloudnetwrapper.server.process.ServerDispatcher;
import de.dytanic.cloudnetwrapper.server.process.ServerProcess;
import de.dytanic.cloudnetwrapper.util.FileUtility;
import de.dytanic.cloudnetwrapper.util.MasterTemplateDeploy;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class GameServer extends AbstractScreenService implements ServerDispatcher {

    private final ServerProcess serverProcess;
    private final ServerGroup serverGroup;
    private final Path dir;
    private Process instance;
    private ServerInfo serverInfo;
    private long startupTimeStamp = 0;

    public GameServer(ServerProcess serverProcess, ServerGroup serverGroup) {
        this.serverProcess = serverProcess;
        this.serverGroup = serverGroup;

        if (serverGroup.getGroupMode().equals(ServerGroupMode.STATIC) ||
            serverGroup.getGroupMode().equals(ServerGroupMode.STATIC_LOBBY)) {
            this.dir = Paths.get("local", "servers", serverGroup.getName(), this.serverProcess.getMeta().getServiceId().getServerId());
        } else {
            this.dir = Paths.get("temp", serverGroup.getName(), serverProcess.getMeta().getServiceId().toString());
        }
    }

    @Override
    public int hashCode() {
        int result = serverProcess != null ? serverProcess.hashCode() : 0;
        result = 31 * result + (serverGroup != null ? serverGroup.hashCode() : 0);
        result = 31 * result + (dir != null ? dir.hashCode() : 0);
        result = 31 * result + (instance != null ? instance.hashCode() : 0);
        result = 31 * result + (serverInfo != null ? serverInfo.hashCode() : 0);
        result = 31 * result + (int) (startupTimeStamp ^ (startupTimeStamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GameServer that = (GameServer) o;

        if (startupTimeStamp != that.startupTimeStamp) {
            return false;
        }
        if (!Objects.equals(serverProcess, that.serverProcess)) {
            return false;
        }
        if (!Objects.equals(serverGroup, that.serverGroup)) {
            return false;
        }
        if (!Objects.equals(dir, that.dir)) {
            return false;
        }
        if (!Objects.equals(instance, that.instance)) {
            return false;
        }
        return Objects.equals(serverInfo, that.serverInfo);
    }

    @Override
    public String toString() {
        return "GameServer{" +
            "serverProcess=" + serverProcess +
            ", serverGroup=" + serverGroup +
            ", dir=" + dir +
            ", instance=" + instance +
            ", serverInfo=" + serverInfo +
            ", startupTimeStamp=" + startupTimeStamp +
            "} " + super.toString();
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public ServerGroup getServerGroup() {
        return serverGroup;
    }

    public long getStartupTimeStamp() {
        return startupTimeStamp;
    }

    public Path getDir() {
        return dir;
    }

    /**
     * Prepare the game server
     *
     * @return Return true if the preparing successful, else return false
     *
     * @throws Exception Throws false if something wrong
     */
    @Override
    public boolean bootstrap() throws Exception {
        long startupTime = System.currentTimeMillis();

        for (ServerInstallablePlugin plugin : serverProcess.getMeta().getPlugins()) {
            downloadInstallablePlugin(plugin);
        }

        for (ServerInstallablePlugin url : serverProcess.getMeta().getTemplate().getInstallablePlugins()) {
            downloadInstallablePlugin(url);
        }

        if (serverGroup.getTemplates().size() == 0 && serverProcess.getMeta().getTemplateUrl() == null) {
            return false;
        }

        if (serverGroup.getGroupMode().equals(ServerGroupMode.STATIC) || serverGroup.getGroupMode().equals(ServerGroupMode.STATIC_LOBBY)) {
            if (!Files.exists(dir) && !templateDownloader()) {
                return false;
            }
        } else if (!templateDownloader()) {
            return false;
        }

        for (ServerInstallablePlugin plugin : serverProcess.getMeta().getPlugins()) {
            FileUtility.copyFileToDirectory(Paths.get("local", "cache", "web_plugins", plugin.getName() + ".jar"),
                                            this.dir.resolve("plugins"));
        }

        for (ServerInstallablePlugin plugin : serverProcess.getMeta().getTemplate().getInstallablePlugins()) {
            FileUtility.copyFileToDirectory(Paths.get("local", "cache", "web_plugins", plugin.getName() + ".jar"),
                                            this.dir.resolve("plugins"));
        }


        if (serverGroup.getServerType().equals(ServerGroupType.BUKKIT)) {
            if (!Files.exists(this.dir.resolve("spigot.jar"))) {
                FileUtility.copyFileToDirectory(Paths.get("local", "spigot.jar"), this.dir);
            }
        }
        copyConfigurations();
        copyCloudNetApi();
        FileUtility.copyFilesInDirectory(Paths.get("local", "global"), this.dir);

        if (!serverGroup.getServerType().equals(ServerGroupType.GLOWSTONE)) {
            this.serverInfo = configureNormalServer();
        } else {
            this.serverInfo = configureGlowstoneServer();
        }
        generateCloudNetConfigurations();

        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutAddServer(this.serverInfo,
                                                                                               this.serverProcess.getMeta()));
        CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO,
                                                               String.format("Server %s started in [%d] milliseconds",
                                                                             this,
                                                                             (System.currentTimeMillis() - startupTime)));
        this.startupTimeStamp = System.currentTimeMillis();

        startProcess();

        CloudNetWrapper.getInstance().getServers().put(this.serverProcess.getMeta().getServiceId().getServerId(), this);
        return true;
    }

    /**
     * Download the {@link ServerInstallablePlugin] and copy to a plugin cache for future installations.
     *
     * @param plugin The ServerInstallable plugin to download it.
     */
    public static void downloadInstallablePlugin(ServerInstallablePlugin plugin) {
        final Path path = Paths.get("local", "cache", "web_plugins", plugin.getName() + ".jar");
        if (plugin.getPluginResourceType().equals(PluginResourceType.URL)) {
            if (!Files.exists(path)) {
                try {
                    URLConnection urlConnection = new URL(plugin.getUrl()).openConnection();
                    urlConnection.setRequestProperty("User-Agent",
                                                     NetworkUtils.USER_AGENT);
                    Files.copy(urlConnection.getInputStream(), path);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (plugin.getPluginResourceType().equals(PluginResourceType.MASTER)) {
            if (!Files.exists(path) && CloudNetWrapper.getInstance()
                                                      .getSimpledUser() != null) {
                try {
                    URLConnection urlConnection = new URL(
                        String.format("http://%s:%d/cloudnet/api/v1/download",
                                      CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost(),
                                      CloudNetWrapper.getInstance().getWrapperConfig().getWebPort())).openConnection();
                    urlConnection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);

                    SimpledUser simpledUser = CloudNetWrapper.getInstance().getSimpledUser();
                    urlConnection.setRequestProperty("-Xcloudnet-user", simpledUser.getUserName());
                    urlConnection.setRequestProperty("-Xcloudnet-token", simpledUser.getApiToken());
                    urlConnection.setRequestProperty("-Xmessage", "plugin");
                    urlConnection.setRequestProperty("-Xvalue", plugin.getName());

                    urlConnection.connect();
                    CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO,
                                                                           String.format("Downloading %s.jar", plugin.getName()));
                    Files.copy(urlConnection.getInputStream(), path);
                    CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO, "Download was completed successfully!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Download the template from master.
     *
     * @return return true if success the download, else  return false.
     *
     * @throws Exception throws if something wrong.
     */
    private boolean templateDownloader() throws Exception {
        Files.createDirectories(dir);

        {
            Template template = this.serverGroup.getGlobalTemplate();
            if (template.getBackend().equals(TemplateResource.URL) && template.getUrl() != null) {
                downloadURL(template);
                return true;
            } else if (template.getBackend().equals(TemplateResource.MASTER) && CloudNetWrapper.getInstance().getSimpledUser() != null) {
                downloadTemplate(template);
                return true;
            } else if (Files.exists(Paths.get("local", "templates", serverGroup.getName(), template.getName()))) {
                FileUtility.copyFilesInDirectory(Paths.get("local", "templates", serverGroup.getName(), template.getName()), this.dir);
            }
        }

        if (serverProcess.getMeta().getTemplateUrl() != null) {
            if (!Files.exists(this.dir.resolve("plugins"))) {
                Files.createDirectory(this.dir.resolve("plugins"));
            }

            TemplateLoader templateLoader = new TemplateLoader(serverProcess.getMeta().getTemplateUrl(), this.dir.resolve("template.zip"));
            CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO,
                                                                   String.format("Downloading template for %s %s",
                                                                                 this.serverProcess.getMeta().getServiceId().getServerId(),
                                                                                 serverProcess
                                                                                     .getMeta()
                                                                                     .getTemplateUrl()));
            templateLoader.load();
            templateLoader.unZip(this.dir);
        } else {

            if (!Files.exists(this.dir.resolve("plugins"))) {
                Files.createDirectory(this.dir.resolve("plugins"));
            }

            if (serverGroup.getTemplates().size() == 0) {
                return false;
            }

            Template template = this.serverProcess.getMeta().getTemplate();
            final Path sourcePath = Paths.get("local", "templates", serverGroup.getName(), template.getName());
            if (template.getBackend().equals(TemplateResource.URL) && template.getUrl() != null) {
                downloadURL(template);
                return true;
            } else if (template.getBackend().equals(TemplateResource.MASTER) && CloudNetWrapper.getInstance().getSimpledUser() != null) {
                downloadTemplate(template);
                return true;
            } else if (Files.exists(sourcePath)) {
                FileUtility.copyFilesInDirectory(sourcePath, this.dir);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Copy some config files for different server types.
     */
    private void copyConfigurations() {
        if (serverGroup.getServerType().equals(ServerGroupType.GLOWSTONE)) {
            final Path configPath = this.dir.resolve("config");
            if (!Files.exists(configPath)) {
                try {
                    Files.createDirectories(configPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!Files.exists(configPath.resolve("glowstone.yml"))) {
                FileUtility.insertData("files/glowstone.yml", configPath.resolve("glowstone.yml"));
            }
        }

        if (!Files.exists(this.dir.resolve("server.properties"))) {
            FileUtility.insertData("files/server.properties", this.dir.resolve("server.properties"));
        }

        if (!Files.exists(this.dir.resolve("bukkit.yml"))) {
            FileUtility.insertData("files/bukkit.yml", this.dir.resolve("bukkit.yml"));
        }

        if (!Files.exists(this.dir.resolve("spigot.yml"))) {
            FileUtility.insertData("files/spigot.yml", this.dir.resolve("spigot.yml"));
        }
    }

    /**
     * Copy the cloudnet api from wrapper to server folder.
     */
    private void copyCloudNetApi() {
        try {
            final Path pluginPath = this.dir.resolve("plugins");
            Files.deleteIfExists(pluginPath.resolve("CloudNetAPI.jar"));
            FileUtility.insertData("files/CloudNetAPI.jar", pluginPath.resolve("CloudNetAPI.jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configure a normal game server with server.properties depend of groups settings and set ip and port right.
     *
     * @return the finished server info of the configured server.
     */
    private ServerInfo configureNormalServer() {
        Properties properties = new Properties();
        final Path serverPropertiesPath = this.dir.resolve("server.properties");
        try (Reader reader = new InputStreamReader(Files.newInputStream(serverPropertiesPath))) {
            try {
                properties.load(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ((properties.isEmpty() || !properties.containsKey("max-players"))) {
            properties.setProperty("max-players", "100");
            FileUtility.insertData("files/server.properties", this.dir.resolve("server.properties"));
            try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(this.dir.resolve("server.properties")))) {
                properties.load(inputStreamReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (this.serverGroup.getGroupMode() == ServerGroupMode.STATIC ||
                this.serverGroup.getGroupMode() == ServerGroupMode.STATIC_LOBBY) {
                CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.WARNING,
                                                                       String.format(
                                                                           "Filled empty server.properties (or missing \"max-players\" entry) of server [%s] at %s",
                                                                           this.serverProcess.getMeta().getServiceId(),
                                                                           this.dir.resolve("server.properties").toAbsolutePath()));
            } else {
                CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.WARNING,
                                                                       String.format(
                                                                           "Filled empty server.properties (or missing \"max-players\" entry) of server [%s], please fix this error in the server.properties (check the template [%s] and the global directory [%s])",
                                                                           this.serverProcess.getMeta().getServiceId(),
                                                                           (this.serverProcess
                                                                               .getMeta()
                                                                               .getTemplate()
                                                                               .getName() + '@' + this.serverProcess.getMeta()
                                                                                                                    .getTemplate()
                                                                                                                    .getBackend()),
                                                                           new File("local/global").getAbsolutePath()));
            }
        }

        Enumeration<Object> enumeration = this.serverProcess.getMeta().getProperties().keys();
        while (enumeration.hasMoreElements()) {
            String x = enumeration.nextElement().toString();
            properties.setProperty(x, this.serverProcess.getMeta().getProperties().getProperty(x));
        }

        properties.setProperty("server-ip", CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP());
        properties.setProperty("server-port", serverProcess.getMeta().getPort() + NetworkUtils.EMPTY_STRING);

        String motd = properties.getProperty("motd");
        int maxPlayers;
        try {
            maxPlayers = Integer.parseInt(properties.getProperty("max-players"));
        } catch (NumberFormatException e) {
            maxPlayers = 100;
        }

        try (OutputStream outputStream = Files.newOutputStream(serverPropertiesPath)) {
            properties.store(outputStream, "CloudNet-Wrapper EDIT");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ServerInfo(serverProcess.getMeta().getServiceId(),
                              CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP(),
                              this.serverProcess.getMeta().getPort(),
                              false,
                              new ArrayList<>(),
                              serverProcess.getMeta().getMemory(),
                              motd,
                              0,
                              maxPlayers,
                              ServerState.OFFLINE,
                              this.serverProcess.getMeta().getServerConfig(),
                              serverProcess.getMeta().getTemplate());
    }

    /**
     * Configure a game server that's compatible with glowstone and set some properties.
     *
     * @return Given back a complete server info
     */
    private ServerInfo configureGlowstoneServer() {
        String motd = null;
        int maxPlayers = 0;
        final Path configPath = this.dir.resolve("config");
        try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(configPath.resolve("glowstone.yml")),
                                                                         StandardCharsets.UTF_8)) {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);
            Configuration section = configuration.getSection("server");
            section.set("ip", CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP());
            section.set("port", serverProcess.getMeta().getPort());

            maxPlayers = section.getInt("max-players");
            motd = section.getString("motd");

            configuration.set("server", section);
            configuration.set("console.use-jline", false);
            try (Writer writer = new OutputStreamWriter(Files.newOutputStream(configPath.resolve("glowstone.yml")),
                                                        StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ServerInfo(serverProcess.getMeta().getServiceId(),
                              CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP(),
                              this.serverProcess.getMeta().getPort(),
                              false,
                              new ArrayList<>(),
                              serverProcess.getMeta().getMemory(),
                              motd,
                              0,
                              maxPlayers,
                              ServerState.OFFLINE,
                              this.serverProcess.getMeta().getServerConfig(),
                              serverProcess.getMeta().getTemplate());
    }

    private void generateCloudNetConfigurations() {
        final Path cloudPath = this.dir.resolve("CLOUD");
        if (!Files.exists(cloudPath)) {
            try {
                Files.createDirectory(cloudPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new Document().append("serviceId", serverProcess.getMeta().getServiceId())
                      .append("serverProcess", serverProcess.getMeta())
                      .append("serverInfo", serverInfo)
                      .append("memory", serverProcess.getMeta().getMemory())
                      .saveAsConfig(cloudPath.resolve("config.json"));

        new Document("connection",
                     new ConnectableAddress(CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost(),
                                            CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetPort()))
            .saveAsConfig(cloudPath.resolve("connection.json"));

    }

    /**
     * Start the java process
     *
     * @throws Exception Throws some exception, if something wrong on the start of a game server
     */
    private void startProcess() throws Exception {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("java ");
        for (String command : serverProcess.getMeta().getJavaProcessParameters()) {
            commandBuilder.append(command).append(NetworkUtils.SPACE_STRING);
        }

        for (String command : serverProcess.getMeta().getTemplate().getProcessPreParameters()) {
            commandBuilder.append(command).append(NetworkUtils.SPACE_STRING);
        }

        commandBuilder.append("-Dfile.encoding=UTF-8 -Dcom.mojang.eula.agree=true -Djline.terminal=jline.UnsupportedTerminal -Xmx")
                      .append(serverProcess.getMeta().getMemory())
                      .append("M -jar ");

        switch (serverGroup.getServerType()) {
            case CAULDRON:
                commandBuilder.append("cauldron.jar nogui");
                break;
            case GLOWSTONE:
                commandBuilder.append("glowstone.jar nogui");
                break;
            case CUSTOM:
                commandBuilder.append("minecraft_server.jar nogui");
                break;
            default:
                commandBuilder.append("spigot.jar nogui");
                break;
        }

        this.instance = Runtime.getRuntime().exec(commandBuilder.toString().split(NetworkUtils.SPACE_STRING), null, this.dir.toFile());
    }

    /**
     * Download the template from url.
     *
     * @param template The information about the template.
     *
     * @throws IOException Throws is something wrong.
     */
    private void downloadURL(Template template) throws IOException {
        final Path groupTemplates = Paths.get("local", "cache", "web_templates", serverGroup.getName(), template.getName());
        if (!Files.exists(groupTemplates)) {
            Files.createDirectories(groupTemplates);
            TemplateLoader templateLoader = new TemplateLoader(template.getUrl(), groupTemplates.resolve("template.zip"));
            CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO,
                                                                   String.format("Downloading template for %s %s",
                                                                                 this.serverProcess.getMeta().getServiceId().getGroup(),
                                                                                 template.getName()));
            templateLoader.load();
            templateLoader.unZip(groupTemplates);
        }
        FileUtility.copyFilesInDirectory(groupTemplates, this.dir);
    }

    /**
     * Download the template from template information's.
     *
     * @param template The template with the information's.
     *
     * @throws IOException Throws is something wrong
     */
    private void downloadTemplate(Template template) throws IOException {
        final Path groupTemplates = Paths.get("local", "cache", "web_templates", serverGroup.getName(), template.getName());
        if (!Files.exists(groupTemplates)) {
            Files.createDirectories(groupTemplates);
            MasterTemplateLoader templateLoader = new MasterTemplateLoader(
                String.format("http://%s:%d/cloudnet/api/v1/download",
                              CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost(),
                              CloudNetWrapper.getInstance().getWrapperConfig().getWebPort()),
                groupTemplates.resolve("template.zip"),
                CloudNetWrapper.getInstance().getSimpledUser(),
                template,
                serverGroup.getName());
            CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO,
                                                                   String.format("Downloading template for %s %s",
                                                                                 this.serverProcess.getMeta()
                                                                                                   .getServiceId()
                                                                                                   .getGroup(),
                                                                                 template.getName()));

            templateLoader.load();
            templateLoader.unZip(groupTemplates);
        }
        FileUtility.copyFilesInDirectory(groupTemplates, this.dir);
    }

    /**
     * Shutdown the game server
     *
     * @return Return true if successful, else false
     */
    @Override
    public boolean shutdown() {

        if (instance == null) {
            if (serverGroup.getGroupMode().equals(ServerGroupMode.DYNAMIC)) {
                FileUtility.deleteDirectory(this.dir);
            }
            return true;
        }

        kill();

        if (CloudNetWrapper.getInstance().getWrapperConfig().isSavingRecords()) {
            try {
                final Path directory = Paths.get("local", "records", serverProcess.getMeta().getServiceId().toString());

                FileUtility.copyFilesInDirectory(this.dir.resolve("logs"), directory);
                FileUtility.copyFilesInDirectory(this.dir.resolve("crash-reports"), directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (serverGroup.isMaintenance() &&
            CloudNetWrapper.getInstance().getWrapperConfig().isMaintenanceCopy() &&
            !serverGroup.getGroupMode().equals(ServerGroupMode.STATIC) &&
            !serverGroup.getGroupMode().equals(ServerGroupMode.STATIC_LOBBY)) {
            copy();
        }

        if (!serverGroup.getGroupMode().equals(ServerGroupMode.STATIC) &&
            !serverGroup.getGroupMode().equals(ServerGroupMode.STATIC_LOBBY)) {
            try {
                FileUtility.deleteDirectory(dir);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        CloudNetWrapper.getInstance().getServers().remove(getServiceId().getServerId());
        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveServer(serverInfo));
        CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO, String.format("Server %s was stopped", this));
        return true;
    }

    /**
     * Kill the process.
     */
    public void kill() {
        if (instance.isAlive()) {
            executeCommand("stop");
            try {
                if (!instance.waitFor(60, TimeUnit.SECONDS)) {
                    instance.destroyForcibly();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                instance.destroyForcibly();
            }
        }
    }

    /**
     * Copy the template to the temporary folder
     */
    public void copy() {
        copy(this.serverInfo.getTemplate());
    }

    @Override
    public ServiceId getServiceId() {
        return serverProcess.getMeta().getServiceId();
    }

    @Override
    public Process getInstance() {
        return instance;
    }

    /**
     * Copy the template to the temporary folder
     *
     * @param template The template with information
     */
    public void copy(Template template) {

        if (!serverGroup.getTemplates().contains(template)) {
            return;
        }

        if (instance != null && instance.isAlive()) {
            executeCommand("save-all");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (template != null && template.getBackend().equals(TemplateResource.MASTER)) {
            MasterTemplateDeploy masterTemplateDeploy =
                new MasterTemplateDeploy(this.dir,
                                         new ConnectableAddress(CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost(),
                                                                CloudNetWrapper.getInstance().getWrapperConfig().getWebPort()),
                                         CloudNetWrapper.getInstance().getSimpledUser(),
                                         template,
                                         serverGroup.getName());
            try {
                masterTemplateDeploy.deploy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (template != null) {
            CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO,
                                                                   String.format("Copying template from %s to local directory...",
                                                                                 this.serverProcess.getMeta().getServiceId()));
            try {
                FileUtility.copyFilesInDirectory(this.dir, Paths.get("local", "templates", serverGroup.getName(), template.getName()));

                FileUtility.deleteDirectory(Paths.get("local",
                                                      "templates",
                                                      serverGroup.getName(),
                                                      serverProcess.getMeta().getTemplate().getName(),
                                                      "CLOUD"));

                Files.deleteIfExists(Paths.get("local",
                                               "templates",
                                               serverGroup.getName(),
                                               template.getName(),
                                               "plugins",
                                               "CloudNetAPI.jar"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO,
                                                                   String.format("Template %s was copied!", template.getName()));
        }
    }

    public ServerProcess getServerProcess() {
        return serverProcess;
    }

    /**
     * Restart the game server
     */
    public void restart() {

        kill();
        CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO, String.format("Server %s was killed and restart...", this));
        try {
            startProcess();
            startupTimeStamp = System.currentTimeMillis();
            CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO, String.format("Server %s restarted now!", this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Copy folder
     *
     * @param name The name of the folder.
     */
    public void copyDirectory(String name) {
        final Path sourcePath = this.dir.resolve(name);
        if (Files.exists(sourcePath) && Files.isDirectory(sourcePath)) {
            try {
                FileUtility.copyFilesInDirectory(sourcePath,
                                                 Paths.get("local",
                                                           "templates",
                                                           serverGroup.getName(),
                                                           serverProcess.getMeta().getTemplate().getName(),
                                                           name));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
