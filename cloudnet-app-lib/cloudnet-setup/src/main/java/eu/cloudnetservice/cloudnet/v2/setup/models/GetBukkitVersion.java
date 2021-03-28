package eu.cloudnetservice.cloudnet.v2.setup.models;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class GetBukkitVersion {
    private final String version;
    private final URL previewURL;

    public GetBukkitVersion(String version, URL previewDownload) {
        this.version = version;
        this.previewURL = previewDownload;
    }

    private URL revealDownload() {
        try {
            URLConnection connection = previewURL.openConnection();
            connection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
            connection.connect();

            InputStream input = connection.getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                for (;;) {
                    String line = reader.readLine();
                    if (line == null) break;
                    if (!line.contains("href=\"https://cdn.getbukkit.org/spigot")) continue;

                    int linkStart = line.indexOf("\"");
                    linkStart++;
                    int linkEnds = line.indexOf("\"", linkStart);

                    String downloadLink = line.substring(linkStart, linkEnds);
                    return new URL(downloadLink);
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public URL getDownloadURL() {
        return revealDownload(); //Only load when needed to minimize delay
    }

    public String getVersion() {
        return version;
    }

}