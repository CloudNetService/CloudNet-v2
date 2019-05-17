/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.setup;

import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnetwrapper.util.PaperBuilder;
import de.dytanic.cloudnetwrapper.util.SpigotBuilder;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by Tareko on 25.05.2017.
 */
public class SetupSpigotVersion
        implements Runnabled<ConsoleReader> {

    private final Runnabled<String> download = new Runnabled<String>() {
        @Override
        public void run(String url)
        {
            try
            {
                System.out.println("Downloading spigot.jar...");
                URLConnection connection = new URL(url).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                connection.connect();
                try (InputStream inputStream = connection.getInputStream())
                {
                    Files.copy(inputStream, Paths.get("local/spigot.jar"), StandardCopyOption.REPLACE_EXISTING);
                }
                System.out.println("Download was successfully completed!");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void run(ConsoleReader reader)
    {
        System.out.println("No spigot.jar has been found!");

        System.out.println("Choose a minecraft server version [\"paper\",\"spigot\", \"buildtools\"]");

        String answer = null;

        if (System.getProperty("spigot-type") != null)
        {
            answer = System.getProperty("spigot-type");
        }

        String input;

        while (answer == null)
        {
            try
            {
                input = reader.readLine();
                switch (input.toLowerCase())
                {
                    case "spigot":
                        answer = "spigot";
                        break;
                    case "buildtools":
                        answer = "buildtools";
                        break;

                    case "paper":
                        answer = "paper";
                        break;
                        /*
                    case "paper":
                        answer = "paper";
                        break;
                        */
                    default:
                        System.out.println("This version is not supported!");
                }

            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if (System.getProperty("spigot-version") != null)
        {
            answer = System.getProperty("spigot-version");
        }

        switch (answer)
        {
            case "spigot":
                System.out.println("Choose a Spigot version [\"1.7.10\", \"1.8.8\", \"1.9.4\", \"1.10.2\", \"1.11.2\", \"1.12.2\", \"1.13\", \"1.13.1\", \"1.13.2\"]");
                while (true)
                {
                    try
                    {
                        switch (reader.readLine().toLowerCase())
                        {
                            case "1.7.10":
                                download.run("https://cdn.getbukkit.org/spigot/spigot-1.7.10-SNAPSHOT-b1657.jar");
                                return;
                            case "1.8.8":
                                download.run("https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar");
                                return;
                            case "1.9.4":
                                download.run("https://cdn.getbukkit.org/spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar");
                                return;
                            case "1.10.2":
                                download.run("https://cdn.getbukkit.org/spigot/spigot-1.10.2-R0.1-SNAPSHOT-latest.jar");
                                return;
                            case "1.11.2":
                                download.run("https://cdn.getbukkit.org/spigot/spigot-1.11.2.jar");
                                return;
                            case "1.12.2":
                                download.run("https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar");
                                return;
                            case "1.13":
                                download.run("https://cdn.getbukkit.org/spigot/spigot-1.13.jar");
                                return;
                            case "1.13.1":
                                download.run("https://cdn.getbukkit.org/spigot/spigot-1.13.1.jar");
                                return;
                            case "1.13.2":
                                download.run("https://cdn.getbukkit.org/spigot/spigot-1.13.2.jar");
                                return;
                            default:
                                System.out.println("This version is not supported!");
                                break;
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            case "buildtools": SpigotBuilder.start(reader);
            case "paper":
                PaperBuilder.start(reader);
                /*
            case "paper":
                System.out.println("Choose a PaperSpigot version [\"1.8.8\", \"1.11.2\", \"1.12.2\", \"1.13.1\", \"1.13.2\"]");
                while (true)
                {
                    try
                    {
                        switch (reader.readLine().toLowerCase())
                        {
                            case "1.8.8":
                                download.run("https://papermc.io/ci/job/Paper/443/artifact/Paperclip.jar");
                                return;
                            case "1.11.2":
                                download.run("https://papermc.io/ci/job/Paper/1104/artifact/paperclip.jar");
                                return;
                            case "1.12.2":
                                download.run("https://papermc.io/ci/job/Paper/1587/artifact/paperclip.jar");
                                return;
                            case "1.13.2":
                                download.run("https://papermc.io/ci/job/Paper-1.13/447/artifact/paperclip.jar");
                                return;
                            default:
                                System.out.println("This version is not supported!");
                                break;
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                */
        }
    }
}
