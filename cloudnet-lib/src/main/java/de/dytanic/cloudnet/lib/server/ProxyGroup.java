/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.proxylayout.ProxyConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * Created by Tareko on 18.07.2017.
 */
@Getter
@Setter
@AllArgsConstructor
public class ProxyGroup
        implements Nameable {

    protected String name;
    protected Collection<String> wrapper;

    protected Template template;
    protected ProxyVersion proxyVersion;

    protected int startPort;
    protected int startup;
    protected int memory;
    protected ProxyConfig proxyConfig;
    protected ProxyGroupMode proxyGroupMode;
    protected WrappedMap settings;

}