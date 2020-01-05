package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnetwrapper.server.CloudGameServer;
import de.dytanic.cloudnetwrapper.util.FileUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class PacketInCreateTemplate implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packet.getData().getString("type").equals(DefaultType.BUKKIT.name())) {
            ServerGroup serverGroup = packet.getData().getObject("serverGroup", ServerGroup.TYPE);
            try {
                for (Template template : serverGroup.getTemplates()) {
                    createBukkitTemplate(serverGroup, template);
                }
                createBukkitTemplate(serverGroup, serverGroup.getGlobalTemplate());

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (serverGroup.getServerType().equals(ServerGroupType.CAULDRON)) {
                for (Template template : serverGroup.getTemplates()) {
                    createCauldronTemplate(serverGroup, template);
                }
                createCauldronTemplate(serverGroup, serverGroup.getGlobalTemplate());
            }

            if (serverGroup.getServerType().equals(ServerGroupType.GLOWSTONE)) {
                for (Template template : serverGroup.getTemplates()) {
                    createGlowstoneTemplate(serverGroup, template);
                }
                createGlowstoneTemplate(serverGroup, serverGroup.getGlobalTemplate());
            }
        } else {
            ProxyGroup proxyGroup = packet.getData().getObject("proxyGroup", ProxyGroup.TYPE);
            try {
                if (!Files.exists(Paths.get("local/templates/" + proxyGroup.getName()))) {
                    System.out.println("Creating GroupTemplate for " + proxyGroup.getName() + " DEFAULT...");
                    Files.createDirectories(Paths.get("local/templates/" + proxyGroup.getName() + "/plugins"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createBukkitTemplate(final ServerGroup serverGroup, final Template template) throws IOException {
        if (!Files.exists(Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName()))) {
            System.out.println("Creating GroupTemplate for " + serverGroup.getName() + ' ' + template.getName() + "...");
            Files.createDirectories(Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + ""));
        }

        if (!Files.exists(Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/server.properties"))) {
            FileUtility.insertData("files/server.properties",
                                   "local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/server.properties");
        }

        if (!Files.exists(Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/plugins"))) {
            new File("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/plugins").mkdir();
        }
    }

    private static void createCauldronTemplate(final ServerGroup serverGroup, final Template template) {
        Path basePath = Paths.get("local", "templates", serverGroup.getName(), template.getName());
        if (!Files.exists(basePath.resolve("cauldron.jar"))) {
            try {
                System.out.println("Downloading cauldron.zip...");
                Path path = basePath.resolve("cauldron.zip");

                URLConnection connection = new URL("https://yivesmirror.com/files/cauldron/cauldron-1.7.10-2.1403.1.54.zip").openConnection();
                connection.setUseCaches(false);
                connection.setRequestProperty("User-Agent",
                                              NetworkUtils.USER_AGENT);
                connection.connect();
                try (InputStream inputStream = connection.getInputStream()) {
                    Files.copy(inputStream, basePath.resolve("cauldron.zip"));
                }
                System.out.println("Download completed successfully!");

                try (ZipFile zip = new ZipFile(path.toFile())) {
                    Enumeration<? extends ZipEntry> entryEnumeration = zip.entries();
                    while (entryEnumeration.hasMoreElements()) {
                        ZipEntry entry = entryEnumeration.nextElement();
                        if (!entry.isDirectory()) {
                            CloudGameServer.extractEntry(zip, entry, basePath);
                        }
                    }

                }
                Files.deleteIfExists(path);

                Files.move(basePath.resolve("cauldron-1.7.10-2.1403.1.54-server.jar"),
                           basePath.resolve("cauldron.jar"));
                System.out.println("Using a cauldron.jar for your minecraft service template " + serverGroup.getName() + ", please copy a eula.txt into the template or into the global folder");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void createGlowstoneTemplate(final ServerGroup serverGroup, final Template template) {
        Path path = Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/glowstone.jar");
        CloudGameServer.downloadGlowstone(path);
    }

}
