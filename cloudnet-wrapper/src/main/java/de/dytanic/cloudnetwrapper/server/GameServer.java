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

    private ServerProcess serverProcess;
    private ServerStage serverStage;
    private ServerGroup serverGroup;
    private Process instance;
    private ServerInfo serverInfo;
    private long startupTimeStamp = 0;
    private Path dir;
    private String path;

    public GameServer(ServerProcess serverProcess, ServerStage serverStage, ServerGroup serverGroup) {
        this.serverProcess = serverProcess;
        this.serverStage = serverStage;
        this.serverGroup = serverGroup;

        if (serverGroup.getGroupMode().equals(ServerGroupMode.STATIC) || serverGroup.getGroupMode()
                                                                                    .equals(ServerGroupMode.STATIC_LOBBY)) {
            this.path = "local/servers/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + this.serverProcess.getMeta()
                                                                                                                 .getServiceId()
                                                                                                                 .getServerId();
        } else {
            this.path = "temp/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + serverProcess.getMeta().getServiceId();
        }

        this.dir = Paths.get(path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverProcess, serverStage, serverGroup, instance, serverInfo, startupTimeStamp, dir, path);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GameServer)) {
            return false;
        }
        final GameServer that = (GameServer) o;
        return startupTimeStamp == that.startupTimeStamp &&
            Objects.equals(serverProcess, that.serverProcess) &&
            serverStage == that.serverStage &&
            Objects.equals(serverGroup, that.serverGroup) &&
            Objects.equals(instance, that.instance) &&
            Objects.equals(serverInfo, that.serverInfo) &&
            Objects.equals(dir, that.dir) &&
            Objects.equals(path, that.path);
    }

    @Override
    public String toString() {
        return "de.dytanic.cloudnetwrapper.server.GameServer{" +
            "serverProcess=" + serverProcess +
            ", serverStage=" + serverStage +
            ", serverGroup=" + serverGroup +
            ", instance=" + instance +
            ", serverInfo=" + serverInfo +
            ", startupTimeStamp=" + startupTimeStamp +
            ", dir=" + dir +
            ", path='" + path + '\'' +
            "} " + super.toString();
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public String getPath() {
        return path;
    }

    public ServerGroup getServerGroup() {
        return serverGroup;
    }

    public long getStartupTimeStamp() {
        return startupTimeStamp;
    }

    public ServerStage getServerStage() {
        return serverStage;
    }

    public Path getDir() {
        return dir;
    }

    /**
     * Download the {@link ServerInstallablePlugin] an copy to a web plugin cache.
     *
     * @param plugin The ServerInstallable plugin to download it.
     */
    private void downloadInstallablePlugin(ServerInstallablePlugin plugin) {
        switch (plugin.getPluginResourceType()) {
            case URL: {
                if (!Files.exists(Paths.get("local/cache/web_plugins/" + plugin.getName() + ".jar"))) {
                    try {
                        URLConnection urlConnection = new URL(plugin.getUrl()).openConnection();
                        urlConnection.setRequestProperty("User-Agent",
                                                         NetworkUtils.USER_AGENT);
                        Files.copy(urlConnection.getInputStream(), Paths.get("local/cache/web_plugins/" + plugin.getName() + ".jar"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            break;
            case MASTER: {
                if (!Files.exists(Paths.get("local/cache/web_plugins/" + plugin.getName() + ".jar")) && CloudNetWrapper.getInstance()
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
                        Files.copy(urlConnection.getInputStream(), Paths.get("local/cache/web_plugins/" + plugin.getName() + ".jar"));
                        CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.INFO, "Download was completed successfully!");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    /**
     * Copy the cloudnet api from wrapper to server folder.
     */
    private void copyCloudNetApi() {
        try {
            Files.deleteIfExists(Paths.get(path, "plugins", "CloudNetAPI.jar"));
            FileUtility.insertData("files/CloudNetAPI.jar", path + "/plugins/CloudNetAPI.jar");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copy some config files for different server types.
     */
    private void copyConfigurations() {
        if (serverGroup.getServerType().equals(ServerGroupType.GLOWSTONE)) {
            if (!Files.exists(Paths.get(path + "/config"))) {
                try {
                    Files.createDirectories(Paths.get(path, "config"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!Files.exists(Paths.get(path + "/config/glowstone.yml"))) {
                FileUtility.insertData("files/glowstone.yml", path + "/config/glowstone.yml");
            }
        }

        if (!Files.exists(Paths.get(path, "server.properties"))) {
            FileUtility.insertData("files/server.properties", path + "/server.properties");
        }

        if (!Files.exists(Paths.get(path, "bukkit.yml"))) {
            FileUtility.insertData("files/bukkit.yml", path + "/bukkit.yml");
        }

        if (!Files.exists(Paths.get(path + "/spigot.yml"))) {
            FileUtility.insertData("files/spigot.yml", path + "/spigot.yml");
        }
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
        serverProcess.setServerStage(ServerStage.DOWNLOAD);
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
            FileUtility.copyFileToDirectory(new File("local/cache/web_plugins/" + plugin.getName() + ".jar"), new File(path + "/plugins"));
        }

        for (ServerInstallablePlugin plugin : serverProcess.getMeta().getTemplate().getInstallablePlugins()) {
            FileUtility.copyFileToDirectory(new File("local/cache/web_plugins/" + plugin.getName() + ".jar"), new File(path + "/plugins"));
        }

        serverProcess.setServerStage(ServerStage.COPY);

        if (serverGroup.getServerType().equals(ServerGroupType.BUKKIT)) {
            if (!Files.exists(Paths.get(path + "/spigot.jar"))) {
                FileUtility.copyFileToDirectory(new File("local/spigot.jar"), new File(path));
            }
        }
        copyConfigurations();
        copyCloudNetApi();
        FileUtility.copyFilesInDirectory(new File("local/global"), new File(path));

        if (!serverGroup.getServerType().equals(ServerGroupType.GLOWSTONE)) {
            this.serverInfo = configureNormalServer();
        } else {
            this.serverInfo = configureNonNormalServer();
        }
        generateCloudNetConfigurations();

        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutAddServer(this.serverInfo,
                                                                                               this.serverProcess.getMeta()));
        System.out.println("Server " + this + " started in [" + (System.currentTimeMillis() - startupTime) + " milliseconds]");
        this.startupTimeStamp = System.currentTimeMillis();

        startProcess();

        serverProcess.setServerStage(ServerStage.PROCESS);
        CloudNetWrapper.getInstance().getServers().put(this.serverProcess.getMeta().getServiceId().getServerId(), this);
        serverProcess.setServerStage(ServerStage.NET_INIT);
        return true;
    }

    private void generateCloudNetConfigurations() {
        if (!Files.exists(Paths.get(path + "/CLOUD"))) {
            try {
                Files.createDirectory(Paths.get(path + "/CLOUD"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new Document().append("serviceId", serverProcess.getMeta().getServiceId())
                      .append("serverProcess", serverProcess.getMeta())
                      .append("serverInfo", serverInfo)
                      .append("memory", serverProcess.getMeta().getMemory())
                      .saveAsConfig(Paths.get(path + "/CLOUD/config.json"));

        new Document().append("connection",
                              new ConnectableAddress(CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost(),
                                                     CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetPort())).saveAsConfig(Paths
                                                                                                                                           .get(
                                                                                                                                               path + "/CLOUD/connection.json"));

    }

    /**
     * Configure a game server that's compatible with glowstone and set some properties.
     *
     * @return Given back a complete server info
     */
    private ServerInfo configureNonNormalServer() {
        String motd = null;
        int maxPlayers = 0;
        try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(Paths.get(path + "/config/glowstone.yml")),
                                                                         StandardCharsets.UTF_8)) {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);
            Configuration section = configuration.getSection("server");
            section.set("ip", CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP());
            section.set("port", serverProcess.getMeta().getPort());

            maxPlayers = section.getInt("max-players");
            motd = section.getString("motd");

            configuration.set("server", section);
            configuration.set("console.use-jline", false);
            try (Writer writer = new OutputStreamWriter(Files.newOutputStream(Paths.get(path, "config", "glowstone.yml")),
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

    /**
     * Configure a normal game server with server.properties depend of groups settings and set ip and port right.
     *
     * @return the finished server info of the configured server.
     */
    private ServerInfo configureNormalServer() {
        String motd;
        int maxPlayers;
        Properties properties = new Properties();
        try (Reader reader = new InputStreamReader(Files.newInputStream(Paths.get(path,  "server.properties")))) {
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
            FileUtility.insertData("files/server.properties", path + "/server.properties");
            try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(Paths.get(path + "/server.properties")))) {
                properties.load(inputStreamReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (this.serverGroup.getGroupMode() == ServerGroupMode.STATIC || this.serverGroup.getGroupMode() == ServerGroupMode.STATIC_LOBBY) {
                CloudNetWrapper.getInstance().getCloudNetLogging().log(Level.WARNING,
                                                                       String.format(
                                                                           "Filled empty server.properties (or missing \"max-players\" entry) of server [%s] at %s",
                                                                           this.serverProcess.getMeta().getServiceId(),
                                                                           new File(path, "server.properties").getAbsolutePath()));
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

        motd = properties.getProperty("motd");
        try {
            maxPlayers = Integer.parseInt(properties.getProperty("max-players"));
        } catch (NumberFormatException e) {
            maxPlayers = 100;
        }

        try (OutputStream outputStream = Files.newOutputStream(Paths.get(path + "/server.properties"))) {
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
     * Shutdown the game server
     *
     * @return Return true if successful, else false
     */
    @Override
    public boolean shutdown() {

        if (instance == null) {
            if (serverGroup.getGroupMode().equals(ServerGroupMode.DYNAMIC)) {
                FileUtility.deleteDirectory(dir.toFile());
            }
            return true;
        }

        kill();

        if (CloudNetWrapper.getInstance().getWrapperConfig().isSavingRecords()) {
            try {
                File directory = new File("local/records/" + serverProcess.getMeta().getServiceId());

                FileUtility.copyFilesInDirectory(new File(path + "/logs"), directory);
                FileUtility.copyFilesInDirectory(new File(path + "/crash-reports"), directory);

                new Document("meta", serverProcess.getMeta()).saveAsConfig(Paths.get("local/records/" + serverProcess.getMeta()
                                                                                                                     .getServiceId() + "/metadata.json"));
            } catch (IOException ignored) {
            }
        }

        if (serverGroup.isMaintenance() && (CloudNetWrapper.getInstance()
                                                           .getWrapperConfig()
                                                           .isMaintenance_copy() && !serverGroup.getGroupMode()
                                                                                                .equals(ServerGroupMode.STATIC))) {
            copy();
        }

        if (!serverGroup.getGroupMode().equals(ServerGroupMode.STATIC) && !serverGroup.getGroupMode()
                                                                                      .equals(ServerGroupMode.STATIC_LOBBY)) {
            try {
                FileUtility.deleteDirectory(dir.toFile());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        CloudNetWrapper.getInstance().getServers().remove(getServiceId().getServerId());
        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveServer(serverInfo));
        System.out.println("Server " + this + " was stopped");
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
                FileUtility.copyFilesInDirectory(new File("local/templates/" + serverGroup.getName(), template.getName()), new File(path));
            }
        }

        if (serverProcess.getMeta().getTemplateUrl() != null) {
            if (!Files.exists(Paths.get(path + "/plugins"))) {
                Files.createDirectory(Paths.get(path + "/plugins"));
            }

            TemplateLoader templateLoader = new TemplateLoader(serverProcess.getMeta().getTemplateUrl(), path + "/template.zip");
            System.out.println("Downloading template for " + this.serverProcess.getMeta().getServiceId().getServerId() + ' ' + serverProcess
                .getMeta()
                .getTemplateUrl());
            templateLoader.load();
            templateLoader.unZip(path);
        } else {

            if (!Files.exists(Paths.get(path + "/plugins"))) {
                Files.createDirectory(Paths.get(path + "/plugins"));
            }

            if (serverGroup.getTemplates().size() == 0) {
                return false;
            }

            Template template = this.serverProcess.getMeta().getTemplate();
            if (template.getBackend().equals(TemplateResource.URL) && template.getUrl() != null) {
                downloadURL(template);
                return true;
            } else if (template.getBackend().equals(TemplateResource.MASTER) && CloudNetWrapper.getInstance().getSimpledUser() != null) {
                downloadTemplate(template);
                return true;
            } else if (Files.exists(Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName()))) {
                FileUtility.copyFilesInDirectory(new File("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName()),
                                                 new File(path));
            } else {
                return false;
            }
        }
        return true;
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

        this.instance = Runtime.getRuntime().exec(commandBuilder.toString().split(NetworkUtils.SPACE_STRING), null, new File(path));
    }

    public ServerProcess getServerProcess() {
        return serverProcess;
    }

    /**
     * Download the template from template information's.
     *
     * @param template The template with the information's.
     *
     * @throws IOException Throws is something wrong
     */
    private void downloadTemplate(Template template) throws IOException {
        String groupTemplates = "local/cache/web_templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName();
        if (!Files.exists(Paths.get(groupTemplates))) {
            Files.createDirectories(Paths.get(groupTemplates));
            MasterTemplateLoader templateLoader = new MasterTemplateLoader(
                String.format("http://%s:%d/cloudnet/api/v1/download",
                              CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost(),
                              CloudNetWrapper.getInstance().getWrapperConfig().getWebPort()),
                groupTemplates + "/template.zip",
                CloudNetWrapper.getInstance().getSimpledUser(),
                template,
                serverGroup.getName());
            System.out.println("Downloading template for " + this.serverProcess.getMeta()
                                                                               .getServiceId()
                                                                               .getGroup() + ' ' + template.getName());
            templateLoader.load();
            templateLoader.unZip(groupTemplates);
        }
        FileUtility.copyFilesInDirectory(new File(groupTemplates), new File(path));
    }

    /**
     * Download the template from url.
     *
     * @param template The information about the template.
     *
     * @throws IOException Throws is something wrong.
     */
    private void downloadURL(Template template) throws IOException {
        String groupTemplates = "local/cache/web_templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName();
        if (!Files.exists(Paths.get(groupTemplates))) {
            Files.createDirectories(Paths.get(groupTemplates));
            TemplateLoader templateLoader = new TemplateLoader(template.getUrl(), groupTemplates + "/template.zip");
            System.out.println("Downloading template for " + this.serverProcess.getMeta()
                                                                               .getServiceId()
                                                                               .getGroup() + ' ' + template.getName());
            templateLoader.load();
            templateLoader.unZip(groupTemplates);
        }
        FileUtility.copyFilesInDirectory(new File(groupTemplates), new File(path));
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
                new MasterTemplateDeploy(path,
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
            System.out.println("Copying template from " + this.serverProcess.getMeta().getServiceId() + " to local directory...");

            try {
                FileUtility.copyFilesInDirectory(Paths.get(this.path),
                                                 Paths.get("local", "templates", serverGroup.getName(), template.getName()));
                FileUtility.deleteDirectory(Paths.get("local",
                                                      "templates",
                                                      serverGroup.getName(),
                                                      serverProcess.getMeta().getTemplate().getName(),
                                                      "CLOUD"));

                Files.deleteIfExists(Paths.get("local", "templates", serverGroup.getName(), serverProcess.getMeta().getTemplate().getName(),
                                               "plugins", "CloudNetAPI.jar"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Template " + template.getName() + " was copied!");
        }
    }

    /**
     * Restart the game server
     */
    public void restart() {

        kill();
        System.out.println("Server " + this + " was killed and restart...");
        try {
            startProcess();
            startupTimeStamp = System.currentTimeMillis();
            System.out.println("Server " + this + " restarted now!");
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
        File file = new File(path, name);

        if (file.exists() && file.isDirectory()) {
            try {
                FileUtility.copyFilesInDirectory(file,
                                                 new File("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + serverProcess
                                                     .getMeta()
                                                     .getTemplate()
                                                     .getName() + NetworkUtils.SLASH_STRING + name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
