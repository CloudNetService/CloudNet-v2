package de.dytanic.cloudnet.web.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

public class WebClient {

    public static final String DEFAULT_URL = "https://cloudnetservice.eu/cloudnet/";

    /**
     * Loads all official modules
     *
     * @return a collection containing all official plugins.
     */
    public Collection<String> getInstallableModules() {
        return handleRequest(DEFAULT_URL + "modules", new TypeToken<Collection<String>>() {}.getType());
    }

    /**
     * Handles a request and downloads JSON from {@code url} and parses it as {@code type}
     *
     * @param url  the URL to download the JSON from
     * @param type the type of data to parse as
     * @param <E>  the runtime type of the parsed json
     *
     * @return the data parsed as the specified type or null, if an error occurred.
     */
    private <E> E handleRequest(String url, Type type) {
        try {
            URLConnection urlConnection = new java.net.URL(url).openConnection();
            urlConnection.setRequestProperty("User-Agent",
                                             "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(1000);
            urlConnection.connect();

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),
                                                                                          StandardCharsets.UTF_8))) {
                JsonObject jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
                return NetworkUtils.GSON.fromJson(jsonObject.get("result"), type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads all official templates
     *
     * @return a collection containing all official templates.
     */
    public Collection<String> getInstallableTemplates() {
        return handleRequest(DEFAULT_URL + "templates", new TypeToken<Collection<String>>() {}.getType());
    }

    /**
     * Loads the latest version of the cloud for comparison.
     *
     * @return the latest version of the cloud
     */
    public String getNewstVersion() {
        return getString(DEFAULT_URL + "update/config.json", "version");
    }

    /**
     * Downloads JSON data from the given {@code url} and returns the given
     * {@code key} as a string.
     *
     * @param url the URL to download the JSON data from.
     * @param key the key to read the data from
     *
     * @return the string representation of the data at the given key
     */
    private String getString(String url, String key) {
        try {
            URLConnection urlConnection = new java.net.URL(url).openConnection();
            urlConnection.setRequestProperty("User-Agent",
                                             "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(1000);
            urlConnection.connect();

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),
                                                                                          StandardCharsets.UTF_8))) {
                JsonObject jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
                return jsonObject.get(key).getAsString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update the master or wrapper, depending on the environment
     *
     * @param version the version to download
     *
     * @see #getEnvironment()
     */
    public void update(String version) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(DEFAULT_URL + "update/" + (getEnvironment() ? "CloudNet-Master.jar" : "CloudNet-Wrapper.jar"))
                .openConnection();
            httpURLConnection.setRequestProperty("User-Agent",
                                                 "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(1000);
            httpURLConnection.connect();

            System.out.println("Downloading update...");
            try (InputStream inputStream = httpURLConnection.getInputStream()) {

                boolean windows = System.getProperty("os.name").toLowerCase().contains("windows");
                Path path = windows ? Paths.get("CloudNet-" + (getEnvironment() ? "Master" : "Wrapper") + "-Update#" + version + '-' + NetworkUtils.RANDOM
                    .nextLong() + ".jar") : Paths.get(WebClient.class.getProtectionDomain()
                                                                     .getCodeSource()
                                                                     .getLocation()
                                                                     .toURI()
                                                                     .getPath());
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
                if (windows) {
                    System.out.println("You are using windows, please replace the file [" + Paths.get(WebClient.class.getProtectionDomain()
                                                                                                                     .getCodeSource()
                                                                                                                     .getLocation()
                                                                                                                     .toURI()
                                                                                                                     .getPath())
                                                                                                 .getFileName() + "] with the new file [" + path + ']');
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            httpURLConnection.disconnect();
            System.out.println("Download complete!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks for the CloudNet environment
     *
     * @return true, when on the master, false when on the wrapper
     */
    private boolean getEnvironment() {
        try {
            Class.forName("de.dytanic.cloudnetcore.CloudNet");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Update the local wrapper (only on the master)
     */
    public void updateLocalCloudWrapper(Path outputPath) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(DEFAULT_URL + "update/CloudNet-Wrapper.jar").openConnection();
            httpURLConnection.setRequestProperty("User-Agent",
                                                 "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(1000);
            httpURLConnection.connect();

            System.out.println("Downloading update for local wrapper...");
            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            }

            httpURLConnection.disconnect();
            System.out.println("Download complete!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
