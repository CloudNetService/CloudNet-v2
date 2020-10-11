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

package eu.cloudnetservice.cloudnet.v2.lib.network.http;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;

import java.net.URLConnection;

public class URLBuilder implements Cloneable {

    private final StringBuilder urlString = new StringBuilder();
    private final StringBuilder param = new StringBuilder().append('?');

    public URLBuilder(String http, String mainUrl) {
        this.urlString.append(http).append("://").append(mainUrl).append(NetworkUtils.SLASH_STRING);
    }

    public StringBuilder getParam() {
        return param;
    }

    public StringBuilder getUrlString() {
        return urlString;
    }

    public URLBuilder path(String path) {
        if (urlString.substring(0).endsWith(NetworkUtils.SLASH_STRING)) {
            urlString.append(path);
        } else {
            urlString.append(NetworkUtils.SLASH_STRING).append(path);
        }
        return this;
    }

    public URLBuilder query(String queryKey, String queryValue) {
        param.append(queryKey).append('=').append(queryValue).append('&');
        return this;
    }

    public java.net.URL url() {
        try {
            return new java.net.URL(urlString.substring(0) + param.substring(0));
        } catch (Exception ex) {
            return null;
        }
    }


    public URLConnection urlConnection() {
        try {
            return new java.net.URL(urlString.substring(0) + param.substring(0)).openConnection();
        } catch (Exception ex) {
            return null;
        }
    }

    public URLBuilder clone() {
        try {
            return (URLBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return this;
    }

}
