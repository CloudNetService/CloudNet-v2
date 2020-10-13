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

package eu.cloudnetservice.cloudnet.v2.lib.server.template;

import eu.cloudnetservice.cloudnet.v2.lib.user.SimpledUser;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.lib.zip.ZipConverter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

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
