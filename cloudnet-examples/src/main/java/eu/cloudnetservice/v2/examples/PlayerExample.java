package eu.cloudnetservice.v2.examples;

import eu.cloudnetservice.v2.api.CloudAPI;
import eu.cloudnetservice.v2.api.player.PlayerExecutorBridge;
import eu.cloudnetservice.v2.lib.NetworkUtils;
import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.v2.lib.player.OfflinePlayer;
import eu.cloudnetservice.v2.lib.player.PlayerConnection;

import java.util.UUID;

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

        PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, "Hello world!"); //writes a message to the player if the player is online
        PlayerExecutorBridge.INSTANCE.sendPlayer(cloudPlayer, "Lobby-2"); //send a player to a some server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PlayerExecutorBridge.INSTANCE.kickPlayer(cloudPlayer,
                                                         "you are a cool guy for the network"); //Kick a player from the network
            }
        }).start();

        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer("Dytanic");
        if (offlinePlayer != null) //If the player is registered
        {

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
