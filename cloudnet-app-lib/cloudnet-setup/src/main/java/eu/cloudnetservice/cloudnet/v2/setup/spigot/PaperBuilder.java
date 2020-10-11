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

package eu.cloudnetservice.cloudnet.v2.setup.spigot;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.setup.models.PaperMCProject;
import eu.cloudnetservice.cloudnet.v2.setup.models.PaperMCProjectVersion;
import eu.cloudnetservice.cloudnet.v2.setup.utils.StreamThread;
import jline.console.ConsoleReader;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public final class PaperBuilder {

    private static final String API_PROJECT_URL = "https://papermc.io/api/v1/paper";
    private static final String API_PROJECT_VERSION_DOWNLOAD = "https://papermc.io/api/v1/paper/%s/%s/download";
    private static final String API_PROJECT_VERSION_URL = "https://papermc.io/api/v1/paper/%s";
    private static Process exec;

    /**
     * Start the process of choice the paper version And build after choice
     *
     * @param reader Read the answer from console
     */
    public static boolean start(ConsoleReader reader, Path outputPath) {
        try {
            System.out.println("Fetch Versions");
            URLConnection connection = new URL(API_PROJECT_URL).openConnection();
            connection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
            connection.connect();
            PaperMCProject paperMCProject = Document.GSON.fromJson(new InputStreamReader(connection.getInputStream()),
                                                                   PaperMCProject.class);
            System.out.println("Available Paper Versions:");
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("PaperSpigot Version");
            System.out.println("-----------------------------------------------------------------------------");
            Arrays.asList(paperMCProject.getVersions()).forEach(System.out::println);
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("Please select a version to continue the install process");
            String name;
            do {
                name = null;
                try {
                    name = reader.readLine().toLowerCase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final String finalAnswer = name;
                if (Arrays.stream(paperMCProject.getVersions()).anyMatch(e -> e.equalsIgnoreCase(finalAnswer))) {
                    return buildPaperVersion(name, outputPath);
                } else {
                    System.out.println("This version does not exist!");
                }
            } while (name == null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Build or copy the paper spigot
     *
     * @param version the version of the paper jar
     *
     * @throws Exception If a connection error or something
     */
    private static boolean buildPaperVersion(String version, Path outputPath) throws Exception {
        System.out.println(String.format("Fetching build %s", version));
        URLConnection connection = new URL(String.format(API_PROJECT_VERSION_URL, version)).openConnection();
        connection.setRequestProperty("User-Agent",
                                      NetworkUtils.USER_AGENT);
        connection.connect();
        PaperMCProjectVersion paperMCProjectVersion = Document.GSON.fromJson(new InputStreamReader(connection.getInputStream()),
                                                                             PaperMCProjectVersion.class);

        connection = new URL(String.format(API_PROJECT_VERSION_DOWNLOAD,
                                           version,
                                           paperMCProjectVersion.getBuilds().getLatest())).openConnection();
        connection.setRequestProperty("User-Agent",
                                      NetworkUtils.USER_AGENT);
        connection.connect();
        File builder = new File("local/builder/papermc");
        File buildFolder = new File(builder, version);
        Files.createDirectories(buildFolder.toPath());
        File paperclip = new File(buildFolder, "paperclip.jar");
        if (!paperclip.exists()) {
            runPaperClip(connection, buildFolder, paperclip, outputPath);
            return true;
        } else {
            File cacheFolder = new File(buildFolder, "cache");
            File[] patchedFiles = cacheFolder.listFiles(pathname -> pathname.getName().startsWith("patched"));
            if (Objects.requireNonNull(patchedFiles).length > 0) {
                System.out.println("Skipping build");
                System.out.println("Copying spigot.jar");
                try {
                    Files.copy(new FileInputStream(Objects.requireNonNull(patchedFiles)[0]),
                               outputPath,
                               StandardCopyOption.REPLACE_EXISTING);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                SpigotBuilder.deleteBuildFolder(buildFolder);
                runPaperClip(connection, buildFolder, paperclip, outputPath);
                return true;
            }

        }
    }

    /**
     * Run paperclip if the jar not exists
     *
     * @param connection  the connection of the jar
     * @param buildFolder the build folder of the jar
     * @param paperclip   the jar file path
     *
     * @throws IOException Throws if input stream null
     */
    private static void runPaperClip(URLConnection connection, File buildFolder, File paperclip, Path outputPath) throws IOException {
        System.out.println("Downloading Paperclip");
        try (InputStream inputStream = connection.getInputStream()) {
            Files.copy(inputStream, Paths.get(paperclip.toURI()), StandardCopyOption.REPLACE_EXISTING);
        }
        exec = Runtime.getRuntime().exec("java -jar paperclip.jar", null, buildFolder);
        printProcessOutputToConsole(exec);

        File cacheFolder = new File(buildFolder, "cache");
        File[] patchedFiles = cacheFolder.listFiles(pathname -> pathname.getName().startsWith("patched"));
        Files.copy(new FileInputStream(Objects.requireNonNull(patchedFiles)[0]), outputPath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Print the process output to wrapper console
     *
     * @param exec the running process
     *
     * @throws IOException throws if readline null
     */
    static void printProcessOutputToConsole(Process exec) throws IOException {
        CountDownLatch count = new CountDownLatch(2);
        try {
            new Thread(new StreamThread(count, exec.getInputStream())).start();
            new Thread(new StreamThread(count, exec.getErrorStream())).start();
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Build finished!");
        System.out.println("Copying spigot.jar");
    }

    public static Process getExec() {
        return exec;
    }
}
