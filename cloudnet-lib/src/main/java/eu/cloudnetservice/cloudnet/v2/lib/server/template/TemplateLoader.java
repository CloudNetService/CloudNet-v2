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
