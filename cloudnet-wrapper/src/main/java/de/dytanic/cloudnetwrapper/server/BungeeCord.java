package de.dytanic.cloudnetwrapper.server;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyGroupMode;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.template.MasterTemplateLoader;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateLoader;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutAddProxy;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutRemoveProxy;
import de.dytanic.cloudnetwrapper.screen.AbstractScreenService;
import de.dytanic.cloudnetwrapper.server.process.ServerDispatcher;
import de.dytanic.cloudnetwrapper.util.FileUtility;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class BungeeCord extends AbstractScreenService implements ServerDispatcher {

    private final ProxyProcessMeta proxyProcessMeta;

    private final ProxyGroup proxyGroup;

    private Process instance;

    private Path dir;

    private ProxyInfo proxyInfo;

    public BungeeCord(ProxyProcessMeta proxyProcessMeta, ProxyGroup proxyGroup) {
        this.proxyProcessMeta = proxyProcessMeta;
        this.proxyGroup = proxyGroup;

        if (proxyGroup.getProxyGroupMode() == ProxyGroupMode.STATIC) {
            this.dir = Paths.get("local",
                                 "servers",
                                 proxyGroup.getName(),
                                 proxyProcessMeta.getServiceId().getServerId());
        } else if (proxyGroup.getProxyGroupMode() == ProxyGroupMode.DYNAMIC) {
            this.dir = Paths.get("temp",
                                 proxyGroup.getName(),
                                 proxyProcessMeta.getServiceId().getServerId() + '#' + proxyProcessMeta.getServiceId().getUniqueId());
        }
    }

    public ProxyGroup getProxyGroup() {
        return proxyGroup;
    }

    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    @Override
    public Queue<String> getCachedLogMessages() {
        return super.getCachedLogMessages();
    }

    public Path getDir() {
        return dir;
    }

    public ProxyProcessMeta getProxyProcessMeta() {
        return proxyProcessMeta;
    }

    @Override
    public boolean bootstrap() throws Exception {

        long startupTime = System.currentTimeMillis();
        //        if (proxyGroup.getTemplate().getBackend().equals(TemplateResource.URL)) {
        //        }

        for (ServerInstallablePlugin plugin : proxyProcessMeta.getPlugins()) {
            GameServer.downloadInstallablePlugin(plugin);
        }

        for (ServerInstallablePlugin plugin : proxyGroup.getTemplate().getInstallablePlugins()) {
            GameServer.downloadInstallablePlugin(plugin);
        }

        final Path pluginsPath = this.dir.resolve("plugins");
        final Path templatePath = Paths.get("local", "templates", proxyGroup.getName());
        if (proxyGroup.getProxyGroupMode().equals(ProxyGroupMode.STATIC)) {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
                if (proxyProcessMeta.getTemplateUrl() != null) {
                    Files.createDirectory(pluginsPath);
                    for (ServerInstallablePlugin plugin : proxyProcessMeta.getPlugins()) {
                        FileUtility.copyFileToDirectory(Paths.get("local", "cache", "web_plugins", plugin.getName() + ".jar"),
                                                        pluginsPath);
                    }

                    TemplateLoader templateLoader = new TemplateLoader(proxyProcessMeta.getTemplateUrl(), this.dir.resolve("template.zip"));
                    System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getServerId());
                    templateLoader.load();
                    templateLoader.unZip(this.dir);
                } else {

                    Files.createDirectory(pluginsPath);
                    for (ServerInstallablePlugin plugin : proxyProcessMeta.getPlugins()) {
                        FileUtility.copyFileToDirectory(Paths.get("local", "cache", "web_plugins", plugin.getName() + ".jar"),
                                                        pluginsPath);
                    }

                    for (ServerInstallablePlugin plugin : proxyGroup.getTemplate().getInstallablePlugins()) {
                        FileUtility.copyFileToDirectory(Paths.get("local", "cache", "web_plugins", plugin.getName() + ".jar"),
                                                        pluginsPath);
                    }

                    Template template = proxyGroup.getTemplate();
                    if (template.getBackend().equals(TemplateResource.URL) && template.getUrl() != null) {
                        Path groupTemplates = Paths.get("local", "cache", "web_templates", proxyGroup.getName());
                        TemplateLoader templateLoader = new TemplateLoader(template.getUrl(), groupTemplates.resolve("template.zip"));
                        System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getGroup());
                        templateLoader.load();
                        templateLoader.unZip(groupTemplates);
                        FileUtility.copyFilesInDirectory(groupTemplates, this.dir);
                    } else if (template.getBackend().equals(TemplateResource.MASTER) &&
                        CloudNetWrapper.getInstance().getSimpledUser() != null) {
                        Path groupTemplates = Paths.get("local", "cache", "web_templates", proxyGroup.getName(), template.getName());
                        if (!Files.exists(groupTemplates)) {
                            Files.createDirectories(groupTemplates);
                            MasterTemplateLoader templateLoader = new MasterTemplateLoader(
                                String.format("http://%s:%d/cloudnet/api/v1/download",
                                              CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost(),
                                              CloudNetWrapper.getInstance().getWrapperConfig().getWebPort()),
                                groupTemplates.resolve("template.zip"),
                                CloudNetWrapper.getInstance().getSimpledUser(),
                                template,
                                proxyGroup.getName()
                            );
                            System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getGroup());
                            templateLoader.load();
                            templateLoader.unZip(groupTemplates);
                        }
                        FileUtility.copyFilesInDirectory(groupTemplates, this.dir);
                    } else if (Files.exists(Paths.get("local/templates/" + proxyGroup.getName()))) {

                        FileUtility.copyFilesInDirectory(templatePath, this.dir);
                    } else {
                        return false;
                    }
                }
            }
        } else {
            FileUtility.deleteDirectory(this.dir);
            Files.createDirectories(this.dir);

            if (proxyProcessMeta.getTemplateUrl() != null) {

                Files.createDirectory(pluginsPath);
                for (ServerInstallablePlugin plugin : proxyProcessMeta.getPlugins()) {
                    FileUtility.copyFileToDirectory(Paths.get("local", "cache", "web_plugins", plugin.getName() + ".jar"),
                                                    pluginsPath);
                }

                for (ServerInstallablePlugin plugin : proxyGroup.getTemplate().getInstallablePlugins()) {
                    FileUtility.copyFileToDirectory(Paths.get("local", "cache", "web_plugins", plugin.getName() + ".jar"),
                                                    pluginsPath);
                }


                TemplateLoader templateLoader = new TemplateLoader(proxyProcessMeta.getTemplateUrl(),
                                                                   Paths.get("local", "templates", proxyGroup.getName(), "template.zip"));
                System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getServerId());
                templateLoader.load();
                templateLoader.unZip(this.dir);
            } else {

                Files.createDirectory(pluginsPath);
                for (ServerInstallablePlugin plugin : proxyProcessMeta.getPlugins()) {
                    FileUtility.copyFileToDirectory(Paths.get("local", "cache", "web_plugins", plugin.getName() + ".jar"), pluginsPath);
                }

                Template template = proxyGroup.getTemplate();
                if (template.getBackend().equals(TemplateResource.URL) && template.getUrl() != null) {
                    final Path groupTemplates = Paths.get("local", "cache", "web_templates", proxyGroup.getName());

                    TemplateLoader templateLoader = new TemplateLoader(template.getUrl(), groupTemplates.resolve("template.zip"));
                    System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getGroup());
                    templateLoader.load();
                    templateLoader.unZip(groupTemplates);
                    FileUtility.copyFilesInDirectory(groupTemplates, this.dir);
                } else if (template.getBackend().equals(TemplateResource.MASTER) &&
                    CloudNetWrapper.getInstance().getSimpledUser() != null) {
                    final Path groupTemplates = Paths.get("local", "cache", "web_templates", proxyGroup.getName(), template.getName());
                    if (!Files.exists(groupTemplates)) {
                        Files.createDirectories(groupTemplates);
                        MasterTemplateLoader templateLoader = new MasterTemplateLoader(
                            String.format("http://%s:%d/cloudnet/api/v1/download",
                                          CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost(),
                                          CloudNetWrapper.getInstance().getWrapperConfig().getWebPort()),
                            groupTemplates.resolve("template.zip"),
                            CloudNetWrapper.getInstance().getSimpledUser(),
                            template,
                            proxyGroup.getName()
                        );
                        System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getGroup());
                        templateLoader.load();
                        templateLoader.unZip(groupTemplates);
                    }
                    FileUtility.copyFilesInDirectory(groupTemplates, this.dir);
                } else if (Files.exists(templatePath)) {
                    FileUtility.copyFilesInDirectory(templatePath, this.dir);
                } else {
                    return false;
                }
            }
        }

        final Path configPath = this.dir.resolve("config.yml");
        if (!Files.exists(configPath)) {
            FileUtility.insertData("files/config.yml", configPath);
        }

        if (!Files.exists(this.dir.resolve("BungeeCord.jar"))) {
            MultiValue<String, String> version = ProxyVersion.url(proxyGroup.getProxyVersion());
            Path path = Paths.get("local", "proxy_versions", version.getSecond());
            if (!Files.exists(path)) {
                try {
                    URLConnection urlConnection = new URL(version.getFirst()).openConnection();
                    urlConnection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
                    urlConnection.connect();
                    System.out.println("Downloading " + version.getSecond() + "...");
                    Files.copy(urlConnection.getInputStream(), path);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            FileUtility.copyFileToDirectory(path, this.dir);
            Files.move(this.dir.resolve(version.getSecond()), this.dir.resolve("BungeeCord.jar"));
        }

        if (!Files.exists(this.dir.resolve("server-icon.png")) &&
            Files.exists(Paths.get("local", "server-icon.png"))) {
            FileUtility.copyFileToDirectory(this.dir.resolve("server-icon.png"), this.dir);
        }

        Files.deleteIfExists(pluginsPath.resolve("CloudNetAPI.jar"));
        FileUtility.insertData("files/CloudNetAPI.jar", pluginsPath.resolve("CloudNetAPI.jar"));

        FileUtility.rewriteFileUtils(this.dir.resolve("config.yml"),
                                     CloudNetWrapper.getInstance().getWrapperConfig().getProxyConfigHost() +
                                         ':' + this.proxyProcessMeta.getPort());

        this.proxyInfo = new ProxyInfo(proxyProcessMeta.getServiceId(),
                                       CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP(),
                                       proxyProcessMeta.getPort(),
                                       false,
                                       new HashMap<>(),
                                       proxyProcessMeta.getMemory(),
                                       0);

        final Path cloudPath = this.dir.resolve("CLOUD");
        if (!Files.exists(cloudPath)) {
            Files.createDirectory(cloudPath);
        }

        new Document().append("serviceId", proxyProcessMeta.getServiceId())
                      .append("proxyProcess", proxyProcessMeta)
                      .append("host", String.format("%s:%d",
                                                    CloudNetWrapper.getInstance().getWrapperConfig().getProxyConfigHost(),
                                                    this.proxyProcessMeta.getPort()))
                      .append("proxyInfo", proxyInfo)
                      .append("memory", proxyProcessMeta.getMemory())
                      .saveAsConfig(cloudPath.resolve("config.json"));
        new Document().append("connection",
                              new ConnectableAddress(CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost(),
                                                     CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetPort()))
                      .saveAsConfig(cloudPath.resolve("connection.json"));

        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("java ");

        for (String command : proxyProcessMeta.getJavaProcessParameters()) {
            commandBuilder.append(command).append(NetworkUtils.SPACE_STRING);
        }

        for (String command : proxyGroup.getTemplate().getProcessPreParameters()) {
            commandBuilder.append(command).append(NetworkUtils.SPACE_STRING);
        }

        commandBuilder.append("-Dfile.encoding=UTF-8 -Dcom.mojang.eula.agree=true -Djline.terminal=jline.UnsupportedTerminal -Xmx")
                      .append(proxyProcessMeta.getMemory())
                      .append("M -jar BungeeCord.jar");

        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutAddProxy(proxyInfo, proxyProcessMeta));
        System.out.println("Proxy " + this + " started in [" + (System.currentTimeMillis() - startupTime) + " milliseconds]");

        this.instance = Runtime.getRuntime().exec(commandBuilder.substring(0).split(NetworkUtils.SPACE_STRING), null, this.dir.toFile());
        CloudNetWrapper.getInstance().getProxies().put(this.proxyProcessMeta.getServiceId().getServerId(), this);
        return true;
    }

    @Override
    public boolean shutdown() {

        if (instance == null) {
            if (proxyGroup.getProxyGroupMode().equals(ProxyGroupMode.DYNAMIC)) {
                try {
                    Files.deleteIfExists(this.dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        if (instance.isAlive()) {
            executeCommand("end");
            try {
                if (!instance.waitFor(60, TimeUnit.SECONDS)) {
                    instance.destroyForcibly();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                instance.destroyForcibly();
            }

        }

        if (CloudNetWrapper.getInstance().getWrapperConfig().isSavingRecords()) {
            try {
                try (Stream<Path> list = Files.list(this.dir)) {
                    list.filter(path -> path.getFileName().toString().contains("proxy.log"))
                        .forEach(path -> {
                            try {
                                FileUtility.copyFileToDirectory(path, Paths.get("local",
                                                                                "records",
                                                                                proxyProcessMeta.getServiceId().toString()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (proxyGroup.getProxyGroupMode().equals(ProxyGroupMode.DYNAMIC)) {
            FileUtility.deleteDirectory(this.dir);
        }

        CloudNetWrapper.getInstance().getProxies().remove(getServiceId().getServerId());
        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveProxy(proxyInfo));
        System.out.println("Proxy " + this + " was stopped");

        return true;
    }

    @Override
    public ServiceId getServiceId() {
        return proxyProcessMeta.getServiceId();
    }

    @Override
    public Process getInstance() {
        return instance;
    }

    @Override
    public int hashCode() {
        int result = proxyProcessMeta != null ? proxyProcessMeta.hashCode() : 0;
        result = 31 * result + (proxyGroup != null ? proxyGroup.hashCode() : 0);
        result = 31 * result + (instance != null ? instance.hashCode() : 0);
        result = 31 * result + (dir != null ? dir.hashCode() : 0);
        result = 31 * result + (proxyInfo != null ? proxyInfo.hashCode() : 0);
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

        final BungeeCord that = (BungeeCord) o;

        if (!Objects.equals(proxyProcessMeta, that.proxyProcessMeta)) {
            return false;
        }
        if (!Objects.equals(proxyGroup, that.proxyGroup)) {
            return false;
        }
        if (!Objects.equals(instance, that.instance)) {
            return false;
        }
        if (!Objects.equals(dir, that.dir)) {
            return false;
        }
        return Objects.equals(proxyInfo, that.proxyInfo);
    }

    @Override
    public String toString() {
        return "BungeeCord{" +
            "proxyProcessMeta=" + proxyProcessMeta +
            ", proxyGroup=" + proxyGroup +
            ", instance=" + instance +
            ", dir=" + dir +
            ", proxyInfo=" + proxyInfo +
            "} " + super.toString();
    }
}
