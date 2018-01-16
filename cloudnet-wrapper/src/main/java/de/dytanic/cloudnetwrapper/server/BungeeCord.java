/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.server;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyGroupMode;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.template.MasterTemplateLoader;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateLoader;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutAddProxy;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutRemoveProxy;
import de.dytanic.cloudnetwrapper.server.process.ServerDispatcher;
import de.dytanic.cloudnetwrapper.util.FileCopy;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

@EqualsAndHashCode
@Getter
public class BungeeCord implements ServerDispatcher {

    private ProxyProcessMeta proxyProcessMeta;

    private ProxyGroup proxyGroup;

    private Process instance;

    private Path dir;

    private String path;

    private ProxyInfo proxyInfo;

    public BungeeCord(ProxyProcessMeta proxyProcessMeta, ProxyGroup proxyGroup)
    {
        this.proxyProcessMeta = proxyProcessMeta;
        this.proxyGroup = proxyGroup;

        this.path = (proxyGroup.getProxyGroupMode().equals(ProxyGroupMode.STATIC) ? "local/servers/" : "temp/") + proxyGroup.getName() + "/" + (proxyGroup.getProxyGroupMode().equals(ProxyGroupMode.STATIC) ? proxyProcessMeta.getServiceId().getServerId() : proxyProcessMeta.getServiceId());
        this.dir = Paths.get(path);
    }

    @Override
    public boolean bootstrap() throws Exception
    {

        long startupTime = System.currentTimeMillis();
        if (proxyGroup.getTemplate().getBackend().equals(TemplateResource.URL))
        {
        }

        for (ServerInstallablePlugin url : proxyProcessMeta.getDownloadablePlugins())
        {
            if (!Files.exists(Paths.get("local/cache/web_plugins/" + url.getName() + ".jar")))
            {
                try
                {
                    URLConnection urlConnection = new java.net.URL(url.getUrl()).openConnection();
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                    Files.copy(urlConnection.getInputStream(), Paths.get("local/cache/web_plugins/" + url.getName() + ".jar"));
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        for (ServerInstallablePlugin url : proxyGroup.getTemplate().getInstallablePlugins())
        {

            switch (url.getPluginResourceType())
            {
                case URL:
                {
                    if (!Files.exists(Paths.get("local/cache/web_plugins/" + url.getName() + ".jar")))
                    {
                        try
                        {
                            URLConnection urlConnection = new java.net.URL(url.getUrl()).openConnection();
                            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                            Files.copy(urlConnection.getInputStream(), Paths.get("local/cache/web_plugins/" + url.getName() + ".jar"));
                        } catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
                case MASTER:
                {
                    if (!Files.exists(Paths.get("local/cache/web_plugins/" + url.getName() + ".jar")) && CloudNetWrapper.getInstance().getSimpledUser() != null)
                    {
                        try
                        {
                            URLConnection urlConnection = new java.net.URL(new StringBuilder(CloudNetWrapper.getInstance().getOptionSet().has("ssl") ? "https://" : "http://")
                                    .append(CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost())
                                    .append(":")
                                    .append(CloudNetWrapper.getInstance().getWrapperConfig().getWebPort())
                                    .append("/cloudnet/api/v1/download").substring(0)).openConnection();
                            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

                            SimpledUser simpledUser = CloudNetWrapper.getInstance().getSimpledUser();
                            urlConnection.setRequestProperty("-Xcloudnet-user", simpledUser.getUserName());
                            urlConnection.setRequestProperty("-Xcloudnet-token", simpledUser.getApiToken());
                            urlConnection.setRequestProperty("-Xmessage", "plugin");
                            urlConnection.setRequestProperty("-Xvalue", url.getName());

                            urlConnection.connect();
                            Files.copy(urlConnection.getInputStream(), Paths.get("local/cache/web_plugins/" + url.getName() + ".jar"));
                            ((HttpURLConnection)urlConnection).disconnect();
                        } catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
                default:
                    break;
            }
        }

        if (proxyGroup.getProxyGroupMode().equals(ProxyGroupMode.STATIC))
        {
            if (!Files.exists(dir))
            {
                Files.createDirectories(dir);
                if (proxyProcessMeta.getUrl() != null)
                {
                    Files.createDirectory(Paths.get(path + "/plugins"));
                    for (ServerInstallablePlugin plugin : proxyProcessMeta.getDownloadablePlugins())
                    {
                        FileCopy.copyFileToDirectory(new File("local/cache/web_plugins/" + plugin.getName() + ".jar"), new File(path + "/plugins"));
                    }

                    TemplateLoader templateLoader = new TemplateLoader(proxyProcessMeta.getUrl(), path + "/template.zip");
                    System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getServerId());
                    templateLoader.load();
                    templateLoader.unZip(path);
                } else
                {

                    Files.createDirectory(Paths.get(path + "/plugins"));
                    for (ServerInstallablePlugin plugin : proxyProcessMeta.getDownloadablePlugins())
                    {
                        FileCopy.copyFileToDirectory(new File("local/cache/web_plugins/" + plugin.getName() + ".jar"), new File(path + "/plugins"));
                    }

                    for (ServerInstallablePlugin plugin : proxyGroup.getTemplate().getInstallablePlugins())
                    {
                        FileCopy.copyFileToDirectory(new File("local/cache/web_plugins/" + plugin.getName() + ".jar"), new File(path + "/plugins"));
                    }

                    Template template = proxyGroup.getTemplate();
                    if (template.getBackend().equals(TemplateResource.URL) && template.getUrl() != null)
                    {
                        String groupTemplates = "local/cache/web_templates/" + proxyGroup.getName();
                        TemplateLoader templateLoader = new TemplateLoader(template.getUrl(), groupTemplates + "/template.zip");
                        System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getGroup());
                        templateLoader.load();
                        templateLoader.unZip(groupTemplates);
                        FileCopy.copyFilesInDirectory(new File("local/cache/web_templates/" + proxyGroup.getName()), new File(path));
                    } else if (template.getBackend().equals(TemplateResource.MASTER) && CloudNetWrapper.getInstance().getSimpledUser() != null)
                    {
                        String groupTemplates = "local/cache/web_templates/" + proxyGroup.getName() + "/" + template.getName();
                        if (!Files.exists(Paths.get(groupTemplates)))
                        {
                            Files.createDirectories(Paths.get(groupTemplates));
                            MasterTemplateLoader templateLoader = new MasterTemplateLoader(new StringBuilder(
                                    CloudNetWrapper.getInstance().getOptionSet().has("ssl") ? "https://" : "http://"
                            )
                                    .append(CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost())
                                    .append(":")
                                    .append(CloudNetWrapper.getInstance().getWrapperConfig().getWebPort())
                                    .append("/cloudnet/api/v1/download").substring(0)
                                    , groupTemplates + "/template.zip", CloudNetWrapper.getInstance().getSimpledUser(), template, proxyGroup.getName(), null);
                            System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getGroup());
                            templateLoader.load();
                            templateLoader.unZip(groupTemplates);
                        }
                        FileCopy.copyFilesInDirectory(new File(groupTemplates), new File(path));
                    } else if (Files.exists(Paths.get("local/templates/" + proxyGroup.getName())))
                    {

                        FileCopy.copyFilesInDirectory(new File("local/templates/" + proxyGroup.getName()), new File(path));
                    } else
                    {
                        return false;
                    }
                }
            }
        } else
        {
            try
            {
                FileUtils.deleteDirectory(new File(path));
            } catch (Exception ex)
            {
            }
            Files.createDirectories(dir);

            if (proxyProcessMeta.getUrl() != null)
            {

                Files.createDirectory(Paths.get(path + "/plugins"));
                for (ServerInstallablePlugin plugin : proxyProcessMeta.getDownloadablePlugins())
                {
                    FileCopy.copyFileToDirectory(new File("local/cache/web_plugins/" + plugin.getName() + ".jar"), new File(path + "/plugins"));
                }

                for (ServerInstallablePlugin plugin : proxyGroup.getTemplate().getInstallablePlugins())
                {
                    FileCopy.copyFileToDirectory(new File("local/cache/web_plugins/" + plugin.getName() + ".jar"), new File(path + "/plugins"));
                }

                TemplateLoader templateLoader = new TemplateLoader(proxyProcessMeta.getUrl(), "local/templates/" + proxyGroup.getName() + "/template.zip");
                System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getServerId());
                templateLoader.load();
                templateLoader.unZip(path);
            } else
            {

                Files.createDirectory(Paths.get(path + "/plugins"));
                for (ServerInstallablePlugin plugin : proxyProcessMeta.getDownloadablePlugins())
                {
                    FileCopy.copyFileToDirectory(new File("local/cache/web_plugins/" + plugin.getName() + ".jar"), new File(path + "/plugins"));
                }

                Template template = proxyGroup.getTemplate();
                if (template.getBackend().equals(TemplateResource.URL) && template.getUrl() != null)
                {
                    String groupTemplates = "local/cache/web_templates/" + proxyGroup.getName();
                    TemplateLoader templateLoader = new TemplateLoader(template.getUrl(), groupTemplates + "/template.zip");
                    System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getGroup());
                    templateLoader.load();
                    templateLoader.unZip(groupTemplates);
                    FileCopy.copyFilesInDirectory(new File("local/cache/web_templates/" + proxyGroup.getName()), new File(path));
                } else if (template.getBackend().equals(TemplateResource.MASTER) && CloudNetWrapper.getInstance().getSimpledUser() != null)
                {
                    String groupTemplates = "local/cache/web_templates/" + proxyGroup.getName() + "/" + template.getName();
                    if (!Files.exists(Paths.get(groupTemplates)))
                    {
                        Files.createDirectories(Paths.get(groupTemplates));
                        MasterTemplateLoader templateLoader = new MasterTemplateLoader(new StringBuilder(
                                CloudNetWrapper.getInstance().getOptionSet().has("ssl") ? "https://" : "http://"
                        )
                                .append(CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost())
                                .append(":")
                                .append(CloudNetWrapper.getInstance().getWrapperConfig().getWebPort())
                                .append("/cloudnet/api/v1/download").substring(0)
                                , groupTemplates + "/template.zip", CloudNetWrapper.getInstance().getSimpledUser(), template, proxyGroup.getName(), null);
                        System.out.println("Downloading template for " + this.proxyProcessMeta.getServiceId().getGroup());
                        templateLoader.load();
                        templateLoader.unZip(groupTemplates);
                    }
                    FileCopy.copyFilesInDirectory(new File(groupTemplates), new File(path));
                } else if (Files.exists(Paths.get("local/templates/" + proxyGroup.getName())))
                {

                    FileCopy.copyFilesInDirectory(new File("local/templates/" + proxyGroup.getName()), new File(path));
                } else
                {
                    return false;
                }
            }
        }

        if (!Files.exists(Paths.get(path + "/config.yml")))
        {
            FileCopy.insertData("files/config.yml", path + "/config.yml");
        }

        if (!Files.exists(Paths.get(path + "/BungeeCord.jar")))
        {
            MultiValue<String, String> version = ProxyVersion.url(proxyGroup.getProxyVersion());
            Path path = Paths.get("local/proxy_versions/" + version.getSecond());
            if (!Files.exists(path))
            {
                try
                {
                    URLConnection urlConnection = new URL(version.getFirst()).openConnection();
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                    urlConnection.connect();
                    System.out.println("Downloading " + version.getSecond() + "...");
                    Files.copy(urlConnection.getInputStream(), path);
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            FileCopy.copyFileToDirectory(new File("local/proxy_versions/" + version.getSecond()), new File(this.path));
            new File(this.path + "/" + version.getSecond()).renameTo(new File(this.path + "/BungeeCord.jar"));
        }

        if (!Files.exists(Paths.get(path + "/server-icon.png")) && Files.exists(Paths.get("local/server-icon.png")))
        {
            FileCopy.copyFileToDirectory(new File("local/server-icon.png"), new File(path));
        }

        Files.deleteIfExists(Paths.get(path + "/plugins/CloudNetAPI.jar"));
        FileCopy.insertData("files/CloudNetAPI.jar", path + "/plugins/CloudNetAPI.jar");

        FileCopy.rewriteFileUtils(new File(path + "/config.yml"), CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP() + ":" + this.proxyProcessMeta.getPort());

        this.proxyInfo = new ProxyInfo(proxyProcessMeta.getServiceId(),
                CloudNetWrapper.getInstance().getWrapperConfig().getInternalIP(),
                proxyProcessMeta.getPort(), false, new LinkedList<>(), proxyProcessMeta.getMemory(), 0);

        if (!Files.exists(Paths.get(path + "/CLOUD")))
        {
            Files.createDirectory(Paths.get(path + "/CLOUD"));
        }

        new Document()
                .append("serviceId", proxyProcessMeta.getServiceId())
                .append("proxyProcess", proxyProcessMeta)
                .append("proxyInfo", proxyInfo)
                .append("ssl", CloudNetWrapper.getInstance().getOptionSet().has("ssl"))
                .append("memory", proxyProcessMeta.getMemory()).saveAsConfig(Paths.get(path + "/CLOUD/config.json"));
        new Document()
                .append("connection", new ConnectableAddress(
                        CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetHost(),
                        CloudNetWrapper.getInstance().getWrapperConfig().getCloudnetPort())).saveAsConfig(Paths.get(path + "/CLOUD/connection.json"));

        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("java ");

        for (String command : proxyProcessMeta.getProcessParameters())
            commandBuilder.append(command).append(" ");

        for (String command : proxyGroup.getTemplate().getProcessPreParameters())
            commandBuilder.append(command).append(" ");

        //commandBuilder.append("-XX:+UseG1GC -XX:MaxGCPauseMillis=50 -Djline.terminal=jline.UnsupportedTerminal -XX:MaxPermSize=256M -DIReallyKnowWhatIAmDoingISwear=true -Xmx" + proxyProcessMeta.getMemory() + "M -jar BungeeCord.jar -o true -p");
        commandBuilder.append("-XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:MaxPermSize=256M -XX:-UseAdaptiveSizePolicy -Dio.netty.leakDetectionLevel=DISABLED -Dfile.encoding=UTF-8 -Dio.netty.maxDirectMemory=0 -Dio.netty.recycler.maxCapacity=0 -Dio.netty.recycler.maxCapacity.default=0 -Djline.terminal=jline.UnsupportedTerminal -DIReallyKnowWhatIAmDoingISwear=true -Xmx" + proxyProcessMeta.getMemory() + "M -jar BungeeCord.jar -o true -p");

        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutAddProxy(proxyInfo, proxyProcessMeta));
        System.out.println("Proxy " + toString() + " started in [" + (System.currentTimeMillis() - startupTime) + " milliseconds]");

        this.instance = Runtime.getRuntime().exec(commandBuilder.substring(0).split(" "), null, new File(path));
        CloudNetWrapper.getInstance().getProxys().put(this.proxyProcessMeta.getServiceId().getServerId(), this);
        return true;
    }

    @Override
    public boolean shutdown()
    {

        if (instance == null)
        {
            if (proxyGroup.getProxyGroupMode().equals(ProxyGroupMode.DYNAMIC))
            {
                try
                {
                    Files.delete(Paths.get(path));
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return true;
        }

        if (instance.isAlive())
        {
            executeCommand("end");
            NetworkUtils.sleepUninterruptedly(500);
        }

        instance.destroyForcibly();

        if (CloudNetWrapper.getInstance().getWrapperConfig().isSavingRecords())
        {
            try
            {
                FileCopy.copyFilesInDirectory(new File(path), new File("local/records/" + proxyProcessMeta.getServiceId().toString()));
                new Document("meta", proxyProcessMeta).saveAsConfig(Paths.get("local/records/metadata.json"));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if (proxyGroup.getProxyGroupMode().equals(ProxyGroupMode.DYNAMIC))
        {
            try
            {
                FileUtils.deleteDirectory(new File(path));
            } catch (IOException e)
            {
            }
        }

        CloudNetWrapper.getInstance().getProxys().remove(getServiceId().getServerId());
        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutRemoveProxy(proxyInfo));
        System.out.println("Proxy " + toString() + " was stopped");

        try
        {
            this.finalize();
        } catch (Throwable throwable)
        {
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "[" + proxyProcessMeta.getServiceId().getServerId() + "/port=" + proxyProcessMeta.getPort() + "/memory=" + proxyProcessMeta.getMemory() + "]";
    }

    @Override
    public ServiceId getServiceId()
    {
        return proxyProcessMeta.getServiceId();
    }
}