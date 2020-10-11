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

import eu.cloudnetservice.cloudnet.v2.bridge.CloudServer;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerState;

public class GameAPIExample {

    public void updateGameMode() {
        CloudServer.getInstance().setMotd("My Map"); //Motd will updated
        CloudServer.getInstance().setAllowAutoStart(false); //Set the allow to autostart for a new server disabling
        CloudServer.getInstance()
                   .setServerState(ServerState.INGAME); //The ServerState can changed to ingame without a new server auto startup
        CloudServer.getInstance().changeToIngame(); //The Ingame Change method sets the game ingame and hide from the signs and mobs
        CloudServer.getInstance().setMaxPlayers(166); //Sets the MaxPlayer count
        CloudServer.getInstance()
                   .update(); //Update the ServerInfo of CloudNet. Alternative is the setXXXXandUpdate(); method for the auto invokation
    }


}
