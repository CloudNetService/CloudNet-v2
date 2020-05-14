package eu.cloudnetservice.cloudnet.v2.lib.server.template;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.zip.ZipConverter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public class TemplateLoader {

    private final String url;
    private final Path dest;

    public TemplateLoader(String url, Path dest) {
        this.url = url;
        this.dest = dest;
    }

    public Path getDest() {
        return dest;
    }

    public String getUrl() {
        return url;
    }

    public TemplateLoader load() {
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
            urlConnection.setUseCaches(false);
            urlConnection.connect();
            Files.copy(urlConnection.getInputStream(), this.dest);
            ((HttpURLConnection) urlConnection).disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public TemplateLoader unZip(Path dest) {
        try {
            ZipConverter.extract(this.dest, dest);
            Files.deleteIfExists(this.dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

}
