package eu.cloudnetservice.cloudnet.v2.wrapper.setup.spigot;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.console.ConsoleManager;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleChangeInputPromote;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.setup.GetBukkitVersion;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SetupSpigotVersion implements ConsoleInputDispatch, ConsoleChangeInputPromote {

    private final ConsoleManager consoleManager;

    private Path target;

    private final Predicate<String> download = url -> {
        try {
            System.out.println("Downloading spigot.jar...");
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
            connection.connect();
            try (InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, SetupSpigotVersion.this.getTarget(), StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Download was successfully completed!");
            CloudNetWrapper.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
            CloudNetWrapper.getInstance().getConsoleManager().getConsoleRegistry().unregisterInput(SetupSpigotVersion.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    };
    private boolean spigot, paper, buildtools;

    public SetupSpigotVersion(final ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
    }

    private void install(String spigotType) {
        switch (spigotType) {
            case "spigot":
                spigot = true;
                System.out.println("Available Spigot Versions for GetBUKKIT:");
                System.out.println("-----------------------------------------------------------------------------");
                System.out.println("Spigot Version");
                System.out.println("-----------------------------------------------------------------------------");
                Arrays.stream(GetBukkitVersion.values()).map(GetBukkitVersion::getVersion).forEach(System.out::println);
                System.out.println("-----------------------------------------------------------------------------");
                System.out.println("Please select a version to continue the install process");
                this.consoleManager.setPrompt("spigot>");
                break;
            case "buildtools":
                this.consoleManager.getConsoleRegistry().registerInput(new SpigotBuilder(consoleManager));
                this.buildtools = true;
                break;
            case "paper":
                this.consoleManager.getConsoleRegistry().registerInput(new PaperBuilder(consoleManager));
                this.paper = true;
                break;
            default:
                System.out.println("This option is not available!");
                break;
        }
        if (paper){
            this.consoleManager.changeConsoleInput(PaperBuilder.class);
        } else if (buildtools) {
            this.consoleManager.changeConsoleInput(SpigotBuilder.class);
        }
    }

    private void installSpigot(String input) {
        if (Arrays.stream(GetBukkitVersion.values())
                  .map(GetBukkitVersion::getVersion)
                  .anyMatch(version -> version.equals(input.toLowerCase()))) {
            final Optional<GetBukkitVersion> bukkitVersion = Arrays.stream(GetBukkitVersion.values())
                                                                   .filter(getBukkitVersion -> getBukkitVersion.getVersion()
                                                                                                               .equals(input.toLowerCase()))
                                                                   .findFirst();
            bukkitVersion.ifPresent(getBukkitVersion -> this.download.test(String.format(getBukkitVersion.getUrl(),
                                                                                         getBukkitVersion.getVersion())));
        } else {
            System.out.println("This version is not supported!");
        }
    }

    public Path getTarget() {
        return this.target != null ? this.target : Paths.get("local/spigot.jar");
    }

    public void setTarget(Path target) {
        this.target = target;
    }

    @Override
    public void dispatch(String line, LineReader lineReader) {
        if (paper){
            this.consoleManager.changeConsoleInput(PaperBuilder.class);
            return;
        } else if (buildtools) {
            this.consoleManager.changeConsoleInput(SpigotBuilder.class);
            return;
        }
        if (line.length() > 0) {
            if (spigot) {
                this.installSpigot(line);
            } else {
                this.install(line);
            }
        }
    }

    @Override
    public boolean history() {
        return false;
    }

    @Override
    public Collection<Candidate> get() {
        List<Candidate> candidateList = new ArrayList<>();
        if (!spigot && !buildtools && !paper) {
            candidateList.add(new Candidate("paper",
                                            "paper",
                                            null,
                                            "Selects the installation type of paper fork based on Spigot",
                                            null,
                                            null,
                                            true));
            candidateList.add(new Candidate("spigot", "spigot", null, "Selects the installation type of spigot", null, null, true));
            candidateList.add(new Candidate("buildtools", "buildtools", null, "Selects the installation type of spigot", null, null, true));
        }
        if (spigot) {
            candidateList.addAll(Arrays.stream(GetBukkitVersion.values())
                                       .map(version -> new Candidate(version.getVersion(),
                                                                     version.getVersion(),
                                                                     "Spigot",
                                                                     "Selects the version of spigot",
                                                                     null,
                                                                     null,
                                                                     true))
                                       .collect(Collectors.toList()));


        }
        return candidateList;
    }

    @Override
    public void changePromote(String oldPromote) {
        if (!this.consoleManager.getPrompt().equals(">")) {
            this.consoleManager.setPrompt(">");
            System.out.println("Choose a minecraft server version [\"paper\", \"spigot\", \"buildtools\"]");
        }
    }
}