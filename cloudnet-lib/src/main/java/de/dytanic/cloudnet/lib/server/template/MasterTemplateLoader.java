/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.template;

import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet.lib.utility.ZipConverter;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Tareko on 24.09.2017.
 */
@Getter
@AllArgsConstructor
public class MasterTemplateLoader {

    private String url;

    private String dest;

    private SimpledUser simpledUser;

    private Template template;

    private String group;

    private String customName;

    public MasterTemplateLoader load()
    {
        try
        {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("-Xcloudnet-user", simpledUser.getUserName());
            urlConnection.setRequestProperty("-Xcloudnet-token", simpledUser.getApiToken());
            urlConnection.setRequestProperty("-Xmessage", customName != null ? "custom" : "template");
            urlConnection.setRequestProperty("-Xvalue", customName != null ? customName : new Document("template", template.getName()).append("group", group).convertToJsonString());
            urlConnection.setUseCaches(false);
            urlConnection.connect();

            if (urlConnection.getHeaderField("-Xresponse") == null)
                Files.copy(urlConnection.getInputStream(), Paths.get(dest));

            urlConnection.disconnect();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public MasterTemplateLoader unZip(String dest)
    {
        try
        {
            ZipConverter.extract(Paths.get(this.dest), Paths.get(dest));
            new File(this.dest).delete();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }

}