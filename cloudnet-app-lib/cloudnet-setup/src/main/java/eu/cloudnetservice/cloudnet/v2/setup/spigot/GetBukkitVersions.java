package eu.cloudnetservice.cloudnet.v2.setup.spigot;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.setup.models.GetBukkitVersion;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class GetBukkitVersions {
    private static final String versionsUrl = "https://getbukkit.org/download/spigot";

    /**
     * Loads a list of all Spigot Versions available on getbukkit.org
     *
     * @param minVersion All versions bellow this version will be ignored
     * @return array of versions
     */
    public static GetBukkitVersion[] getVersions(@Nullable String minVersion) {
        ArrayList<GetBukkitVersion> versions = new ArrayList<>();
        try {
            URL url = new URL(versionsUrl);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
            connection.connect();

            InputStream input = connection.getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String currentVersion = null;

                boolean exitAfter = false; //Is true when minimum version is reached
                short searchFor = 0; //0 -> "Version" Text; 1 -> Actual Version; 2 -> Download Link

                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;

                    if (line.contains("Version")) {
                        searchFor = 1;
                        continue;
                    }

                    if (searchFor == 1) {
                        int versionStart = line.indexOf(">");
                        versionStart++;
                        int versionEnd = line.indexOf("<", versionStart);
                        String version = line.substring(versionStart, versionEnd);

                        currentVersion = version;
                        searchFor = 2;

                        if (version.equals(minVersion))
                            exitAfter = true;
                        continue;
                    }

                    if (searchFor == 2) {
                        if (!line.contains("href=\"https://getbukkit.org/get/")) continue;

                        int linkStart = line.indexOf("\"");
                        linkStart++;
                        int linkEnds = line.indexOf("\"", linkStart);

                        String downloadPreview = line.substring(linkStart, linkEnds);
                        URL previewURL = new URL(downloadPreview);
                        GetBukkitVersion version = new GetBukkitVersion(currentVersion, previewURL);

                        versions.add(version);
                        searchFor = 0;
                        if (exitAfter) break;
                    }
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return versions.toArray(new GetBukkitVersion[0]);
    }

}