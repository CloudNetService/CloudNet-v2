/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 04.10.2017.
 */
public final class CommandLog extends Command {

    private final Gson g = new GsonBuilder().setPrettyPrinting().create();

    private final String version = "1";

    public CommandLog() {
        super("log", "cloudnet.command.log");

        description = "Creates a web server log";

    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (CloudNet.getInstance().getServers().containsKey(args[0])) {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[0]);
                String rndm = NetworkUtils.randomString(10);
                CloudNet.getInstance().getServerLogManager().append(rndm, minecraftServer.getServerId());
                List<String> hasteServer = CloudNet.getInstance().getConfig().getHasteServer().stream().filter(this::checkUrl).collect(
                    Collectors.toList());
                sendMinecraftServerPaste(sender, rndm, hasteServer, minecraftServer);
                String x = new StringBuilder("http://").append(CloudNet.getInstance().getConfig().getWebServerConfig().getAddress()).append(
                    ':').append(CloudNet.getInstance().getConfig().getWebServerConfig().getPort()).append("/cloudnet/log?server=").append(
                    rndm).substring(0);
                sender.sendMessage("You can see the log at: " + x);

                sender.sendMessage("The log is dynamic and will be deleted in 10 minutes");
            } else if (CloudNet.getInstance().getProxys().containsKey(args[0])) {
                ProxyServer proxyServer = CloudNet.getInstance().getProxy(args[0]);
                String rndm = NetworkUtils.randomString(10);
                CloudNet.getInstance().getServerLogManager().append(rndm, proxyServer.getServerId());
                List<String> hasteServer = CloudNet.getInstance().getConfig().getHasteServer().stream().filter(this::checkUrl).collect(
                    Collectors.toList());
                sendProxyServerPaste(sender, rndm, hasteServer, proxyServer);
                String x = new StringBuilder("http://").append(CloudNet.getInstance().getConfig().getWebServerConfig().getAddress()).append(
                    ':').append(CloudNet.getInstance().getConfig().getWebServerConfig().getPort()).append("/cloudnet/log?server=").append(
                    rndm).substring(0);
                sender.sendMessage("You can see the log at: " + x);
            } else {
                sender.sendMessage("The server/proxy doesn't exist!");
            }
        } else {
            sender.sendMessage("log <server/proxy> | Creates a web server log");
        }
    }

    /**
     * Check url is page available
     *
     * @param url The url to check
     *
     * @return Return true if exist and connectible, else return false
     */
    private boolean checkUrl(String url) {
        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            con.connect();
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);
            return con.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            System.out.println("Log Command: Server \"" + url + "\" cannot been reached!");
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return false;
    }

    /**
     * Send the log to a random past site
     *
     * @param sender          The sender of the command
     * @param random          A random string
     * @param url             The url of the paste site
     * @param minecraftServer The minecraft server with the log inside
     */
    private void sendMinecraftServerPaste(CommandSender sender, String random, List<String> url, MinecraftServer minecraftServer) {
        CloudNet.getInstance().getScheduler().runTaskDelayAsync(() -> {
            StringBuilder paste = new StringBuilder();
            paste.append("LOG VERSION: " + version).append('\n');
            paste.append("-----BEGIN SYSTEM INFO-----").append('\n');
            paste.append(String.format("Time: %s", ZonedDateTime.now())).append('\n');
            paste.append(String.format("Server: %s", minecraftServer.getServerId())).append('\n');
            paste.append(String.format("Java Version: %s", System.getProperty("java.version"))).append('\n');
            paste.append(String.format("User: %s", System.getProperty("user.name"))).append('\n');
            paste.append(String.format("OS: %s %s (%s)",
                                       System.getProperty("os.name"),
                                       System.getProperty("os.arch"),
                                       System.getProperty("os.version"))).append('\n');
            paste.append(String.format("Memory Used: %d Mb",
                                       ((NetworkUtils.system().getTotalPhysicalMemorySize() - NetworkUtils.system()
                                                                                                          .getFreePhysicalMemorySize()) / 1024L) / 1024L))
                 .append('\n');
            paste.append(String.format("Memory Free: %d Mb", (NetworkUtils.system().getFreePhysicalMemorySize() / 1024L) / 1024L)).append(
                '\n');
            paste.append(String.format("Memory Max: %d Mb", (NetworkUtils.system().getTotalPhysicalMemorySize() / 1024L) / 1024L)).append(
                '\n');
            paste.append(String.format("CPU Cores: %d", NetworkUtils.system().getAvailableProcessors())).append('\n');
            paste.append("-----END SYSTEM INFO-----").append('\n');
            paste.append("-----BEGIN CLOUDNET INFO-----").append('\n');
            paste.append(String.format("CloudNet Version: %s # %s",
                                       NetworkUtils.class.getPackage().getSpecificationVersion(),
                                       NetworkUtils.class.getPackage().getImplementationVersion())).append('\n');
            paste.append(String.format("Backend: %s", minecraftServer.getServerInfo().getTemplate().getBackend().name())).append('\n');
            paste.append(String.format("GroupMode: %s", minecraftServer.getGroup().getGroupMode().name())).append('\n');
            paste.append(String.format("Server: %s", minecraftServer.getServerId())).append('\n');
            paste.append("-----END CLOUDNET INFO-----").append('\n');
            paste.append("-----BEGIN SERVER INFO-----").append('\n');
            paste.append(g.toJson(minecraftServer.getServerInfo(), TypeToken.get(ServerInfo.class).getType())).append('\n');
            paste.append("-----END SERVER INFO-----").append('\n');
            paste.append("-----BEGIN GROUP INFO-----").append('\n');
            paste.append(g.toJson(minecraftServer.getGroup(), TypeToken.get(ServerGroup.class).getType())).append('\n');
            paste.append("-----END GROUP INFO-----").append('\n');
            paste.append("-----BEGIN LOG INFO-----").append('\n');
            CloudNet.getInstance()
                    .getServerLogManager()
                    .getScreenInfos()
                    .getS(random)
                    .forEach(screenInfo -> paste.append(screenInfo.getLine()).append('\n'));
            paste.append("-----END LOG INFO-----").append('\n');
            for (String s : url) {
                if (postTo(sender, s, paste)) {
                    break;
                }
            }
        }, 3 * 50);
    }

    /**
     * Send the log to a random past site
     *
     * @param sender      The sender of the command
     * @param random      A random string
     * @param url         The url of the paste site
     * @param proxyServer The proxy server with the log inside
     */
    private void sendProxyServerPaste(CommandSender sender, String random, List<String> url, ProxyServer proxyServer) {
        CloudNet.getInstance().getScheduler().runTaskDelayAsync(() -> {
            StringBuilder paste = new StringBuilder();
            paste.append("LOG VERSION: " + version).append('\n');
            paste.append("-----BEGIN SYSTEM INFO-----").append('\n');
            paste.append(String.format("Time: %s", ZonedDateTime.now())).append('\n');
            paste.append(String.format("Server: %s", proxyServer.getServerId())).append('\n');
            paste.append(String.format("Java Version: %s", System.getProperty("java.version"))).append('\n');
            paste.append(String.format("User: %s", System.getProperty("user.name"))).append('\n');
            paste.append(String.format("OS: %s %s (%s)",
                                       System.getProperty("os.name"),
                                       System.getProperty("os.arch"),
                                       System.getProperty("os.version"))).append('\n');
            paste.append(String.format("Memory Used: %d Mb",
                                       ((NetworkUtils.system().getTotalPhysicalMemorySize() - NetworkUtils.system()
                                                                                                          .getFreePhysicalMemorySize()) / 1024L) / 1024L))
                 .append('\n');
            paste.append(String.format("Memory Free: %d Mb", (NetworkUtils.system().getFreePhysicalMemorySize() / 1024L) / 1024L)).append(
                '\n');
            paste.append(String.format("Memory Max: %d Mb", (NetworkUtils.system().getTotalPhysicalMemorySize() / 1024L) / 1024L)).append(
                '\n');
            paste.append(String.format("CPU Cores: %d", NetworkUtils.system().getAvailableProcessors())).append('\n');
            paste.append("-----END SYSTEM INFO-----").append('\n');
            paste.append("-----BEGIN CLOUDNET INFO-----").append('\n');
            paste.append(String.format("CloudNet Version: %s # %s",
                                       NetworkUtils.class.getPackage().getSpecificationVersion(),
                                       NetworkUtils.class.getPackage().getImplementationVersion())).append('\n');
            paste.append(String.format("Backend: %s", CloudNet.getInstance()
                                                              .getProxyGroup(proxyServer.getProxyInfo()
                                                                                        .getServiceId()
                                                                                        .getGroup())
                                                              .getTemplate()
                                                              .getBackend()
                                                              .name())).append('\n');
            paste.append(String.format("GroupMode: %s", CloudNet.getInstance()
                                                                .getProxyGroup(proxyServer.getProxyInfo()
                                                                                          .getServiceId()
                                                                                          .getGroup())
                                                                .getProxyGroupMode()
                                                                .name())).append('\n');
            paste.append(String.format("Server: %s", proxyServer.getServerId())).append('\n');
            paste.append("-----END CLOUDNET INFO-----").append('\n');
            paste.append("-----BEGIN SERVER INFO-----").append('\n');
            paste.append(g.toJson(proxyServer.getProxyInfo(), TypeToken.get(ProxyInfo.class).getType())).append('\n');
            paste.append("-----END SERVER INFO-----").append('\n');
            paste.append("-----BEGIN GROUP INFO-----").append('\n');
            paste.append(g.toJson(CloudNet.getInstance().getProxyGroup(proxyServer.getProxyInfo().getServiceId().getGroup()),
                                  TypeToken.get(ProxyGroup.class).getType())).append('\n');
            paste.append("-----END GROUP INFO-----").append('\n');
            paste.append("-----BEGIN LOG INFO-----").append('\n');
            CloudNet.getInstance()
                    .getServerLogManager()
                    .getScreenInfos()
                    .getS(random)
                    .forEach(screenInfo -> paste.append(screenInfo.getLine()).append('\n'));
            paste.append("-----END LOG INFO-----").append('\n');
            for (String s : url) {
                if (postTo(sender, s, paste)) {
                    break;
                }
            }
        }, 3 * 50);
    }

    /**
     * Push the log to url
     *
     * @param url     The url to push it
     * @param builder The content of the log
     *
     * @return The finish url
     */
    private boolean postTo(CommandSender sender, String url, StringBuilder builder) {
        try {
            URL pasteUrl = new URL(url + "/documents");
            HttpURLConnection connection = (HttpURLConnection) pasteUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            InputStream inputStream = connection.getInputStream();
            JsonObject object = g.fromJson(new InputStreamReader(inputStream), JsonObject.class);
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                sender.sendMessage("You can see the log at: " + String.format(url + "/%s", object.get("key").getAsString()));
                connection.disconnect();
                return true;
            } else {
                connection.disconnect();
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
