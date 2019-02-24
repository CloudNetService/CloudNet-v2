/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.util;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

/**
 * Created by Tareko on 18.08.2017.
 */
public class CloudPermissible extends PermissibleBase {

    private UUID uniqueId;

    private Map<String, PermissionAttachmentInfo> permissions = new HashMap<>();

    public CloudPermissible(Player player)
    {
        super(player);
        this.uniqueId = player.getUniqueId();

        player.setOp(false);
        recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions()
    {
        return new HashSet<>(permissions.values());
    }

    @Override
    public void recalculatePermissions() {
        this.permissions.clear();
        if (this.uniqueId == null) {
            return;
        }
        PermissionEntity permissionEntity = CloudServer.getInstance().getCloudPlayers().get(this.uniqueId).getPermissionEntity();
        final Map<String, Boolean> playerPermissions = permissionEntity.getPermissions();
        playerPermissions.forEach((key, value) -> {
            PermissionAttachmentInfo permissionAttachmentInfo = new PermissionAttachmentInfo(this, key, null, value);
            permissions.put(key, permissionAttachmentInfo);
        });
        permissionEntity.getGroups().stream()
                .filter(g -> g.getTimeout() > System.currentTimeMillis())
                .map(g -> CloudAPI.getInstance().getPermissionGroup(g.getGroup()))
                .forEach(g -> {
                    g.getPermissions().forEach((key, value) -> {
                        PermissionAttachmentInfo permissionAttachmentInfo = new PermissionAttachmentInfo(this, key, null, value);
                        permissions.put(key, permissionAttachmentInfo);
                    });
                });
    }

    @Override
    public boolean isPermissionSet(String name) {
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
        if (inName.equalsIgnoreCase("bukkit.broadcast.user")) return true;

        CloudPlayer cloudPlayer = CloudServer.getInstance().getCloudPlayers().get(this.uniqueId);
        if (cloudPlayer != null)
            return cloudPlayer.getPermissionEntity().hasPermission(CloudAPI.getInstance().getPermissionPool(), inName, CloudAPI.getInstance().getGroup());
        else
            return false;
    }

    @Override
    public boolean isOp()
    {
        return false;
    }

    public UUID getUniqueId()
    {
        return uniqueId;
    }
}
