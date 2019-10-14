/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.util.FileUtility;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class PacketInCreateTemplate extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {

        if (data.getString("type").equals(DefaultType.BUKKIT.name())) {
            ServerGroup serverGroup = data.getObject("serverGroup", new TypeToken<ServerGroup>() {}.getType());
            try {
                for (Template template : serverGroup.getTemplates()) {
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
                Template template = serverGroup.getGlobalTemplate();
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

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (serverGroup.getServerType().equals(ServerGroupType.CAULDRON)) {
                for (Template template : serverGroup.getTemplates()) {
                    if (!Files.exists(Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/cauldron.jar"))) {
                        try {
                            System.out.println("Downloading cauldron.zip...");
                            File file = new File("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/cauldron.zip");
                            URLConnection connection = new URL("https://yivesmirror.com/files/cauldron/cauldron-1.7.10-2.1403.1.54.zip").openConnection();
                            connection.setUseCaches(false);
                            connection.setRequestProperty("User-Agent",
                                                          "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                            connection.connect();
                            Files.copy(connection.getInputStream(),
                                       Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/cauldron.zip"));
                            System.out.println("Downloading Complete!");

                            ZipFile zip = new ZipFile(file);
                            Enumeration<? extends ZipEntry> entryEnumeration = zip.entries();
                            while (entryEnumeration.hasMoreElements()) {
                                ZipEntry entry = entryEnumeration.nextElement();

                                if (!entry.isDirectory()) {
                                    extractEntry(zip,
                                                 entry,
                                                 "local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName());
                                }
                            }

                            zip.close();
                            file.delete();

                            new File("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/cauldron-1.7.10-2.1403.1.54-server.jar")
                                .renameTo(new File("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/cauldron.jar"));

                            System.out.println("Using a cauldron.jar for your minecraft service template " + serverGroup.getName() + ", please copyFileToDirectory a eula.txt into the template or into the global folder");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Template template = serverGroup.getGlobalTemplate();
                if (!Files.exists(Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/cauldron.jar"))) {
                    try {
                        System.out.println("Downloading cauldron.zip...");
                        File file = new File("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/cauldron.zip");
                        URLConnection connection = new URL("https://yivesmirror.com/files/cauldron/cauldron-1.7.10-2.1403.1.54.zip").openConnection();
                        connection.setUseCaches(false);
                        connection.setRequestProperty("User-Agent",
                                                      "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                        connection.connect();
                        Files.copy(connection.getInputStream(),
                                   Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/cauldron.zip"));
                        System.out.println("Download was completed successfully!");

                        ZipFile zip = new ZipFile(file);
                        Enumeration<? extends ZipEntry> entryEnumeration = zip.entries();
                        while (entryEnumeration.hasMoreElements()) {
                            ZipEntry entry = entryEnumeration.nextElement();

                            if (!entry.isDirectory()) {
                                extractEntry(zip,
                                             entry,
                                             "local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName());
                            }
                        }

                        zip.close();
                        file.delete();

                        new File("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/cauldron-1.7.10-2.1403.1.54-server.jar")
                            .renameTo(new File("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/cauldron.jar"));

                        System.out.println("Using a cauldron.jar for your minecraft service template " + serverGroup.getName() + ", please copyFileToDirectory a eula.txt into the template or into the global folder");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (serverGroup.getServerType().equals(ServerGroupType.GLOWSTONE)) {
                for (Template template : serverGroup.getTemplates()) {
                    Path path = Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/glowstone.jar");
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
                Template template = serverGroup.getGlobalTemplate();
                Path path = Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/glowstone.jar");
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
        } else {
            ProxyGroup proxyGroup = data.getObject("proxyGroup", new TypeToken<ProxyGroup>() {}.getType());
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

    private void extractEntry(ZipFile zipFile, ZipEntry entry, String destDir) throws IOException {
        File file = new File(destDir, entry.getName());

        if (!file.toPath().normalize().startsWith(Paths.get(destDir))) {
            return;
        }

        final byte[] BUFFER = new byte[0xFFFF];

        if (entry.isDirectory()) {
            file.mkdirs();
        } else {
            new File(file.getParent()).mkdirs();

            InputStream is = null;
            OutputStream os = null;

            try {
                is = zipFile.getInputStream(entry);
                os = new FileOutputStream(file);

                int len;
                while ((len = is.read(BUFFER)) != -1) {
                    os.write(BUFFER, 0, len);
                }
            } finally {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            }
        }
    }

}
