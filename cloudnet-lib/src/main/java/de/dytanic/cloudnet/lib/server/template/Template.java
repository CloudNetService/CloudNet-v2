package de.dytanic.cloudnet.lib.server.template;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Created by Tareko on 18.07.2017.
 */
public class Template {

    public static final Type TYPE = TypeToken.get(Template.class).getType();

    private String name;
    private TemplateResource backend;
    private String url;
    private String[] processPreParameters;
    private Collection<ServerInstallablePlugin> installablePlugins;
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
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(processPreParameters, template.processPreParameters)) {
            return false;
        }
        return Objects.equals(installablePlugins, template.installablePlugins);
    }

    @Override
    public String toString() {
        return "de.dytanic.cloudnet.lib.server.template.Template{" +
            "name='" + name + '\'' +
            ", backend=" + backend +
            ", url='" + url + '\'' +
            ", processPreParameters=" + Arrays.toString(processPreParameters) +
            ", installablePlugins=" + installablePlugins +
            '}';
    }
}
