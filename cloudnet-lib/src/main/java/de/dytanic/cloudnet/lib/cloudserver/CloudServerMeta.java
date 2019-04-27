/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.cloudserver;

import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import lombok.Getter;

/**
 * Created by Tareko on 17.10.2017.
 */
@Getter
public class CloudServerMeta {

    private ServiceId serviceId;

    private int memory;

    private boolean priorityStop;

    private String[] processParameters;

    private Collection<ServerInstallablePlugin> plugins;

    private ServerConfig serverConfig;

    private int port;

    private String templateName;

    private Properties serverProperties;

    private ServerGroupType serverGroupType;

    private Template template;

    public CloudServerMeta(ServiceId serviceId, int memory, boolean priorityStop, String[] processParameters, Collection<ServerInstallablePlugin> plugins, ServerConfig serverConfig, int port, String templateName, Properties properties, ServerGroupType serverGroupType)
    {
        this.serviceId = serviceId;
        this.memory = memory;
        this.priorityStop = priorityStop;
        this.processParameters = processParameters;
        this.plugins = plugins;
        this.serverConfig = serverConfig;
        this.port = port;
        this.templateName = templateName;
        this.serverProperties = properties;
        this.serverGroupType = serverGroupType;
        this.template = new Template(templateName, TemplateResource.MASTER, null, new String[0], new ArrayList<>());
    }
}