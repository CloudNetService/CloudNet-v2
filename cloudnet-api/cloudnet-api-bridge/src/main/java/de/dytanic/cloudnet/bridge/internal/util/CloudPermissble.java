/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.util;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

import java.util.UUID;

/**
 * Created by Tareko on 18.08.2017.
 */
public class CloudPermissble extends PermissibleBase {

    private UUID unqiueId;

    public CloudPermissble(Player player)
    {
        super(player);
        this.unqiueId = player.getUniqueId();

        player.setOp(false);
    }

    @Override
    public boolean isPermissionSet(String name)
    {
        return hasPermission(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm)
    {
        return hasPermission(perm.getName());
    }

    @Override
    public boolean hasPermission(Permission perm)
    {
        return hasPermission(perm.getName());
    }

    @Override
    public boolean hasPermission(String inName)
    {
        if(inName.equalsIgnoreCase("bukkit.broadcast.user")) return true;

        CloudPlayer cloudPlayer = CloudServer.getInstance().getCloudPlayers().get(this.unqiueId);
        if(cloudPlayer != null)
        return cloudPlayer.getPermissionEntity().hasPermission(CloudAPI.getInstance().getPermissionPool(), inName, CloudAPI.getInstance().getGroup());
        else
        return false;
    }

    @Override
    public boolean isOp()
    {
        return false;
    }

    public UUID getUnqiueId()
    {
        return unqiueId;
    }
}
