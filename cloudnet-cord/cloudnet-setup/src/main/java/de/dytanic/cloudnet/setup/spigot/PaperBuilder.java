package de.dytanic.cloudnet.setup.spigot;

import com.google.gson.Gson;
import de.dytanic.cloudnet.setup.models.PaperMCProject;
import de.dytanic.cloudnet.setup.models.PaperMCProjectVersion;
import de.dytanic.cloudnet.setup.utils.StreamThread;
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

    private static Gson gson = new Gson();
    private static Process exec;
    private static String apiProjectUrl = "https://papermc.io/api/v1/paper";
    private static String apiProjectVersionDownload = "https://papermc.io/api/v1/paper/%s/%s/download";
    private static String API_PROJECT_VERSION_URL = "https://papermc.io/api/v1/paper/%s";

    /**
     * Start the process of choice the paper version And build after choice
     *
     * @param reader Read the answer from console
     */
    public static boolean start(ConsoleReader reader, Path outputPath) {
        try {
            System.out.println("Fetch Versions");
            URLConnection connection = new URL(apiProjectUrl).openConnection();
            connection.setRequestProperty("User-Agent",
                                          "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            PaperMCProject paperMCProject = gson.fromJson(new InputStreamReader(connection.getInputStream()), PaperMCProject.class);
            System.out.println("Available Paper Versions:");
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("PaperSpigot Version");
            System.out.println("-----------------------------------------------------------------------------");
            Arrays.asList(paperMCProject.getVersions()).forEach(System.out::println);
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("Please select a version to continue the install process");
            String answer = null;
            while (answer == null) {
                String name = null;
                try {
                    name = reader.readLine().toLowerCase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String finalAnswer = name;
                if (Arrays.stream(paperMCProject.getVersions()).anyMatch(e -> e.equalsIgnoreCase(finalAnswer))) {
                    answer = name;
                    return buildPaperVersion(answer, outputPath);
                } else if (Arrays.stream(paperMCProject.getVersions()).noneMatch(e -> e.equalsIgnoreCase(finalAnswer))) {
                    System.out.println("This version does not exist!");
                }
            }
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
                                      "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();
        PaperMCProjectVersion paperMCProjectVersion = gson.fromJson(new InputStreamReader(connection.getInputStream()),
                                                                    PaperMCProjectVersion.class);

        connection = new URL(String.format(apiProjectVersionDownload,
                                           version,
                                           paperMCProjectVersion.getBuilds().getLatest())).openConnection();
        connection.setRequestProperty("User-Agent",
                                      "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
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
