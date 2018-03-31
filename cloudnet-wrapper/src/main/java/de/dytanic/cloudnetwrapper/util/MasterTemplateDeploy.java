/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.util;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet3.ZipConverter;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Created by Tareko on 08.10.2017.
 */
@Getter
@AllArgsConstructor
public class MasterTemplateDeploy {

    private String dir;

    private ConnectableAddress connectableAddress;

    private SimpledUser simpledUser;

    private boolean ssl;

    private Template template;

    private String group;

    private String customName;

    public void deploy() throws Exception
    {
        System.out.println("Trying to setup the new template... [" + template.getName() + "]");
        Path file = Paths.get("local/cache/" + NetworkUtils.randomString(10) + ".zip");
        if (!Files.exists(file))
        {
            Files.createFile(file);
        }
        Path dir = Paths.get("local/cache/" + NetworkUtils.randomString(10));
        try
        {
            FileCopy.copyFilesInDirectory(new File(this.dir), dir.toFile());
            new File(dir.toString() + "/plugins/CloudNetAPI.jar").delete();
        } catch (Exception ex)
        {
        }

        ZipConverter.convert(file, dir);
        HttpURLConnection urlConnection = (HttpURLConnection) new URL((ssl ? "https" : "http") +
                "://" +
                connectableAddress.getHostName() +
                ":" +
                connectableAddress.getPort() +
                "/cloudnet/api/v1/deployment")
                .openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("-Xcloudnet-user", simpledUser.getUserName());
        urlConnection.setRequestProperty("-Xcloudnet-token", simpledUser.getApiToken());
        urlConnection.setRequestProperty("-Xmessage", customName != null ? "custom" : "template");
        urlConnection.setRequestProperty("-Xvalue", customName != null ? customName : new Document("template", template.getName()).append("group", group).convertToJsonString());
        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(true);
        urlConnection.connect();
        System.out.println("Connected and deployed template... [" + template.getName() + "]");
        try (OutputStream outputStream = urlConnection.getOutputStream())
        {
            byte[] data = new byte[16344];
            try(InputStream inputStream = Files.newInputStream(file))
            {
                while (inputStream.read(data) != -1)
                {
                    outputStream.write(data);
                    outputStream.flush();
                }
            }
        }
        try(InputStream inputStream = urlConnection.getInputStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
        {
            String input;
            while ((input = bufferedReader.readLine()) != null);
        }
        System.out.println("Successfully deploy template [" + template.getName() + "]");
        urlConnection.disconnect();
        try
        {
            FileUtils.deleteDirectory(dir.toFile());
        }catch (Exception ex) {

        }
        Files.deleteIfExists(file);
    }
}