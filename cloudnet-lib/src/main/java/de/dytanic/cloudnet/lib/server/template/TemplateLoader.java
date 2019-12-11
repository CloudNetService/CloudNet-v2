/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.template;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.zip.ZipConverter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TemplateLoader {

    private String url;

    private String dest;

    public TemplateLoader(String url, String dest) {
        this.url = url;
        this.dest = dest;
    }

    public String getUrl() {
        return url;
    }

    public String getDest() {
        return dest;
    }

    public TemplateLoader load() {
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
            urlConnection.setUseCaches(false);
            urlConnection.connect();
            Files.copy(urlConnection.getInputStream(), Paths.get(dest));
            ((HttpURLConnection) urlConnection).disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public TemplateLoader unZip(String dest) {
        try {
            ZipConverter.extract(Paths.get(this.dest), Paths.get(dest));
            new File(this.dest).delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

}
