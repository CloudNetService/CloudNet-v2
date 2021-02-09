/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.wrapper.server;

import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.server.*;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.MasterTemplateLoader;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.TemplateLoader;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.TemplateResource;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.service.plugin.PluginResourceType;
import eu.cloudnetservice.cloudnet.v2.lib.service.plugin.ServerInstallablePlugin;
import eu.cloudnetservice.cloudnet.v2.lib.user.SimpledUser;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.logging.CloudLogger;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out.PacketOutAddServer;
import eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out.PacketOutRemoveServer;
import eu.cloudnetservice.cloudnet.v2.wrapper.screen.AbstractScreenService;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.process.ServerDispatcher;
import eu.cloudnetservice.cloudnet.v2.wrapper.util.FileUtility;
import eu.cloudnetservice.cloudnet.v2.wrapper.util.MasterTemplateDeploy;
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

    private final ServerProcessMeta serverProcessMeta;
    private final ServerGroup serverGroup;
    private final Path dir;
    private final CloudLogger logger;
    private Process instance;
    private ServerInfo serverInfo;
    private long startupTimeStamp;

    public GameServer(ServerProcessMeta serverProcessMeta, ServerGroup serverGroup) {
        this.logger = CloudNetWrapper.getInstance().getCloudNetLogging();
        this.serverProcessMeta = serverProcessMeta;
        this.serverGroup = serverGroup;

        if (serverGroup.getGroupMode().equals(ServerGroupMode.STATIC) ||
            serverGroup.getGroupMode().equals(ServerGroupMode.STATIC_LOBBY)) {
            this.dir = Paths.get("local", "servers", serverGroup.getName(), this.serverProcessMeta.getServiceId().getServerId());
        } else {
            this.dir = Paths.get("temp", serverGroup.getName(), serverProcessMeta.getServiceId().toString());
        }
    }

    @Override
    public int hashCode() {
        int result = serverProcessMeta != null ? serverProcessMeta.hashCode() : 0;
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
        if (!Objects.equals(serverProcessMeta, that.serverProcessMeta)) {
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
            "serverProcessMeta=" + serverProcessMeta +
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

        for (ServerInstallablePlugin plugin : serverProcessMeta.getPlugins()) {
            downloadInstallablePlugin(plugin);
        }

        for (ServerInstallablePlugin url : serverProcessMeta.getTemplate().getInstallablePlugins()) {
            downloadInstallablePlugin(url);
        }

        if (serverGroup.getTemplates().size() == 0 && serverProcessMeta.getTemplateUrl() == null) {
            return false;
        }

        if (serverGroup.getGroupMode().equals(ServerGroupMode.STATIC) || serverGroup.getGroupMode().equals(ServerGroupMode.STATIC_LOBBY)) {
            if (!Files.exists(dir) && !templateDownloader()) {
                return false;
            }
        } else if (!templateDownloader()) {
            return false;
        }

        for (ServerInstallablePlugin plugin : serverProcessMeta.getPlugins()) {
            FileUtility.copyFileToDirectory(Paths.get("local", "cache", "web_plugins", plugin.getName() + ".jar"),
                                            this.dir.resolve("plugins"));
        }

        for (ServerInstallablePlugin plugin : serverProcessMeta.getTemplate().getInstallablePlugins()) {
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
                                                                                               this.serverProcessMeta));
        logger.info(String.format("Server %s started in [%d] milliseconds",
                                  this,
                                  (System.currentTimeMillis() - startupTime)));
        this.startupTimeStamp = System.currentTimeMillis();

        startProcess();

        CloudNetWrapper.getInstance().getServers().put(this.serverProcessMeta.getServiceId().getServerId(), this);
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
                                      CloudNetWrapper.getInstance().getWrapperConfig().getCloudNetHost(),
                                      CloudNetWrapper.getInstance().getWrapperConfig().getWebPort())).openConnection();
                    urlConnection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);

                    SimpledUser simpledUser = CloudNetWrapper.getInstance().getSimpledUser();
                    urlConnection.setRequestProperty("-Xcloudnet-user", simpledUser.getUserName());
                    urlConnection.setRequestProperty("-Xcloudnet-token", simpledUser.getApiToken());
                    urlConnection.setRequestProperty("-Xmessage", "plugin");
                    urlConnection.setRequestProperty("-Xvalue", plugin.getName());

                    urlConnection.connect();
                    CloudNetWrapper.getInstance().getCloudNetLogging().info(String.format("Downloading %s.jar", plugin.getName()));
                    Files.copy(urlConnection.getInputStream(), path);
                    CloudNetWrapper.getInstance().getCloudNetLogging().info(String.format("Download of %s.jar completed successfully!",
                                                                                          plugin.getName()));
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
            } else {
                final Path templatePath = Paths.get("local", "templates", serverGroup.getName(), template.getName());
                if (Files.exists(templatePath)) {
                    FileUtility.copyFilesInDirectory(templatePath, this.dir);
                }
            }
        }

        final Path pluginsDir = this.dir.resolve("plugins");
        if (serverProcessMeta.getTemplateUrl() != null) {
            if (!Files.exists(pluginsDir)) {
                Files.createDirectory(pluginsDir);
            }

            TemplateLoader templateLoader = new TemplateLoader(serverProcessMeta.getTemplateUrl(), this.dir.resolve("template.zip"));
            logger.info(String.format("Downloading template for %s from %s",
                                      this.serverProcessMeta.getServiceId().getServerId(),
                                      serverProcessMeta.getTemplateUrl()));
            templateLoader.load();
            templateLoader.unZip(this.dir);
        } else {

            if (!Files.exists(pluginsDir)) {
                Files.createDirectory(pluginsDir);
            }

            if (serverGroup.getTemplates().size() == 0) {
                return false;
            }

            Template template = this.serverProcessMeta.getTemplate();
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
            final Path apiPath = pluginPath.resolve("CloudNetAPI.jar");
            Files.deleteIfExists(apiPath);
            FileUtility.insertData("files/CloudNetAPI.jar", apiPath);
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
            FileUtility.insertData("files/server.properties", serverPropertiesPath);
            try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(serverPropertiesPath))) {
                properties.load(inputStreamReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (this.serverGroup.getGroupMode() == ServerGroupMode.STATIC ||
                this.serverGroup.getGroupMode() == ServerGroupMode.STATIC_LOBBY) {
                logger.warning(String.format("Filled empty server.properties (or missing \"max-players\" entry) of server [%s] at %s",
                                             this.serverProcessMeta.getServiceId(),
                                             serverPropertiesPath.toAbsolutePath()));
            } else {
                logger.warning(String.format(
                    "Filled empty server.properties (or missing \"max-players\" entry) of server [%s], please fix this error in the server.properties (check the template [%s@%s] and the global directory [%s])",
                    this.serverProcessMeta.getServiceId(),
                    this.serverProcessMeta
                        .getTemplate()
                        .getName(),
                    this.serverProcessMeta
                        .getTemplate()
                        .getBackend(),
                    new File("local/global").getAbsolutePath()));
            }
        }

        Enumeration<Object> enumeration = this.serverProcessMeta.getProperties().keys();
        while (enumeration.hasMoreElements()) {
            String x = enumeration.nextElement().toString();
            properties.setProperty(x, this.serverProcessMeta.getProperties().getProperty(x));
        }

        properties.setProperty("server-ip", CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP().getHostAddress());
        properties.setProperty("server-port", serverProcessMeta.getPort() + NetworkUtils.EMPTY_STRING);

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
        return new ServerInfo(serverProcessMeta.getServiceId(),
                              CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP(),
                              this.serverProcessMeta.getPort(),
                              false,
                              new ArrayList<>(),
                              serverProcessMeta.getMemory(),
                              motd,
                              maxPlayers,
                              ServerState.OFFLINE,
                              this.serverProcessMeta.getServerConfig(),
                              serverProcessMeta.getTemplate());
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
            section.set("port", serverProcessMeta.getPort());

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
        return new ServerInfo(serverProcessMeta.getServiceId(),
                              CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP(),
                              this.serverProcessMeta.getPort(),
                              false,
                              new ArrayList<>(),
                              serverProcessMeta.getMemory(),
                              motd,
                              maxPlayers,
                              ServerState.OFFLINE,
                              this.serverProcessMeta.getServerConfig(),
                              serverProcessMeta.getTemplate());
    }

    private void generateCloudNetConfigurations() {
        final Path cloudPath = this.dir.resolve("CLOUD");
        if (!(Files.exists(cloudPath) && Files.isDirectory(cloudPath))) {
            try {
                Files.deleteIfExists(cloudPath);
                Files.createDirectory(cloudPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new Document().append("serviceId", serverProcessMeta.getServiceId())
                      .append("serverProcess", serverProcessMeta)
                      .append("serverInfo", serverInfo)
                      .append("memory", serverProcessMeta.getMemory())
                      .saveAsConfig(cloudPath.resolve("config.json"));

        new Document("connection",
                     new ConnectableAddress(CloudNetWrapper.getInstance().getWrapperConfig().getCloudNetHost(),
                                            CloudNetWrapper.getInstance().getWrapperConfig().getCloudNetPort()))
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
        for (String command : serverProcessMeta.getJavaProcessParameters()) {
            commandBuilder.append(command).append(NetworkUtils.SPACE_STRING);
        }
        commandBuilder.append("-Dfile.encoding=UTF-8 -Dcom.mojang.eula.agree=true -Djline.terminal=jline.UnsupportedTerminal -Xmx")
                      .append(serverProcessMeta.getMemory())
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

        for (String command : serverProcessMeta.getServerProcessParameters()) {
            commandBuilder.append(command).append(NetworkUtils.SPACE_STRING);
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
            logger.info(String.format("Downloading template for %s %s",
                                      this.serverProcessMeta.getServiceId().getGroup(),
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
                              CloudNetWrapper.getInstance().getWrapperConfig().getCloudNetHost(),
                              CloudNetWrapper.getInstance().getWrapperConfig().getWebPort()),
                groupTemplates.resolve("template.zip"),
                CloudNetWrapper.getInstance().getSimpledUser(),
                template,
                serverGroup.getName());
            logger.info(String.format("Downloading template for %s %s",
                                      this.serverProcessMeta.getServiceId().getGroup(),
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
                final Path directory = Paths.get("local", "records", serverProcessMeta.getServiceId().toString());

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
        logger.info(String.format("Server %s was stopped", this));
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
        return serverProcessMeta.getServiceId();
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
                                         new ConnectableAddress(CloudNetWrapper.getInstance().getWrapperConfig().getCloudNetHost(),
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
            logger.info(String.format("Copying template from %s to local directory...", this.serverProcessMeta.getServiceId()));
            try {
                final Path templatePath = Paths.get("local", "templates", serverGroup.getName(), template.getName());
                FileUtility.copyFilesInDirectory(this.dir, templatePath);

                FileUtility.deleteDirectory(templatePath.resolve("CLOUD"));

                Files.deleteIfExists(templatePath.resolve(Paths.get("plugins", "CloudNetAPI.jar")));
            } catch (IOException exception) {
                logger.log(Level.SEVERE, "Error copying the template!", exception);
            }
            logger.log(Level.INFO,
                       String.format("Template %s was copied!", template.getName()));
        }
    }

    public ServerProcessMeta getServerProcessMeta() {
        return serverProcessMeta;
    }

    /**
     * Restart the game server
     */
    public void restart() {

        kill();
        logger.log(Level.INFO, String.format("Server %s was killed and restart...", this));
        try {
            startProcess();
            startupTimeStamp = System.currentTimeMillis();
            logger.log(Level.INFO, String.format("Server %s restarted now!", this));
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
                                                           serverProcessMeta.getTemplate().getName(),
                                                           name));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
