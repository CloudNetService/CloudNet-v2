package de.dytanic.cloudnet.bridge.vault;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import net.milkbowl.vault.permission.Permission;

/**
 * Created by Tareko on 25.11.2017.
 */
public class VaultPermissionImpl extends Permission {

    @Override
    public String getName()
    {
        return "CloudNet-Permission";
    }

    @Override
    public boolean isEnabled()
    {
        return CloudAPI.getInstance().getPermissionPool() != null;
    }

    @Override
    public boolean hasSuperPermsCompat()
    {
        return true;
    }

    @Override
    public boolean playerHas(String s, String s1, String s2)
    {
        PermissionEntity permissionEntity = getPlayer(s1).getPermissionEntity();
        return permissionEntity.hasPermission(CloudAPI.getInstance().getPermissionPool(), s2, null);
    }

    @Override
    public boolean playerAdd(String s, String s1, String s2)
    {
        OfflinePlayer offlinePlayer = getPlayer(s1);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        permissionEntity.getPermissions().put(s2, true);
        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        return true;
    }

    @Override
    public boolean playerRemove(String s, String s1, String s2)
    {
        OfflinePlayer offlinePlayer = getPlayer(s1);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        permissionEntity.getPermissions().remove(s2);
        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        return true;
    }

    @Override
    public boolean groupHas(String s, String s1, String s2)
    {
        OfflinePlayer offlinePlayer = getPlayer(s1);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        return permissionEntity.isInGroup(s2);
    }

    @Override
    public boolean groupAdd(String s, String s1, String s2)
    {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(s1);
        permissionGroup.getPermissions().put(s2, true);
        CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        return true;
    }

    @Override
    public boolean groupRemove(String s, String s1, String s2)
    {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(s1);
        permissionGroup.getPermissions().remove(s2);
        CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        return true;
    }

    @Override
    public boolean playerInGroup(String s, String s1, String s2)
    {
        OfflinePlayer offlinePlayer = getPlayer(s1);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        return permissionEntity.isInGroup(s2);
    }

    @Override
    public boolean playerAddGroup(String s, String s1, String s2)
    {
        OfflinePlayer offlinePlayer = getPlayer(s1);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();

        GroupEntityData groupEntityData = CollectionWrapper.filter(permissionEntity.getGroups(), new Acceptable<GroupEntityData>() {
            @Override
            public boolean isAccepted(GroupEntityData groupEntityData)
            {
                return groupEntityData.getGroup().equalsIgnoreCase(s2);
            }
        });

        if(groupEntityData != null)
        {
            permissionEntity.getGroups().remove(groupEntityData);
            groupEntityData = new GroupEntityData(groupEntityData.getGroup(), 0);
        }
        else
        {
            groupEntityData = new GroupEntityData(groupEntityData.getGroup(), 0);
        }

        permissionEntity.getGroups().add(groupEntityData);
        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String s, String s1, String s2)
    {
        OfflinePlayer offlinePlayer = getPlayer(s1);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();

        GroupEntityData groupEntityData = CollectionWrapper.filter(permissionEntity.getGroups(), new Acceptable<GroupEntityData>() {
            @Override
            public boolean isAccepted(GroupEntityData groupEntityData)
            {
                return groupEntityData.getGroup().equalsIgnoreCase(s2);
            }
        });

        if(groupEntityData != null) permissionEntity.getGroups().remove(groupEntityData);
        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        return true;
    }

    @Override
    public String[] getPlayerGroups(String s, String s1)
    {
        PermissionEntity permissionEntity = getPlayer(s1).getPermissionEntity();
        String[] data = new String[permissionEntity.getGroups().size()];
        short i = 0;
        for(GroupEntityData groupEntityData : permissionEntity.getGroups())
        {
            data[i++] = groupEntityData.getGroup();
        }
        return data;
    }

    @Override
    public String getPrimaryGroup(String s, String s1)
    {
        return getPlayer(s1).getPermissionEntity().getHighestPermissionGroup(
                CloudAPI.getInstance().getPermissionPool()
        ).getName();
    }

    @Override
    public String[] getGroups()
    {
        String[] groups = new String[CloudAPI.getInstance().getPermissionPool().getGroups().size()];
        int i = 0;
        for(String group : CloudAPI.getInstance().getPermissionPool().getGroups().keySet()) groups[i++] = group;

        return groups;
    }

    @Override
    public boolean hasGroupSupport()
    {
        return true;
    }

    private void updatePlayer(OfflinePlayer offlinePlayer)
    {
        CloudAPI.getInstance().updatePlayer(offlinePlayer);
    }

    private OfflinePlayer getPlayer(String name)
    {
        OfflinePlayer offlinePlayer = CloudServer.getInstance().getCachedPlayer(name);

        if(offlinePlayer == null)
            offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(name);

        return offlinePlayer;
    }
    
}