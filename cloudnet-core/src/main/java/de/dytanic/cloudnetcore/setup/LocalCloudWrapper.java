/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.setup;

import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnet.web.client.WebClient;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Tareko on 01.10.2017.
 */
public class LocalCloudWrapper implements Runnabled<OptionSet> {

    private Process process;

    @Override
    public void run(OptionSet obj)
    {
        if(obj.has("installWrapper"))
        {
            if(!Files.exists(Paths.get("wrapper")))
            {

                try
                {
                    Files.createDirectories(Paths.get("wrapper"));
                    System.out.println("Downloading wrapper...");
                    URLConnection urlConnection = new URL(WebClient.DEFAULT_URL + "cloud/wrapper").openConnection();
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                    urlConnection.connect();
                    Files.copy(urlConnection.getInputStream(), Paths.get("wrapper/CloudNet-Wrapper.jar"));
                    System.out.println("Download completed!");
                } catch (Exception ex)
                {
                    System.out.println("Error on setting up wrapper: " + ex.getMessage());
                    return;
                }
            }

            try
            {
                process = new ProcessBuilder("java", "-Xmx256M", "-jar", "CloudNet-Wrapper.jar").directory(new File("wrapper")).inheritIO().start();
                Runtime.getRuntime().addShutdownHook(new Thread(process::destroy));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}