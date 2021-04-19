package eu.cloudnetservice.cloudnet.v2.wrapper.setup.spigot;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.console.ConsoleManager;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleChangeInputPromote;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public final class SpigotBuilder implements ConsoleInputDispatch, ConsoleChangeInputPromote {

    private final static String buildToolsUrl = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";
    private final static String versionsUrl = "https://hub.spigotmc.org/versions/";
    private static Process exec;
    private LinkedList<String> versions;
    private final ConsoleManager consoleManager;

    public SpigotBuilder(final ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;

    }

    /**
     * @return A list for available versions of spigot
     */
    private static LinkedList<String> loadVersions() {
        LinkedList<String> array = new LinkedList<>();
        try {
            Document doc = Jsoup.connect(versionsUrl).userAgent(
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11").get();
            for (Element file : doc.select("a")) {
                String rawName = file.attr("href");
                if (rawName.contains("../")) {
                    continue;
                }
                if (!(rawName.contains("latest.json") || rawName.startsWith("1."))) {
                    continue;
                }
                array.add(rawName.replace(".json", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * Build the spigot or copy if exists
     *
     * @param version the version of spigot
     */
    private static boolean buildSpigot(final String version, Path outputPath) {
        File builder = new File("local/builder/spigot");
        File buildFolder = new File(builder, version);
        try {
            Files.createDirectories(buildFolder.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        File buildTools = new File(buildFolder, "buildtools.jar");
        if (!buildTools.exists()) {
            return runBuildTools(version, buildFolder, buildTools, outputPath);
        } else {
            if (Objects.requireNonNull(buildFolder.listFiles(pathname -> pathname.getName().startsWith("spigot-"))).length > 0) {
                System.out.println("Skipping build");
                System.out.println("Copying spigot.jar");
                try {
                    Files.copy(new FileInputStream(Objects.requireNonNull(buildFolder.listFiles(pathname -> pathname.getName()
                                                                                                                    .startsWith("spigot-")))[0]),
                               outputPath,
                               StandardCopyOption.REPLACE_EXISTING);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return runBuildTools(version, buildFolder, buildTools, outputPath);
            }
        }

    }

    /**
     * Run the build tools of the jar not exists
     *
     * @param version     the version of spigot
     * @param buildFolder the folder in there are build
     * @param buildTools  the path of the build tools
     */
    private static boolean runBuildTools(String version, File buildFolder, File buildTools, Path outputPath) {
        try {
            long startTime = System.currentTimeMillis();
            Files.createDirectories(buildFolder.toPath());
            System.out.println("Downloading BuildTools.jar...");
            URLConnection connection = new URL(buildToolsUrl).openConnection();
            connection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
            connection.connect();
            try (InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, Paths.get(buildTools.toURI()), StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Download was successfully completed!");
            System.out.println("Building Spigot " + version);
            exec = Runtime.getRuntime().exec(String.format("java -jar buildtools.jar --rev %s", version), null, buildFolder);
            PaperBuilder.printProcessOutputToConsole(exec);
            if (Objects.requireNonNull(buildFolder.listFiles(pathname -> pathname.getName().startsWith("spigot-"))).length > 0) {
                Files.copy(new FileInputStream(Objects.requireNonNull(buildFolder.listFiles(pathname -> pathname.getName()
                                                                                                                .startsWith("spigot-")))[0]),
                           outputPath,
                           StandardCopyOption.REPLACE_EXISTING);
                long endTime = System.currentTimeMillis();
                long minutes = ((endTime - startTime) / 1000) / 60;
                long seconds = ((endTime - startTime) / 1000) % 60;
                System.out.printf("Total Build Time %dMin %dSec%n%n", minutes, seconds);
                CloudNetWrapper.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
                CloudNetWrapper.getInstance().getConsoleManager().getConsoleRegistry().unregisterInput(SpigotBuilder.class);
                return true;
            } else {
                deleteBuildFolder(buildFolder);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CloudNetWrapper.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
            CloudNetWrapper.getInstance().getConsoleManager().getConsoleRegistry().unregisterInput(SpigotBuilder.class);
        }
        return false;
    }

    /**
     * Delete the build folder
     *
     * @param buildFolder foldr path
     *
     * @throws IOException throws if tree throws
     */
    static void deleteBuildFolder(File buildFolder) throws IOException {
        Files.walkFileTree(buildFolder.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                File toFile = file.toFile();
                toFile.setExecutable(true, false);
                toFile.setWritable(true, false);
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

                File file = dir.toFile();
                file.setExecutable(true, false);
                file.setWritable(true, false);
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        System.out.println("Build failed. Retry Build");
    }

    public static Process getExec() {
        return exec;
    }

    private void printVersions() {
        System.out.println("Fetching Spigot versions");
        this.versions = loadVersions();
        System.out.println("Available Spigot versions:");
        System.out.println("-----------------------------------------------------------------------------");
        versions.forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Please select a version to continue the install process");
    }

    @Override
    public void dispatch(final String line, final LineReader lineReader) {
        if(line.length() > 0){
            if (versions.stream().anyMatch(e -> e.equalsIgnoreCase(line.toLowerCase()))) {
                if (buildSpigot(line.toLowerCase(), Paths.get("local/spigot.jar"))) {
                    CloudNetWrapper.getInstance().getServerProcessQueue().setRunning(true);
                }
            } else if (versions.stream().noneMatch(e -> e.equalsIgnoreCase(line.toLowerCase()))) {
                System.out.println("This version does not exist!");
            }
        }

    }

    @Override
    public boolean history() {
        return false;
    }

    @Override
    public Collection<Candidate> get() {
        return this.versions.stream().map(s -> new Candidate(s,s,"buildtools","A version to build with the md_5 build tools", null, null, true)).collect(
            Collectors.toList());
    }

    @Override
    public void changePromote(String oldPromote) {
        if (!this.consoleManager.getPrompt().equals("buildtools>")) {
            this.consoleManager.setPrompt("buildtools>");
            printVersions();
        }
    }
}
