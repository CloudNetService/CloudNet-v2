/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup.spigot;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Tareko on 25.05.2017.
 */
public class SetupSpigotVersion implements Consumer<ConsoleReader> {

    private Path target;

    private final Predicate<String> download = url -> {
        try {
            System.out.println("Downloading spigot.jar...");
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent",
                                          "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            try (InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, SetupSpigotVersion.this.getTarget(), StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Download was successfully completed!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    };

    /**
     * A method to choose on start the spigot version
     *
     * @param reader The console reader
     */
    @Override
    public void accept(ConsoleReader reader) {
        System.out.println("No spigot.jar has been found!");

        String spigotType;
        do {
            System.out.println("Choose a minecraft server version [\"paper\", \"spigot\", \"buildtools\"]");
            spigotType = System.getProperty("spigot-type") != null ? System.getProperty("spigot-type") : this.askForServerType(reader);
        } while (!this.install(reader, spigotType));
    }

    private boolean install(ConsoleReader reader, String spigotType) {
        switch (spigotType) {
            case "spigot":
                return this.installSpigot(reader);
            case "buildtools":
                return SpigotBuilder.start(reader, this.getTarget());
            case "paper":
                return PaperBuilder.start(reader, this.getTarget());
        }
        return false;
    }

    private boolean installSpigot(ConsoleReader reader) {
        System.out.println(
            "Choose a Spigot version [\"1.7.10\", \"1.8.8\", \"1.9.4\", \"1.10.2\", \"1.11.2\", \"1.12.2\", \"1.13\", \"1.13.1\", \"1.13.2\", \"1.14\", \"1.14.1\", \"1.14.2\", \"1.14.3\", \"1.14.4\"]");
        while (true) {
            try {
                switch (reader.readLine().toLowerCase()) {
                    case "1.7.10":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.7.10-SNAPSHOT-b1657.jar");
                    case "1.8.8":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar");
                    case "1.9.4":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar");
                    case "1.10.2":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.10.2-R0.1-SNAPSHOT-latest.jar");
                    case "1.11.2":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.11.2.jar");
                    case "1.12.2":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar");
                    case "1.13":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.13.jar");
                    case "1.13.1":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.13.1.jar");
                    case "1.13.2":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.13.2.jar");
                    case "1.14":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.14.jar");
                    case "1.14.1":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.14.1.jar");
                    case "1.14.2":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.14.2.jar");
                    case "1.14.3":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.14.3.jar");
                    case "1.14.4":
                        return this.download.test("https://cdn.getbukkit.org/spigot/spigot-1.14.4.jar");
                    default:
                        System.out.println("This version is not supported!");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String askForServerType(ConsoleReader reader) {
        String answer = null;

        String input;

        while (answer == null) {
            try {
                input = reader.readLine();
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

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return answer;
    }

    public Path getTarget() {
        return this.target != null ? this.target : Paths.get("local/spigot.jar");
    }

    public void setTarget(Path target) {
        this.target = target;
    }
}
