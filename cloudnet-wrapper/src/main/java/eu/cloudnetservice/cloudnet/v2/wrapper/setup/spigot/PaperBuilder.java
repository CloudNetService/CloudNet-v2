package eu.cloudnetservice.cloudnet.v2.wrapper.setup.spigot;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.console.ConsoleManager;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleChangeInputPromote;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.setup.models.PaperMCProject;
import eu.cloudnetservice.cloudnet.v2.setup.models.PaperMCProjectVersion;
import eu.cloudnetservice.cloudnet.v2.setup.utils.StreamThread;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public final class PaperBuilder implements ConsoleInputDispatch, ConsoleChangeInputPromote {

    private static final String API_PROJECT_URL = "https://papermc.io/api/v1/paper";
    private static final String API_PROJECT_VERSION_DOWNLOAD = "https://papermc.io/api/v1/paper/%s/%s/download";
    private static final String API_PROJECT_VERSION_URL = "https://papermc.io/api/v1/paper/%s";
    private static Process exec;
    private PaperMCProject paperMCProject;
    private final ConsoleManager consoleManager;

    public PaperBuilder(ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
        try {
            URLConnection connection = new URL(API_PROJECT_URL).openConnection();
            connection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
            connection.connect();
            paperMCProject = Document.GSON.fromJson(new InputStreamReader(connection.getInputStream()),
                                                    PaperMCProject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Build or copy the paper spigot
     *
     * @param version the version of the paper jar
     *
     * @throws Exception If a connection error or something
     */
    private static boolean buildPaperVersion(String version, Path outputPath) throws Exception {
        System.out.printf("Fetching build %s", version);
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
                    CloudNetWrapper.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
                    CloudNetWrapper.getInstance().getConsoleManager().getConsoleRegistry().unregisterInput(PaperBuilder.class);
                    CloudNetWrapper.getInstance().getConsoleManager().getConsoleRegistry().unregisterInput(SetupSpigotVersion.class);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    CloudNetWrapper.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
                    CloudNetWrapper.getInstance().getConsoleManager().getConsoleRegistry().unregisterInput(PaperBuilder.class);
                    CloudNetWrapper.getInstance().getConsoleManager().getConsoleRegistry().unregisterInput(SetupSpigotVersion.class);
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
        CloudNetWrapper.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
        CloudNetWrapper.getInstance().getConsoleManager().getConsoleRegistry().unregisterInput(PaperBuilder.class);
        CloudNetWrapper.getInstance().getServerProcessQueue().setRunning(true);
    }

    /**
     * Print the process output to wrapper console
     *
     * @param exec the running process
     *
     */
    protected static void printProcessOutputToConsole(Process exec) {
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

    private void printVersions() {
        System.out.println("Available Paper Versions:");
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("PaperSpigot Version");
        System.out.println("-----------------------------------------------------------------------------");
        Arrays.asList(paperMCProject.getVersions()).forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Please select a version to continue the install process");
    }

    @Override
    public void dispatch(final String line, final LineReader lineReader) {
        if (line.length() > 0) {
            if (Arrays.stream(paperMCProject.getVersions()).anyMatch(e -> e.equalsIgnoreCase(line))) {
                try {
                    buildPaperVersion(line.toLowerCase(), Paths.get("local/spigot.jar"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
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
        return Arrays.stream(paperMCProject.getVersions()).map(s -> new Candidate(s,s,paperMCProject.getProject(),"A paper version",null,null,true)).collect(
            Collectors.toList());
    }

    @Override
    public void changePromote(String oldPromote) {
        if (!this.consoleManager.getPrompt().equals("paper>")) {
            this.consoleManager.setPrompt("paper>");
            printVersions();
        }
    }
}
