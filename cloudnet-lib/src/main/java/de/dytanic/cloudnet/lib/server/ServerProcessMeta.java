package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import java.util.Collection;
import java.util.Properties;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 30.07.2017.
 */
@Getter
@AllArgsConstructor
public class ServerProcessMeta {

    private ServiceId serviceId;

    private int memory;

    private boolean priorityStop;

    private String url;

    private String[] processParameters;

    private boolean onlineMode;

    private Collection<ServerInstallablePlugin> downloadablePlugins;

    private ServerConfig serverConfig;

    private String customServerDownload;

    private int port;

    private Properties serverProperties;

    private Template template;

}