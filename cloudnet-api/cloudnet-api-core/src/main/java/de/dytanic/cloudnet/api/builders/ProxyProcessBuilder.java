package de.dytanic.cloudnet.api.builders;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.api.PacketOutStartProxy;
import de.dytanic.cloudnet.lib.process.ProxyProcessData;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ProxyProcessBuilder {
    private final ProxyProcessData proxyProcessData = new ProxyProcessData();

    private ProxyProcessBuilder() {
    }

    public static ProxyProcessBuilder create(String proxyGroupName) {
        return new ProxyProcessBuilder().proxyGroupName(proxyGroupName);
    }

    public ProxyProcessBuilder proxyGroupName(String proxyGroup) {
        this.proxyProcessData.setProxyGroupName(proxyGroup);
        return this;
    }

    public ProxyProcessBuilder wrapper(String wrapper) {
        this.proxyProcessData.setWrapper(wrapper);
        return this;
    }

    public ProxyProcessBuilder memory(int memory) {
        this.proxyProcessData.setMemory(memory);
        return this;
    }

    public ProxyProcessBuilder javaProcessParameters(List<String> javaProcessParameters) {
        this.proxyProcessData.setJavaProcessParameters(new ArrayList<>(javaProcessParameters));
        return this;
    }

    public ProxyProcessBuilder proxyProcessBuilder(List<String> proxyProcessParameters) {
        this.proxyProcessData.setProxyProcessParameters(new ArrayList<>(proxyProcessParameters));
        return this;
    }

    public ProxyProcessBuilder templateUrl(String templateUrl) {
        this.proxyProcessData.setTemplateUrl(templateUrl);
        return this;
    }

    public ProxyProcessBuilder plugins(Set<ServerInstallablePlugin> plugins) {
        this.proxyProcessData.setPlugins(new HashSet<>(plugins));
        return this;
    }

    public ProxyProcessBuilder properties(Document properties) {
        this.proxyProcessData.setProperties(properties);
        return this;
    }

    public ProxyProcessBuilder addJavaProcessParameter(String parameter) {
        this.proxyProcessData.getJavaProcessParameters().add(parameter);
        return this;
    }

    public ProxyProcessBuilder addProxyProcessParameter(String parameter) {
        this.proxyProcessData.getProxyProcessParameters().add(parameter);
        return this;
    }

    public ProxyProcessBuilder addPlugin(ServerInstallablePlugin plugin) {
        this.proxyProcessData.getPlugins().add(plugin);
        return this;
    }

    public CompletableFuture<ProxyProcessMeta> startProxy() {
        final UUID uuid = UUID.randomUUID();
        this.proxyProcessData.getProperties().append("cloudnet:requestId", uuid);
        CloudAPI.getInstance().getNetworkConnection().sendAsynchronous(
            new PacketOutStartProxy(this.proxyProcessData)
        );
        return CloudAPI.getInstance().getCloudService().waitForProxy(uuid);
    }

    public String getWrapper() {
        return proxyProcessData.getWrapper();
    }

    public String getProxyGroupName() {
        return proxyProcessData.getProxyGroupName();
    }

    public int getMemory() {
        return proxyProcessData.getMemory();
    }

    public List<String> getJavaProcessParameters() {
        return proxyProcessData.getJavaProcessParameters();
    }

    public List<String> getProxyProcessParameters() {
        return proxyProcessData.getProxyProcessParameters();
    }

    public String getTemplateUrl() {
        return proxyProcessData.getTemplateUrl();
    }

    public Set<ServerInstallablePlugin> getPlugins() {
        return proxyProcessData.getPlugins();
    }

    public Document getProperties() {
        return proxyProcessData.getProperties();
    }

    public ProxyProcessData getProxyProcessData() {
        return proxyProcessData;
    }

}
