package de.dytanic.cloudnet.lib.server;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.process.ServerProcessData;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Tareko on 30.07.2017.
 */
public class ServerProcessMeta extends ServerProcessData {

    public static final Type TYPE = TypeToken.get(ServerProcessMeta.class).getType();

    private ServiceId serviceId;
    private int port;

    public ServerProcessMeta(final String wrapperName,
                             final String serverGroupName,
                             final int memory,
                             final ServerConfig serverConfig,
                             final Template template,
                             final List<String> javaProcessParameters,
                             final List<String> serverProcessParameters,
                             final String templateUrl,
                             final Set<ServerInstallablePlugin> plugins,
                             final Properties properties,
                             final ServiceId serviceId,
                             final int port) {
        super(wrapperName,
              serverGroupName,
              memory,
              serverConfig,
              template,
              javaProcessParameters,
              serverProcessParameters,
              templateUrl,
              plugins,
              properties);
        this.serviceId = serviceId;
        this.port = port;
    }

    public ServerProcessMeta(final ServerProcessData serverProcessData,
                             final ServiceId serviceId,
                             final int port) {
        super(serverProcessData);
        this.serviceId = serviceId;
        this.port = port;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public int getPort() {
        return port;
    }

}
