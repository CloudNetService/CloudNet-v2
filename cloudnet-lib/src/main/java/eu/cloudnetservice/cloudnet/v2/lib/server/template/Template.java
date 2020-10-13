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

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.lib.service.plugin.ServerInstallablePlugin;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class Template {

    public static final Type TYPE = TypeToken.get(Template.class).getType();

    private final String name;
    private final TemplateResource backend;
    private final String url;
    private final String[] processPreParameters;
    private final Collection<ServerInstallablePlugin> installablePlugins;

    public Template(String name,
                    TemplateResource backend,
                    String url,
                    String[] processPreParameters,
                    Collection<ServerInstallablePlugin> installablePlugins) {
        this.name = name;
        this.backend = backend;
        this.url = url;
        this.processPreParameters = processPreParameters;
        this.installablePlugins = installablePlugins;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public Collection<ServerInstallablePlugin> getInstallablePlugins() {
        return installablePlugins;
    }

    public String[] getProcessPreParameters() {
        return processPreParameters;
    }

    public TemplateResource getBackend() {
        return backend;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (backend != null ? backend.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(processPreParameters);
        result = 31 * result + (installablePlugins != null ? installablePlugins.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Template)) {
            return false;
        }

        final Template template = (Template) o;

        if (!Objects.equals(name, template.name)) {
            return false;
        }
        if (backend != template.backend) {
            return false;
        }
        if (!Objects.equals(url, template.url)) {
            return false;
        }
        if (!Arrays.equals(processPreParameters, template.processPreParameters)) {
            return false;
        }
        return Objects.equals(installablePlugins, template.installablePlugins);
    }

    @Override
    public String toString() {
        return "Template{" +
            "name='" + name + '\'' +
            ", backend=" + backend +
            ", url='" + url + '\'' +
            ", processPreParameters=" + Arrays.toString(processPreParameters) +
            ", installablePlugins=" + installablePlugins +
            '}';
    }
}
