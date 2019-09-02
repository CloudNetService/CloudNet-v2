package de.dytanic.cloudnet.lib.server.template;

import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Tareko on 18.07.2017.
 */
public class Template {
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

    @Override
    public String toString() {
        return "Template{" + "name='" + name + '\'' + ", backend=" + backend + ", url='" + url + '\'' + ", processPreParameters=" + Arrays.toString(
            processPreParameters) + ", installablePlugins=" + installablePlugins + '}';
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
}
