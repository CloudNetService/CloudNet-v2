/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet;

import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.server.ServerState;

/**
 * Created by Tareko on 15.10.2017.
 */
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
