package de.dytanic.cloudnet.api.builders;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.api.PacketOutStartServer;
import de.dytanic.cloudnet.lib.process.ServerProcessData;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ServerProcessBuilder {
    private final ServerProcessData serverProcessData = new ServerProcessData();

    private ServerProcessBuilder() {
    }

    public static ServerProcessBuilder create(String serverGroupName) {
        return new ServerProcessBuilder().serverGroupName(serverGroupName);
    }

    public ServerProcessBuilder serverGroupName(String serverGroupName) {
        this.serverProcessData.setServerGroupName(serverGroupName);
        return this;
    }

    public ServerProcessBuilder wrapper(String wrapper) {
        this.serverProcessData.setWrapper(wrapper);
        return this;
    }

    public ServerProcessBuilder memory(int memory) {
        this.serverProcessData.setMemory(memory);
        return this;
    }

    public ServerProcessBuilder javaProcessParameters(List<String> javaProcessParameters) {
        this.serverProcessData.setJavaProcessParameters(new ArrayList<>(javaProcessParameters));
        return this;
    }

    public ServerProcessBuilder proxyProcessBuilder(List<String> proxyProcessParameters) {
        this.serverProcessData.setServerProcessParameters(new ArrayList<>(proxyProcessParameters));
        return this;
    }

    public ServerProcessBuilder templateUrl(String templateUrl) {
        this.serverProcessData.setTemplateUrl(templateUrl);
        return this;
    }

    public ServerProcessBuilder plugins(Set<ServerInstallablePlugin> plugins) {
        this.serverProcessData.setPlugins(new HashSet<>(plugins));
        return this;
    }

    public ServerProcessBuilder properties(Document properties) {
        this.serverProcessData.setProperties(properties);
        return this;
    }

    public ServerProcessBuilder template(Template template) {
        this.serverProcessData.setTemplate(template);
        return this;
    }

    public ServerProcessBuilder serverConfig(ServerConfig serverConfig) {
        this.serverProcessData.setServerConfig(serverConfig);
        return this;
    }

    public ServerProcessBuilder addJavaProcessParameter(String parameter) {
        this.serverProcessData.getJavaProcessParameters().add(parameter);
        return this;
    }

    public ServerProcessBuilder addProxyProcessParameter(String parameter) {
        this.serverProcessData.getServerProcessParameters().add(parameter);
        return this;
    }

    public ServerProcessBuilder addPlugin(ServerInstallablePlugin plugin) {
        this.serverProcessData.getPlugins().add(plugin);
        return this;
    }

    public CompletableFuture<ServerProcessMeta> startServer() {
        final UUID uuid = UUID.randomUUID();
        this.serverProcessData.getProperties().append("cloudnet:requestId", uuid);
        CloudAPI.getInstance().getNetworkConnection().sendAsynchronous(
            new PacketOutStartServer(this.serverProcessData)
        );
        return CloudAPI.getInstance().getCloudService().waitForServer(uuid);
    }

    public String getWrapper() {
        return serverProcessData.getWrapper();
    }

    public String getProxyGroup() {
        return serverProcessData.getServerGroupName();
    }

    public int getMemory() {
        return serverProcessData.getMemory();
    }

    public List<String> getJavaProcessParameters() {
        return serverProcessData.getJavaProcessParameters();
    }

    public List<String> getProxyProcessParameters() {
        return serverProcessData.getServerProcessParameters();
    }

    public String getTemplateUrl() {
        return serverProcessData.getTemplateUrl();
    }

    public Set<ServerInstallablePlugin> getPlugins() {
        return serverProcessData.getPlugins();
    }

    public Document getProperties() {
        return serverProcessData.getProperties();
    }

    public ServerConfig getServerConfig() {
        return serverProcessData.getServerConfig();
    }

    public Template getTemplate() {
        return serverProcessData.getTemplate();
    }

    public ServerProcessData getServerProcessData() {
        return serverProcessData;
    }

}
