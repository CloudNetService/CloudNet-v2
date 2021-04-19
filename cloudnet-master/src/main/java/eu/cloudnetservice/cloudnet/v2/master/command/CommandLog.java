package eu.cloudnetservice.cloudnet.v2.master.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class CommandLog extends Command implements TabCompletable {

    private final Gson g = new GsonBuilder().setPrettyPrinting().create();

    private final String version = "1";

    public CommandLog() {
        super("log", "cloudnet.command.log");

        description = "Creates a server log on a paste server";

    }

    private static void addLog(final String random, final StringBuilder paste) {
        paste.append("-----END GROUP INFO-----").append('\n');
        paste.append("-----BEGIN LOG INFO-----").append('\n');
        CloudNet.getInstance()
                .getServerLogManager()
                .getScreenInfos()
                .getS(random)
                .forEach(screenInfo -> paste.append(screenInfo.getLine()).append('\n'));
        paste.append("-----END LOG INFO-----").append('\n');
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

    @Override
    public void onExecuteCommand(CommandSender sender, ParsedLine parsedLine) {
        if (parsedLine.words().size() == 2) {
            String commandArgument = parsedLine.words().get(1);
            if (CloudNet.getInstance().getServers().containsKey(commandArgument)) {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(commandArgument);
                if (minecraftServer == null) {
                    sender.sendMessage("§cThe given server does not exist.");
                    return;
                }
                String rndm = NetworkUtils.randomString(10);
                CloudNet.getInstance().getServerLogManager().append(rndm, minecraftServer.getServerId());
                List<String> hasteServer = CloudNet.getInstance().getConfig().getHasteServer().stream()
                                                   .filter(this::checkUrl)
                                                   .collect(Collectors.toList());
                sendMinecraftServerPaste(sender, rndm, hasteServer, minecraftServer);
            } else if (CloudNet.getInstance().getProxys().containsKey(commandArgument)) {
                ProxyServer proxyServer = CloudNet.getInstance().getProxy(commandArgument);
                if (proxyServer == null) {
                    sender.sendMessage("§cThe given proxy does not exist.");
                    return;
                }
                String rndm = NetworkUtils.randomString(10);
                CloudNet.getInstance().getServerLogManager().append(rndm, proxyServer.getServerId());
                List<String> hasteServer = CloudNet.getInstance().getConfig().getHasteServer().stream()
                                                   .filter(this::checkUrl)
                                                   .collect(Collectors.toList());
                sendProxyServerPaste(sender, rndm, hasteServer, proxyServer);
            } else {
                sender.sendMessage("§cThe server/proxy doesn't exist!");
            }
        } else {
            sender.sendMessage("log <server/proxy> | Creates a web server log");
        }
    }

    /**
     * Send the log to a random past site
     *
     * @param sender          The sender of the command
     * @param random          A random string
     * @param urls            The urls of the paste sites
     * @param minecraftServer The minecraft server with the log inside
     */
    private void sendMinecraftServerPaste(CommandSender sender, String random, List<String> urls, MinecraftServer minecraftServer) {
        CloudNet.getExecutor().schedule(() -> {
            final String backend = minecraftServer.getServerInfo().getTemplate().getBackend().name();
            final String groupMode = minecraftServer.getGroup().getGroupMode().name();
            StringBuilder paste = buildHeader(backend, groupMode, minecraftServer.getServerId());
            paste.append("-----BEGIN SERVER INFO-----").append('\n');
            paste.append(g.toJson(minecraftServer.getServerInfo(), ServerInfo.TYPE)).append('\n');
            paste.append("-----END SERVER INFO-----").append('\n');
            paste.append("-----BEGIN GROUP INFO-----").append('\n');
            paste.append(g.toJson(minecraftServer.getGroup(), ServerGroup.TYPE)).append('\n');
            addLog(random, paste);
            sendLog(sender, urls, paste);
        }, 3, TimeUnit.SECONDS);
    }

    private void sendLog(final CommandSender sender, final List<String> urls, final StringBuilder paste) {
        for (String s : urls) {
            if (postTo(sender, s, paste)) {
                break;
            }
        }
    }

    /**
     * Send the log to a random past site
     *
     * @param sender      The sender of the command
     * @param random      A random string
     * @param urls        The urls of the paste sites
     * @param proxyServer The proxy server with the log inside
     */
    private void sendProxyServerPaste(CommandSender sender, String random, List<String> urls, ProxyServer proxyServer) {
        CloudNet.getExecutor().schedule(() -> {
            final ProxyGroup proxyGroup = CloudNet.getInstance().getProxyGroup(proxyServer.getProxyInfo().getServiceId().getGroup());
            final String backend = proxyGroup.getTemplate().getBackend().name();
            final String groupMode = proxyGroup.getProxyGroupMode().name();
            final StringBuilder paste = buildHeader(backend, groupMode, proxyServer.getServerId());
            paste.append("-----BEGIN PROXY INFO-----").append('\n');
            paste.append(g.toJson(proxyServer.getProxyInfo(), ProxyInfo.TYPE)).append('\n');
            paste.append("-----END PROXY INFO-----").append('\n');
            paste.append("-----BEGIN GROUP INFO-----").append('\n');
            paste.append(g.toJson(proxyGroup, ProxyGroup.TYPE)).append('\n');
            addLog(random, paste);
            sendLog(sender, urls, paste);
        }, 3, TimeUnit.SECONDS);
    }

    private StringBuilder buildHeader(final String backend, final String groupMode, final String serverId) {
        StringBuilder paste = new StringBuilder();
        paste.append("LOG VERSION: " + version).append('\n');
        paste.append("-----BEGIN SYSTEM INFO-----").append('\n');
        paste.append(String.format("Time: %s", ZonedDateTime.now())).append('\n');
        paste.append(String.format("Server: %s", serverId)).append('\n');
        paste.append(String.format("Java Version: %s", System.getProperty("java.version"))).append('\n');
        paste.append(String.format("User: %s", System.getProperty("user.name"))).append('\n');
        paste.append(String.format("OS: %s %s (%s)",
                                   System.getProperty("os.name"),
                                   System.getProperty("os.arch"),
                                   System.getProperty("os.version"))).append('\n');
        paste.append(String.format("Memory Used: %d Mb",
                                   (NetworkUtils.system().getTotalPhysicalMemorySize() -
                                       NetworkUtils.system().getFreePhysicalMemorySize()) >> 20))
             .append('\n');
        paste.append(String.format("Memory Free: %d Mb", (NetworkUtils.system().getFreePhysicalMemorySize() / 1024L) / 1024L))
             .append('\n');
        paste.append(String.format("Memory Max: %d Mb", (NetworkUtils.system().getTotalPhysicalMemorySize() / 1024L) / 1024L))
             .append('\n');
        paste.append(String.format("CPU Cores: %d", NetworkUtils.system().getAvailableProcessors())).append('\n');
        paste.append("-----END SYSTEM INFO-----").append('\n');
        paste.append("-----BEGIN CLOUDNET INFO-----").append('\n');
        paste.append(String.format("CloudNet Version: %s # %s",
                                   NetworkUtils.class.getPackage().getSpecificationVersion(),
                                   NetworkUtils.class.getPackage().getImplementationVersion())).append('\n');
        paste.append(String.format("Backend: %s", backend)).append('\n');
        paste.append(String.format("GroupMode: %s", groupMode)).append('\n');
        paste.append(String.format("Server: %s", serverId)).append('\n');
        paste.append("-----END CLOUDNET INFO-----").append('\n');
        return paste;
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
            try (OutputStream outputStream = connection.getOutputStream();
                 InputStream inputStream = connection.getInputStream()
            ) {
                outputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                JsonObject object = g.fromJson(new InputStreamReader(inputStream), JsonObject.class);
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    sender.sendMessage("§aYou can see the log at: " + String.format(url + "/%s", object.get("key").getAsString()));
                    return true;
                } else {
                    return false;
                }
            } finally {
                connection.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Candidate> onTab(ParsedLine parsedLine) {
        List<Candidate> strings = new ArrayList<>();
        if (parsedLine.words().size() == 1) {
            if (parsedLine.words().get(0).equalsIgnoreCase("log")) {
                strings.addAll(CloudNet.getInstance().getServers().values().stream().map(minecraftServer -> new Candidate(minecraftServer.getName(), minecraftServer.getName(), minecraftServer.getGroup().getName(), "A simple minecraft server", null, null, true)).collect(
                    Collectors.toList()));
                strings.addAll(CloudNet.getInstance().getProxys().values().stream().map(proxyServer -> new Candidate(proxyServer.getName(), proxyServer.getName(), proxyServer.getProcessMeta().getProxyGroupName(), "A simple proxy", null, null, true)).collect(
                    Collectors.toList()));
                return strings;
            }

        }
        return strings;
    }
}
