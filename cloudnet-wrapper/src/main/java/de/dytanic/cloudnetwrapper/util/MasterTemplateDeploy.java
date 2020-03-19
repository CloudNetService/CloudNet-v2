package de.dytanic.cloudnetwrapper.util;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.zip.ZipConverter;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Tareko on 08.10.2017.
 */
public class MasterTemplateDeploy {

    private String dir;

    private ConnectableAddress connectableAddress;

    private SimpledUser simpledUser;

    private Template template;

    private String group;

    public MasterTemplateDeploy(String dir,
                                ConnectableAddress connectableAddress,
                                SimpledUser simpledUser,
                                Template template,
                                String group) {
        this.dir = dir;
        this.connectableAddress = connectableAddress;
        this.simpledUser = simpledUser;
        this.template = template;
        this.group = group;
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

    public Template getTemplate() {
        return template;
    }

    public String getGroup() {
        return group;
    }

    public void deploy() throws Exception {
        System.out.println("Trying to setup the new template... [" + template.getName() + ']');
        Path dir = Paths.get("local/cache/" + NetworkUtils.randomString(10));
        try {
            FileUtility.copyFilesInDirectory(new File(this.dir), dir.toFile());
            new File(dir + "/plugins/CloudNetAPI.jar").delete();
        } catch (Exception ex) {
        }
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(
            String.format("http://%s:%d/cloudnet/api/v1/deployment",
                          connectableAddress.getHostName(),
                          connectableAddress.getPort()))
            .openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("-Xcloudnet-user", simpledUser.getUserName());
        urlConnection.setRequestProperty("-Xcloudnet-token", simpledUser.getApiToken());
        urlConnection.setRequestProperty("-Xmessage", "template");
        urlConnection.setRequestProperty("-Xvalue", new Document("template", template.getName())
            .append("group", group).convertToJsonString());
        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(true);
        urlConnection.connect();
        System.out.println("Connected and deployed template... [" + template.getName() + ']');

        try (OutputStream outputStream = urlConnection.getOutputStream()) {
            outputStream.write(ZipConverter.convert(new Path[] {dir}));
            outputStream.flush();
        }
        System.out.println("Successfully deploy template [" + template.getName() + ']');
        urlConnection.disconnect();
        try {
            FileUtility.deleteDirectory(dir.toFile());
        } catch (Exception ignored) {

        }
    }
}
