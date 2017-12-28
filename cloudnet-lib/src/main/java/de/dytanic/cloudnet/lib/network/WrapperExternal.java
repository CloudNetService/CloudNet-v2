package de.dytanic.cloudnet.lib.network;

import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.user.SimpledUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 30.07.2017.
 */
@Getter
@AllArgsConstructor
public class WrapperExternal {

    private CloudNetwork cloudNetwork;

    private SimpledUser user;

    private java.util.Map<String, ServerGroup> serverGroups;

    private java.util.Map<String, ProxyGroup> proxyGroups;

}