/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.server;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.MasterTemplateLoader;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutAddCloudServer;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutRemoveCloudServer;
import de.dytanic.cloudnetwrapper.screen.AbstractScreenService;
import de.dytanic.cloudnetwrapper.server.process.ServerDispatcher;
import de.dytanic.cloudnetwrapper.util.FileUtility;
import de.dytanic.cloudnetwrapper.util.MasterTemplateDeploy;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Tareko on 17.10.2017.
 */
public class CloudGameServer extends AbstractScreenService implements ServerDispatcher {

    private CloudServerMeta cloudServerMeta;
    private Path dir;
    private Path path;
    private ServerInfo serverInfo;
    private Process instance;

    public CloudGameServer(CloudServerMeta cloudServerMeta) {
        this.cloudServerMeta = cloudServerMeta;
        this.path = Paths.get(CloudNetWrapper.getInstance()
                                             .getWrapperConfig().getDevServicePath(),
                              cloudServerMeta.getServiceId().getServerId());
        this.dir = path;
    }

    @Override
    public int hashCode() {
        int result = cloudServerMeta != null ? cloudServerMeta.hashCode() : 0;
        result = 31 * result + (dir != null ? dir.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (serverInfo != null ? serverInfo.hashCode() : 0);
        result = 31 * result + (instance != null ? instance.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CloudGameServer)) {
            return false;
        }
        final CloudGameServer that = (CloudGameServer) o;
        return Objects.equals(cloudServerMeta, that.cloudServerMeta) && Objects.equals(dir, that.dir) && Objects.equals(path,
                                                                                                                        that.path) && Objects
            .equals(serverInfo, that.serverInfo) && Objects.equals(instance, that.instance);
    }

    @Override
    public String toString() {
        return '[' + cloudServerMeta.getServiceId()
                                    .getServerId() + "/port=" + cloudServerMeta.getPort() + "/memory=" + cloudServerMeta.getMemory() + ']';
    }

    public CloudServerMeta getCloudServerMeta() {
        return cloudServerMeta;
    }

    public Path getDir() {
        return dir;
    }

    public Path getPath() {
        return path;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public Queue<String> getCachedLogMessages() {
        return super.getCachedLogMessages();
    }

    @Override
    public boolean bootstrap() throws Exception {

        long startupTime = System.currentTimeMillis();

        for (ServerInstallablePlugin url : cloudServerMeta.getPlugins()) {
            switch (url.getPluginResourceType()) {
                case URL: {
                    if (!Files.exists(Paths.get("local/cache/web_plugins/" + url.getName() + ".jar"))) {
                        try {
                            URLConnection urlConnection = new java.net.URL(url.getUrl()).openConnection();
                            urlConnection.setRequestProperty("User-Agent",
                                                             "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                            Files.copy(urlConnection.getInputStream(), Paths.get("local/cache/web_plugins/" + url.getName() + ".jar"));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
                case MASTER: {
                    if (!Files.exists(Paths.get("local/cache/web_plugins/" + url.getName() + ".jar")) && CloudNetWrapper.getInstance()
                                                                                                                        .getSimpledUser() != null) {
                        try {
                            URLConnection urlConnection = new java.net.URL(new StringBuilder(CloudNetWrapper.getInstance()
                                                                                                            .getOptionSet()
                                                                                                            .has("ssl") ? "https://" : "http://")
                                                                               .append(CloudNetWrapper.getInstance()
                                                                                                      .getWrapperConfig()
                                                                                                      .getCloudnetHost())
                                                                               .append(':')
                                                                               .append(CloudNetWrapper.getInstance()
                                                                                                      .getWrapperConfig()
                                                                                                      .getWebPort())
                                                                               .append("/cloudnet/api/v1/download")
                                                                               .substring(0)).openConnection();
                            urlConnection.setRequestProperty("User-Agent",
                                                             "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

                            SimpledUser simpledUser = CloudNetWrapper.getInstance().getSimpledUser();
                            urlConnection.setRequestProperty("-Xcloudnet-user", simpledUser.getUserName());
                            urlConnection.setRequestProperty("-Xcloudnet-token", simpledUser.getApiToken());
                            urlConnection.setRequestProperty("-Xmessage", "plugin");
                            urlConnection.setRequestProperty("-Xvalue", url.getName());

                            urlConnection.connect();
                            System.out.println("Downloading " + url.getName() + ".jar");
                            Files.copy(urlConnection.getInputStream(), Paths.get("local/cache/web_plugins/" + url.getName() + ".jar"));
                            System.out.println("Download was completed successfully!");
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

        Files.createDirectories(this.dir);

        //Template
        MasterTemplateLoader templateLoader = new MasterTemplateLoader(new StringBuilder(CloudNetWrapper.getInstance()
                                                                                                        .getOptionSet()
                                                                                                        .has("ssl") ? "https://" : "http://")
                                                                           .append(CloudNetWrapper.getInstance()
                                                                                                  .getWrapperConfig()
                                                                                                  .getCloudnetHost())
                                                                           .append(':')
                                                                           .append(CloudNetWrapper.getInstance()
                                                                                                  .getWrapperConfig()
                                                                                                  .getWebPort())
                                                                           .append("/cloudnet/api/v1/download")
                                                                           .toString(),
                                                                       dir + "/template.zip",
                                                                       CloudNetWrapper.getInstance().getSimpledUser(),
                                                                       null,
                                                                       null,
                                                                       cloudServerMeta.getTemplateName());
        System.out.println("Downloading cloud server for " + this.cloudServerMeta.getServiceId());
        templateLoader.load();
        templateLoader.unZip(dir.toString());

        FileUtility.copyFilesInDirectory(dir.toFile(), path.toFile());

        if (cloudServerMeta.getServerGroupType().equals(ServerGroupType.CAULDRON)) {
            try {
                System.out.println("Downloading cauldron.zip...");
                File file = new File(path + "/cauldron.zip");
                URLConnection connection = new URL("https://yivesmirror.com/files/cauldron/cauldron-1.7.10-2.1403.1.54.zip").openConnection();
                connection.setUseCaches(false);
                connection.setRequestProperty("User-Agent",
                                              "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                connection.connect();
                Files.copy(connection.getInputStream(), Paths.get(path + "/cauldron.zip"));
                ((HttpURLConnection) connection).disconnect();
                System.out.println("Download was completed successfully!");

                ZipFile zip = new ZipFile(file);
                Enumeration<? extends ZipEntry> entryEnumeration = zip.entries();
                while (entryEnumeration.hasMoreElements()) {
                    ZipEntry entry = entryEnumeration.nextElement();

                    if (!entry.isDirectory()) {
                        extractEntry(zip, entry, path);
                    }
                }

                zip.close();
                file.delete();

                new File(path + "/cauldron-1.7.10-2.1403.1.54-server.jar").renameTo(new File(path + "/cauldron.jar"));

                try (FileWriter fileWriter = new FileWriter(path + "/eula.txt")) {
                    fileWriter.write("eula=true");
                    fileWriter.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (cloudServerMeta.getServerGroupType().equals(ServerGroupType.GLOWSTONE)) {
            Path path = Paths.get(this.path + "/glowstone.jar");
            downloadGlowstone(path);
        }

        //Init
        for (ServerInstallablePlugin plugin : cloudServerMeta.getPlugins()) {
            FileUtility.copyFileToDirectory(new File("local/cache/web_plugins/" + plugin.getName() + ".jar"), new File(path + "/plugins"));
        }

        for (ServerInstallablePlugin plugin : cloudServerMeta.getPlugins()) {
            FileUtility.copyFileToDirectory(new File("local/cache/web_plugins/" + plugin.getName() + ".jar"), new File(path + "/plugins"));
        }

        if (cloudServerMeta.getServerGroupType().equals(ServerGroupType.BUKKIT)) {
            if (!Files.exists(Paths.get(path + "/spigot.jar"))) {
                FileUtility.copyFileToDirectory(new File("local/spigot.jar"), path.toFile());
            }
        }

        if (cloudServerMeta.getServerGroupType().equals(ServerGroupType.GLOWSTONE)) {
            if (!Files.exists(Paths.get(path + "/config"))) {
                Files.createDirectories(Paths.get(path + "/config"));
            }
            if (!Files.exists(Paths.get(path + "/config/glowstone.yml"))) {
                FileUtility.insertData("files/glowstone.yml", path + "/config/glowstone.yml");
            }
        }

        if (!Files.exists(Paths.get(path + "/server.properties"))) {
            FileUtility.insertData("files/server.properties", path + "/server.properties");
        }

        if (!Files.exists(Paths.get(path + "/bukkit.yml"))) {
            FileUtility.insertData("files/bukkit.yml", path + "/bukkit.yml");
        }

        if (!Files.exists(Paths.get(path + "/spigot.yml"))) {
            FileUtility.insertData("files/spigot.yml", path + "/spigot.yml");
        }

        if (!Files.exists(Paths.get(path + "/plugins"))) {
            Files.createDirectory(Paths.get(path + "/plugins"));
        }

        if (!Files.exists(Paths.get(path + "/CLOUD"))) {
            Files.createDirectory(Paths.get(path + "/CLOUD"));
        }

        Files.deleteIfExists(Paths.get(path + "/plugins/CloudNetAPI.jar"));
        FileUtility.insertData("files/CloudNetAPI.jar", path + "/plugins/CloudNetAPI.jar");

        try {
            FileUtility.copyFilesInDirectory(new File("local/global_cloudserver"), path.toFile());
        } catch (Exception ex) {
        }

        if (CloudNetWrapper.getInstance().getWrapperConfig().isViaVersion()) {
            if (!Files.exists(Paths.get("local/viaversion.jar"))) {
                try {
                    System.out.println("Downloading ViaVersion...");
                    URLConnection url = new URL("https://ci.viaversion.com/job/ViaVersion/177/artifact/jar/target/ViaVersion-1.2.0.jar").openConnection();
                    url.setRequestProperty("User-Agent",
                                           "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                    url.connect();
                    Files.copy(url.getInputStream(), Paths.get("local/viaversion.jar"));
                    ((HttpURLConnection) url).disconnect();
                    System.out.println("Download was completed successfully!");
                } catch (Exception ex) {

                }
            }
            FileUtility.copyFileToDirectory(new File("local/viaversion.jar"), new File(path + "/plugins"));
        }

        /*===============================================================================*/

        String motd = "Default Motd";
        int maxPlayers = 0;

        if (!cloudServerMeta.getServerGroupType().equals(ServerGroupType.GLOWSTONE)) {
            Properties properties = new Properties();
            try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(Paths.get(path + "/server.properties")))) {
                properties.load(inputStreamReader);
            }

            if (properties.isEmpty() || !properties.contains("max-players")) {
                properties.setProperty("max-players", "100");
                FileUtility.insertData("files/server.properties", path + "/server.properties");
                try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(Paths.get(path + "/server.properties")))) {
                    properties.load(inputStreamReader);
                }
                System.err.println("Filled empty server.properties (or missing \"max-players\" entry) of server [" + this.cloudServerMeta.getServiceId() + "], please fix this error in the server.properties");
            }

            Enumeration enumeration = this.cloudServerMeta.getServerProperties().keys();
            while (enumeration.hasMoreElements()) {
                String x = enumeration.nextElement().toString();
                properties.setProperty(x, this.cloudServerMeta.getServerProperties().getProperty(x));
            }

            properties.setProperty("server-ip", CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP());
            properties.setProperty("server-port", cloudServerMeta.getPort() + NetworkUtils.EMPTY_STRING);
            properties.setProperty("online-mode", "false");
            //properties.setProperty("server-name", serverProcess.getMeta().getServiceId().getServerId());

            motd = properties.getProperty("motd");
            try {
                maxPlayers = Integer.parseInt(properties.getProperty("max-players"));
            } catch (NumberFormatException e) {
                maxPlayers = 100;
            }

            try (OutputStream outputStream = Files.newOutputStream(Paths.get(path + "/server.properties"))) {
                properties.store(outputStream, "CloudNet-Wrapper EDIT");
            }
        } else {
            try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(Paths.get(path + "/config/glowstone.yml")),
                                                                             StandardCharsets.UTF_8)) {
                Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);
                Configuration section = configuration.getSection("server");
                section.set("ip", CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP());
                section.set("port", cloudServerMeta.getPort());

                maxPlayers = section.getInt("max-players");
                motd = section.getString("motd");

                configuration.set("server", section);
                configuration.set("console.use-jline", false);
                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get(path + "/config/glowstone.yml")),
                                                                                    StandardCharsets.UTF_8)) {
                    ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
                }
            }
        }

        /*===============================================================================*/

        this.serverInfo = new ServerInfo(cloudServerMeta.getServiceId(),
                                         CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP(),
                                         this.cloudServerMeta.getPort(),
                                         false,
                                         new ArrayList<>(),
                                         cloudServerMeta.getMemory(),
                                         motd,
                                         0,
                                         maxPlayers,
                                         ServerState.OFFLINE,
                                         this.cloudServerMeta.getServerConfig(),
                                         cloudServerMeta.getTemplate());

        new Document().append("serviceId", cloudServerMeta.getServiceId())
                      .append("cloudProcess", cloudServerMeta)
                      .append("serverInfo",
                              serverInfo)
                      .append("ssl", CloudNetWrapper.getInstance().getOptionSet().has("ssl"))
                      .append("memory", cloudServerMeta.getMemory())
                      .saveAsConfig(Paths.get(path + "/CLOUD/config.json"));

        new Document().append("connection",
                              new ConnectableAddress(CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost(),
                                                     CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetPort())).saveAsConfig(Paths
                                                                                                                                           .get(
                                                                                                                                               path + "/CLOUD/connection.json"));

        Files.deleteIfExists(Paths.get(path + "/plugins/CloudNetAPI.jar"));
        FileUtility.insertData("files/CloudNetAPI.jar", path + "/plugins/CloudNetAPI.jar");

        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("java ");
        for (String command : cloudServerMeta.getProcessParameters()) {
            commandBuilder.append(command).append(NetworkUtils.SPACE_STRING);
        }

        commandBuilder.append(
            "-Dfile.encoding=UTF-8 -Dcom.mojang.eula.agree=true -Djline.terminal=jline.UnsupportedTerminal -Xmx"
                + cloudServerMeta.getMemory() + "M -jar ");

        switch (cloudServerMeta.getServerGroupType()) {
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

        CloudNetWrapper.getInstance().getCloudServers().put(this.cloudServerMeta.getServiceId().getServerId(), this);
        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutAddCloudServer(this.serverInfo, cloudServerMeta));
        System.out.println("CloudServer " + this + " [" + (cloudServerMeta.isPriorityStop() ? "priority stop: true" : "priority stop: false") + "] start [" + (System
            .currentTimeMillis() - startupTime) + " milliseconds]");
        this.instance = Runtime.getRuntime().exec(commandBuilder.toString().split(NetworkUtils.SPACE_STRING), null, path.toFile());

        return true;
    }

    @Override
    public boolean shutdown() {
        if (instance == null) {
            FileUtility.deleteDirectory(dir.toFile());
            return true;
        }

        if (instance.isAlive()) {
            executeCommand("stop");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        instance.destroyForcibly();

        try {
            Files.deleteIfExists(Paths.get(path + "/plugins/CloudNetAPI.jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (CloudNetWrapper.getInstance().isCanDeployed()) {
            MasterTemplateDeploy masterTemplateDeploy = new MasterTemplateDeploy(path.toString(),
                                                                                 new ConnectableAddress(CloudNetWrapper.getInstance()
                                                                                                                       .getWrapperConfig()
                                                                                                                       .getCloudnetHost(),
                                                                                                        CloudNetWrapper.getInstance()
                                                                                                                       .getWrapperConfig()
                                                                                                                       .getWebPort()),
                                                                                 CloudNetWrapper.getInstance().getSimpledUser(),
                                                                                 CloudNetWrapper.getInstance().getOptionSet().has("ssl"),
                                                                                 cloudServerMeta.getTemplate(),
                                                                                 null,
                                                                                 cloudServerMeta.getTemplateName());
            try {
                masterTemplateDeploy.deploy();
            } catch (Exception e) {
            }
        }

        FileUtility.deleteDirectory(path.toFile());

        CloudNetWrapper.getInstance().getCloudServers().remove(getServiceId().getServerId());
        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveCloudServer(serverInfo));
        System.out.println("Server " + this + " was stopped");
        return true;
    }

    public static void extractEntry(ZipFile zipFile, ZipEntry entry, Path destDir) throws IOException {
        Path outputPath = destDir.resolve(entry.getName());

        if (!outputPath.normalize().startsWith(destDir)) {
            return;
        }

        if (entry.isDirectory()) {
            Files.createDirectories(outputPath);
        } else {
            Files.createDirectory(outputPath.getParent());
            Files.copy(zipFile.getInputStream(entry), outputPath);
        }
    }

    @Override
    public ServiceId getServiceId() {
        return cloudServerMeta.getServiceId();
    }

    @Override
    public Process getInstance() {
        return instance;
    }

    public static void downloadGlowstone(final Path path) {
        if (!Files.exists(path)) {
            try {
                URLConnection connection = new URL("https://yivesmirror.com/grab/glowstone/Glowstone-latest.jar").openConnection();
                connection.setUseCaches(false);
                connection.setRequestProperty("User-Agent",
                                              "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                connection.connect();
                System.out.println("Downloading glowstone.jar...");
                Files.copy(connection.getInputStream(), path);
                System.out.println("Download was completed successfully");
                ((HttpURLConnection) connection).disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
