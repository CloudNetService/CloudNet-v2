/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.player.PlayerExecutor;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 15.10.2017.
 */
public class PlayerExample {

    public void examplePlayer() {
        CloudPlayer cloudPlayer = CloudAPI.getInstance()
                                          .getOnlinePlayer(UUID.fromString("e71d69dd-058f-4319-9ae9-8c8f0a7a61f5")); //Returns the CloudPlayer if the play is online or null if isn't online

        {
            PlayerConnection playerConnection = cloudPlayer.getPlayerConnection(); //Returns the online player connection metadata
            playerConnection.getHost(); //Returns the IP
            playerConnection.getPort(); //Returns the port of the last connection
            playerConnection.getUniqueId(); //Returns the UUID of the connection
            playerConnection.getVersion(); //Returns the legacy version
            playerConnection.isOnlineMode(); //Returns if the player was in onlinemode
        }

        System.out.println("The player " + cloudPlayer.getName() + " is on " + cloudPlayer.getProxy() + NetworkUtils.SLASH_STRING + cloudPlayer
            .getServer() + " and connected at " + cloudPlayer.getLoginTimeStamp().getTime());

        PlayerExecutor playerExecutor = cloudPlayer.getPlayerExecutor(); //Returns a util Class for some network methods
        playerExecutor.sendMessage(cloudPlayer, "Hello world!"); //writes a message to the player if the player is online
        playerExecutor.sendPlayer(cloudPlayer, "Lobby-2"); //send a player to a some server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                playerExecutor.kickPlayer(cloudPlayer, "you are a cool guy for the network"); //Kick a player from the network
            }
        }).start();

        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer("Dytanic");
        if (offlinePlayer != null) //If the player is registered
        {
            PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity(); //Returns the permissionentity for manage some permission systems
            permissionEntity.getGroups().add(new GroupEntityData("VIP",
                                                                 System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30))); //add a permission group with the delay of 30 days
            permissionEntity.getPermissions().put("minecraft.command.tp", true); //add a permission for this player

            if (permissionEntity.isInGroup("VIP")) {
                System.out.println("The player " + offlinePlayer.getUniqueId() + NetworkUtils.SLASH_STRING + offlinePlayer.getName() + " is in the group VIP");
            }

            PlayerConnection playerConnection = offlinePlayer.getLastPlayerConnection();
            playerConnection.getHost(); //Returns the IP
            playerConnection.getPort(); //Returns the port of the last connection
            playerConnection.getUniqueId(); //Returns the UUID of the connection
            playerConnection.getVersion(); //Returns the legacy version
            playerConnection.isOnlineMode(); //Returns if the player was in onlinemode

        }

        //update the player
        CloudAPI.getInstance().updatePlayer(cloudPlayer); //cloudplayer update
        CloudAPI.getInstance().updatePlayer(offlinePlayer); //update the offline player
    }
}
