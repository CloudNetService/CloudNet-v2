package de.dytanic.cloudnet.lib.service.plugin;

public class ServerInstallablePlugin {

    private final String name;
    private final PluginResourceType pluginResourceType;
    private final String url;

    public ServerInstallablePlugin(String name, PluginResourceType pluginResourceType, String url) {
        this.name = name;
        this.pluginResourceType = pluginResourceType;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public PluginResourceType getPluginResourceType() {
        return pluginResourceType;
    }
}
