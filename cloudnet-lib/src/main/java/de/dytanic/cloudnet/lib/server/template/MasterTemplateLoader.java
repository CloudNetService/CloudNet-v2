package de.dytanic.cloudnet.lib.server.template;

import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.zip.ZipConverter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Tareko on 24.09.2017.
 */
public class MasterTemplateLoader {

    private final String url;

    private final Path dest;

    private final SimpledUser simpledUser;

    private final Template template;

    private final String group;

    public MasterTemplateLoader(String url, Path dest, SimpledUser simpledUser, Template template, String group) {
        this.url = url;
        this.dest = dest;
        this.simpledUser = simpledUser;
        this.template = template;
        this.group = group;
    }

    public Template getTemplate() {
        return template;
    }

    public String getUrl() {
        return url;
    }

    public String getGroup() {
        return group;
    }

    public SimpledUser getSimpledUser() {
        return simpledUser;
    }

    public Path getDest() {
        return dest;
    }

    public MasterTemplateLoader load() {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("-Xcloudnet-user", simpledUser.getUserName());
            urlConnection.setRequestProperty("-Xcloudnet-token", simpledUser.getApiToken());
            urlConnection.setRequestProperty("-Xmessage", "template");
            urlConnection.setRequestProperty("-Xvalue", new Document("template", template.getName())
                .append("group", group).convertToJsonString());
            urlConnection.setUseCaches(false);
            urlConnection.connect();

            if (urlConnection.getHeaderField("-Xresponse") == null) {
                Files.copy(urlConnection.getInputStream(), dest);
            }

            urlConnection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MasterTemplateLoader unZip(Path dest) {
        try {
            ZipConverter.extract(this.dest, dest);
            Files.deleteIfExists(this.dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

}
