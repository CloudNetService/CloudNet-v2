package eu.cloudnetservice.v2.lib.service.plugin;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Set;

public class ServerInstallablePlugin {

    public static final Type TYPE = TypeToken.get(ServerInstallablePlugin.class).getType();
    public static final Type SET_TYPE = TypeToken.getParameterized(Set.class, ServerInstallablePlugin.class).getType();

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
