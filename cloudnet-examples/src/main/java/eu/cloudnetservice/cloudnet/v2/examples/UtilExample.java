/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.examples;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.builders.ApiServerProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.SimpleServerGroup;

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
