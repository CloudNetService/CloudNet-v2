/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.client;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.NetworkUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

public class WebClient {

    public static final String DEFAULT_URL = "https://cloudnetservice.eu/cloudnet/";

    public Collection<String> getInstallableModules()
    {
        return handleRequest(DEFAULT_URL + "modules", new TypeToken<Collection<String>>() {
        }.getType());
    }

    public Collection<String> getInstallableTemplates()
    {
        return handleRequest(DEFAULT_URL + "templates", new TypeToken<Collection<String>>() {
        }.getType());
    }

    public String getNewstVersion()
    {
        return getString(DEFAULT_URL + "update/config.json", "version");
    }

    private <E> E handleRequest(String url, Type type)
    {
        try
        {
            URLConnection urlConnection = new java.net.URL(url).openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(1000);
            urlConnection.connect();

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8)))
            {
                JsonObject jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
                return NetworkUtils.GSON.fromJson(jsonObject.get("result"), type);
            }
        } catch (IOException e)
        {
        }
        return null;
    }

    private String getString(String url, String key)
    {
        try
        {
            URLConnection urlConnection = new java.net.URL(url).openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(1000);
            urlConnection.connect();

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8)))
            {
                JsonObject jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
                return jsonObject.get(key).getAsString();
            }
        } catch (IOException e)
        {
        }
        return null;
    }

    public void update(String version)
    {
        try
        {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(DEFAULT_URL + "update/" + (getEnvironment() ? "CloudNet-Master.jar" : "CloudNet-Wrapper.jar")).openConnection();
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(1000);
            httpURLConnection.connect();

            System.out.println("Downloading update...");
            try (InputStream inputStream = httpURLConnection.getInputStream())
            {

                Files.copy(inputStream, (System.getProperty("os.name").toLowerCase().contains("windows") ?
                        Paths.get("CloudNet-" + (getEnvironment() ? "Master" : "Wrapper") + "-Update#" + version + "-" + NetworkUtils.RANDOM.nextLong() + ".jar")
                        :
                        Paths.get(WebClient.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                ), StandardCopyOption.REPLACE_EXISTING);

            } catch (URISyntaxException e)
            {
                e.printStackTrace();
            }

            httpURLConnection.disconnect();
            System.out.println("Download complete!");

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    private boolean getEnvironment()
    {
        try
        {
            Class.forName("de.dytanic.cloudnetcore.CloudNet");
            return true;
        } catch (Exception ex)
        {
            return false;
        }
    }

}
