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

package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.DefaultType;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroupType;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.wrapper.util.FileUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
                            extractEntry(zip, entry, basePath);
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

    private static void createGlowstoneTemplate(final ServerGroup serverGroup, final Template template) {
        Path path = Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/glowstone.jar");
        downloadGlowstone(path);
    }

    public static void downloadGlowstone(final Path path) {
        if (!Files.exists(path)) {
            try {
                URLConnection connection = new URL("https://yivesmirror.com/grab/glowstone/Glowstone-latest.jar").openConnection();
                connection.setUseCaches(false);
                connection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
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
