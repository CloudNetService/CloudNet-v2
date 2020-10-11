/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.wrapper.util;

import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.user.SimpledUser;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.lib.zip.ZipConverter;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MasterTemplateDeploy {

    private final Path dir;

    private final ConnectableAddress connectableAddress;

    private final SimpledUser simpledUser;

    private final Template template;

    private final String group;

    public MasterTemplateDeploy(Path dir,
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

    public Path getDir() {
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
            FileUtility.copyFilesInDirectory(this.dir, dir);
            new File(dir + "/plugins/CloudNetAPI.jar").delete();
        } catch (Exception ex) {
            ex.printStackTrace();
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
