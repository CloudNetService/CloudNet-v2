package eu.cloudnetservice.v2.examples;

import eu.cloudnetservice.v2.api.CloudAPI;
import eu.cloudnetservice.v2.api.builders.ApiServerProcessBuilder;
import eu.cloudnetservice.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.v2.lib.server.ServerGroup;
import eu.cloudnetservice.v2.lib.server.SimpleServerGroup;

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

        ApiServerProcessBuilder.create("Lobby").startServer(); //start a simple game server

    }

}
