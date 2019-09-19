/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.proxylayout.ProxyConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;

import java.util.Collection;

/**
 * Created by Tareko on 18.07.2017.
 */
public class ProxyGroup implements Nameable {

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

    public ProxyGroup(String name,
                      Collection<String> wrapper,
                      Template template,
                      ProxyVersion proxyVersion,
                      int startPort,
                      int startup,
                      int memory,
                      ProxyConfig proxyConfig,
                      ProxyGroupMode proxyGroupMode,
                      WrappedMap settings) {
        this.name = name;
        this.wrapper = wrapper;
        this.template = template;
        this.proxyVersion = proxyVersion;
        this.startPort = startPort;
        this.startup = startup;
        this.memory = memory;
        this.proxyConfig = proxyConfig;
        this.proxyGroupMode = proxyGroupMode;
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "ProxyGroup{" + "name='" + name + '\'' + ", wrapper=" + wrapper + ", template=" + template + ", proxyVersion=" + proxyVersion + ", startPort=" + startPort + ", startup=" + startup + ", memory=" + memory + ", proxyConfig=" + proxyConfig + ", proxyGroupMode=" + proxyGroupMode + ", settings=" + settings + '}';
    }

    @Override
    public String getName() {
        return name;
    }

    public int getMemory() {
        return memory;
    }

    public Template getTemplate() {
        return template;
    }

    public int getStartPort() {
        return startPort;
    }

    public Collection<String> getWrapper() {
        return wrapper;
    }

    public int getStartup() {
        return startup;
    }

    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public ProxyGroupMode getProxyGroupMode() {
        return proxyGroupMode;
    }

    public ProxyVersion getProxyVersion() {
        return proxyVersion;
    }

    public WrappedMap getSettings() {
        return settings;
    }
}
