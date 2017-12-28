/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.service.plugin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerInstallablePlugin {

    private String name;

    private PluginResourceType pluginResourceType;

    private String url;

}