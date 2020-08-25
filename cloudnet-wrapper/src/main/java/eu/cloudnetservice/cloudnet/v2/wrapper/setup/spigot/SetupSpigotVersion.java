package eu.cloudnetservice.cloudnet.v2.wrapper.setup.spigot;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.console.ConsoleManager;
import eu.cloudnetservice.cloudnet.v2.console.model.ConsoleInputDispatch;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.setup.GetBukkitVersion;
import org.jline.reader.LineReader;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 25.05.2017.
 */
public class SetupSpigotVersion implements ConsoleInputDispatch {

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
    private String spigotType;
    private boolean spigot;

    public SetupSpigotVersion(final ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
    }

    private void install(String spigotType) {
        switch (spigotType) {
            case "spigot":
                spigot = true;
                break;
            case "buildtools":
                this.consoleManager.getConsoleRegistry().registerInput(new SpigotBuilder());
                this.consoleManager.changeConsoleInput(SpigotBuilder.class);
                break;
            case "paper":
                this.consoleManager.getConsoleRegistry().registerInput(new PaperBuilder());
                this.consoleManager.changeConsoleInput(PaperBuilder.class);
                break;
        }
    }

    private void installSpigot(String input) {
        System.out.println("Available Spigot Versions for GetBUKKIT:");
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Spigot Version");
        System.out.println("-----------------------------------------------------------------------------");
        Arrays.stream(GetBukkitVersion.values()).map(GetBukkitVersion::getVersion).forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("Please select a version to continue the install process");

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

    private String askForServerType(String input) {
        String answer = null;
        System.out.println("Choose a minecraft server version [\"paper\", \"spigot\", \"buildtools\"]");
        switch (input.toLowerCase()) {
            case "spigot":
                answer = "spigot";
                break;
            case "buildtools":
                answer = "buildtools";
                break;
            case "paper":
                answer = "paper";
                break;
            default:
                System.out.println("This version is not supported!");
        }

        return answer;
    }

    public Path getTarget() {
        return this.target != null ? this.target : Paths.get("local/spigot.jar");
    }

    public void setTarget(Path target) {
        this.target = target;
    }

    @Override
    public void dispatch(final String line, final LineReader lineReader) {
        if (line.length() > 0) {
            if (spigotType == null) {
                this.spigotType = System.getProperty("spigot-type") != null ? System.getProperty("spigot-type") : this.askForServerType(line);
            } else {
                if (spigot) {
                    this.installSpigot(line);
                } else {
                    this.install(spigotType);
                }
            }
        }
    }

    @Override
    public Collection<String> get() {
        if (spigotType == null) {
            return Arrays.asList("paper", "spigot", "buildtools");
        }
        if (spigot) {
            return Arrays.asList("1.8.8",
                                 "1.9.4",
                                 "1.10.2",
                                 "1.11.2",
                                 "1.12.2",
                                 "1.13",
                                 "1.13.1",
                                 "1.13.2",
                                 "1.14",
                                 "1.14.1",
                                 "1.14.2",
                                 "1.14.3",
                                 "1.14.4",
                                 "1.15",
                                 "1.15.1",
                                 "1.15.2");
        }
        return new ArrayList<>();
    }
}
