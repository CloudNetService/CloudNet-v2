/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.SimpleServerGroup;

/**
 * Created by Tareko on 15.10.2017.
 */
public class UtilExample {

    public void toDo() {
        CloudAPI.getInstance().getOnlineCount(); //Returns the OnlineCount

        /*===========================*/
        ProxyGroup proxyGroup = CloudAPI.getInstance().getProxyGroupData("Bungee"); //Returns a proxygroup
        proxyGroup.getWrapper().add("Second-Wrapper");
        CloudAPI.getInstance().updateProxyGroup(proxyGroup); //update a proxy group

        ServerGroup serverGroup = CloudAPI.getInstance()
                                          .getServerGroup("Lobby"); //Returns from the CloudNet synchronized the server group objective
        serverGroup.setMaintenance(true);
        CloudAPI.getInstance().updateServerGroup(serverGroup); //update the server group

        SimpleServerGroup simpleServerGroup = CloudAPI.getInstance().getServerGroupData("Lobby"); //Returns a cached server group
        if (simpleServerGroup.isMaintenance()) {
            System.out.println("Is a maintenance group");
        }

        CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData("Lobby")); //start a simple game server

    }

}
