/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.setup;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

/**
 * Created by Tareko on 25.05.2017.
 */
public class SetupSpigotVersion implements Consumer<ConsoleReader> {

    private final Consumer<String> download = new Consumer<String>() {
        @Override
        public void accept(String url)
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
    public void accept(ConsoleReader reader)
    {
        System.out.println("No spigot.jar has been found!");

        System.out.println("Choose a minecraft server version [\"taco\", \"paper\", \"hose\", \"spigot\"]" /*" | [\"dytanicspigot\" 1.8.8 BETA]" */);

        String answer = null;

        if(System.getProperty("spigot-type") != null)
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
                    case "taco":
                        answer = "taco";
                        break;
                    case "spigot":
                        answer = "spigot";
                        break;
                    case "paper":
                        answer = "paper";
                        break;
                    case "hose":
                        answer = "hose";
                        break;
                        /*
                    case "dytanicspigot":
                        answer = "dytanicspigot";
                        break;
                        */
                    default: System.out.println("This version is not supported!");
                }

            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        String cancer = null;

        if(System.getProperty("spigot-version") != null)
        {
            answer = System.getProperty("spigot-version");
        }

        switch (answer)
        {
            /*
            case "dytanicspigot":
                download.run("http://dytanic.de/645793465974z9724z24793zt9247tz9274nztv24zt924zt924tz9158929384/dytanicspigot.jar");
                return;
                */
            case "taco":
                System.out.println("Choose a TacoSpigot version [\"1.8.8\", \"1.11.2\", \"1.12.2\"]");
                while (true)
                {
                    try
                    {
                        switch (cancer == null ? reader.readLine().toLowerCase() : cancer)
                        {
                            case "1.8.8":
                                download.accept("https://yivesmirror.com/files/tacospigot/TacoSpigot-1.8.8.jar");
                                return;
                            case "1.11.2":
                                download.accept("https://yivesmirror.com/files/tacospigot/TacoSpigot-1.11.2-b102.jar");
                                return;
                            case "1.12.2":
                                download.accept("https://yivesmirror.com/files/tacospigot/TacoSpigot-1.12.2-b109.jar");
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
            case "spigot":
                System.out.println("Choose a Spigot version [\"1.7.10\", \"1.8.8\", \"1.9.4\", \"1.10.2\", \"1.11.2\", \"1.12.2\"]");
                while (true)
                {
                    try
                    {
                        switch (cancer == null ? reader.readLine().toLowerCase() : cancer)
                        {
                            case "1.7.10":
                                download.accept("https://yivesmirror.com/files/spigot/spigot-1.7.10-SNAPSHOT-b1657.jar");
                                return;
                            case "1.8.8":
                                download.accept("https://yivesmirror.com/files/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar");
                                return;
                            case "1.9.4":
                                download.accept("https://yivesmirror.com/files/spigot/spigot-1.9.4-R0.1-SNAPSHOT.jar");
                                return;
                            case "1.11.2":
                                download.accept("https://yivesmirror.com/files/spigot/spigot-1.11.2-R0.1-SNAPSHOT.jar");
                                return;
                            case "1.10.2":
                                download.accept("https://yivesmirror.com/files/spigot/spigot-1.10.2-R0.1-SNAPSHOT.jar");
                                return;
                            case "1.12.2":
                                download.accept("https://yivesmirror" +
                                        ".com/files/spigot/spigot-1.12.2-R0.1-SNAPSHOT-b1496.jar");
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
            case "paper":
                System.out.println("Choose a PaperSpigot version [\"1.8.8\", \"1.11.2\", \"1.12.2\"]");
                while (true)
                {
                    try
                    {
                        switch (cancer == null ? reader.readLine().toLowerCase() : cancer)
                        {
                            case "1.8.8":
                                download.accept("https://yivesmirror.com/files/paperspigot/PaperSpigot-1.8.8-R0.1-SNAPSHOT-latest.jar");
                                return;
                            case "1.11.2":
                                download.accept("https://yivesmirror.com/files/paperspigot/PaperSpigot-1.11.2-b1104.jar");
                                return;
                            case "1.12.2":
                                download.accept("https://yivesmirror.com/files/paperspigot/PaperSpigot-1.12.2-b1298.jar");
                                break;
                            default:
                                System.out.println("This version is not supported!");
                                break;
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            case "hose":
                System.out.println("Choose a Hose version [\"1.8.8\", \"1.9.4\", \"1.10.2\", \"1.11.2\"]");
                while (true)
                {
                    try
                    {
                        switch (cancer == null ? reader.readLine().toLowerCase() : cancer)
                        {
                            case "1.8.8":
                                download.accept("https://yivesmirror.com/files/hose/hose-1.8.8.jar");
                                return;
                            case "1.9.4":
                                download.accept("https://yivesmirror.com/files/hose/hose-1.9.4.jar");
                                return;
                            case "1.10.2":
                                download.accept("https://yivesmirror.com/files/hose/hose-1.10.2.jar");
                                return;
                            case "1.11.2":
                                download.accept("https://yivesmirror.com/files/hose/hose-1.11.2.jar");
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
        }
    }
}
