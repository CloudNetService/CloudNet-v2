/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import org.bukkit.Bukkit;

/**
 * Created by Tareko on 15.10.2017.
 */
public class PermissionsExample {

    public void setTags() {
        CloudServer.getInstance()
                   .updateNameTags(Bukkit.getPlayer("Dytanic")); //Sets the NameTags for all Players for one player. Use this method, if the scoreboard is already set
    }

    public void handlePermissions() {
        PermissionPool permissionPool = CloudAPI.getInstance()
                                                .getPermissionPool(); //Returns the permission pool with all permissions groups etc.
        PermissionGroup permissionGroup = new Boolean(true) ? CloudAPI.getInstance()
                                                                      .getPermissionGroup("Admin") : permissionPool.getGroups()
                                                                                                                   .get("Admin"); //Options for got the permission group
        permissionGroup.getOptions(); //custom api options
        permissionGroup.getJoinPower(); //returns the joinpower
        permissionGroup.getPermissions(); //a map with all permissions of the group
        //... getXXX();

        CloudAPI.getInstance().updatePermissionGroup(permissionGroup); //Updates the permission group
    }

}
