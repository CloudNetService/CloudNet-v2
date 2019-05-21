package de.dytanic.cloudnet.lib.server.template;

import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

/**
 * Created by Tareko on 18.07.2017.
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Template {

    private String name;
    private TemplateResource backend;
    private String url;
    private String[] processPreParameters;
    private Collection<ServerInstallablePlugin> installablePlugins;

}
