/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.util;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.zip.ZipConverter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Tareko on 08.10.2017.
 */
public class MasterTemplateDeploy {

    private String dir;

    private ConnectableAddress connectableAddress;

    private SimpledUser simpledUser;

    private boolean ssl;

    private Template template;

    private String group;

    private String customName;

    public MasterTemplateDeploy(String dir,
                                ConnectableAddress connectableAddress,
                                SimpledUser simpledUser,
                                boolean ssl,
                                Template template,
                                String group,
                                String customName) {
        this.dir = dir;
        this.connectableAddress = connectableAddress;
        this.simpledUser = simpledUser;
        this.ssl = ssl;
        this.template = template;
        this.group = group;
        this.customName = customName;
    }

    public String getDir() {
        return dir;
    }

    public ConnectableAddress getConnectableAddress() {
        return connectableAddress;
    }

    public SimpledUser getSimpledUser() {
        return simpledUser;
    }

    public boolean isSsl() {
        return ssl;
    }

    public Template getTemplate() {
        return template;
    }

    public String getGroup() {
        return group;
    }

    public String getCustomName() {
        return customName;
    }

    public void deploy() throws Exception {
        System.out.println("Trying to setup the new template... [" + template.getName() + ']');
        Path dir = Paths.get("local/cache/" + NetworkUtils.randomString(10));
        try {
            FileUtility.copyFilesInDirectory(new File(this.dir), dir.toFile());
            new File(dir.toString() + "/plugins/CloudNetAPI.jar").delete();
        } catch (Exception ex) {
        }
        HttpURLConnection urlConnection = (HttpURLConnection) new URL((ssl ? "https" : "http") + "://" + connectableAddress.getHostName() + ':' + connectableAddress
            .getPort() + "/cloudnet/api/v1/deployment").openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("-Xcloudnet-user", simpledUser.getUserName());
        urlConnection.setRequestProperty("-Xcloudnet-token", simpledUser.getApiToken());
        urlConnection.setRequestProperty("-Xmessage", customName != null ? "custom" : "template");
        urlConnection.setRequestProperty("-Xvalue", customName != null ? customName : new Document("template", template.getName()).append(
            "group",
            group).convertToJsonString());
        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(true);
        urlConnection.connect();
        System.out.println("Connected and deployed template... [" + template.getName() + ']');

        try (OutputStream outputStream = urlConnection.getOutputStream()) {
            outputStream.write(ZipConverter.convert(new Path[] {dir}));
            outputStream.flush();
        }

        try (InputStream inputStream = urlConnection.getInputStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
            inputStream,
            StandardCharsets.UTF_8))) {
            String input;
            while ((input = bufferedReader.readLine()) != null) {
                ;
            }
        }
        System.out.println("Successfully deploy template [" + template.getName() + ']');
        urlConnection.disconnect();
        try {
            FileUtility.deleteDirectory(dir.toFile());
        } catch (Exception ignored) {

        }
    }
}
