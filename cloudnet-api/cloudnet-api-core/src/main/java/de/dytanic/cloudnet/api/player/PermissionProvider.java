package de.dytanic.cloudnet.api.player;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.DefaultPermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Permission Provider
 * Created by _Klaro on 25.06.2018.
 * Version 1.0
 * Copyright (c) 2018 _Klaro
 */

public class PermissionProvider
{
    /**
     *
     * Returns the SimpleDateFormat for the Permission Time
     */
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");

    /**
     *
     * @param offlinePlayer
     * Update the Player in the Database
     */
    private void updatePlayer(OfflinePlayer offlinePlayer)
    {
        CloudAPI.getInstance().updatePlayer(offlinePlayer);
    }

    /**
     *
     * @param permissionGroup
     * Updates the Permissiongroup in the Database
     */

    private void updatePermissionGroup(PermissionGroup permissionGroup)
    {
        CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
    }

    /**
     *
     * @param value
     * @return Time Value for Permission Time
     * Calculate the Days by the given int
     */
    private long calculateDays(int value) { return System.currentTimeMillis() + ((TimeUnit.DAYS.toMillis(value))); }

    /**
     *
     * @param offlinePlayer
     * @return Player Group Display of the given player
     * Gets the Display Name of the given player
     */
    public String getDisplay(OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getDisplay();
    }

    /**
     *
     * @param uuid
     * @return Player Group Display of the given UUID
     * Gets the Display Name of the group by the given UUID
     */
    public String getDisplay(UUID uuid)
    {
        return CloudAPI.getInstance().getOfflinePlayer(uuid).getPermissionEntity().getHighestPermissionGroup(CloudAPI.
        getInstance().getPermissionPool()).getDisplay();
    }

    /**
     *
     * @param offlinePlayer
     * @return Player Group Suffix of the given player
     * Gets the Suffix of the given player
     */

    public String getSuffix(OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getSuffix();
    }

    /**
     *
     * @param uuid
     * @return Player group Suffix of the given UUID
     * Gets the group suffix by the given UUID
     */

    public String getSuffix(UUID uuid)
    {
        return CloudAPI.getInstance().getOfflinePlayer(uuid).getPermissionEntity().getHighestPermissionGroup(CloudAPI.
            getInstance().getPermissionPool()).getSuffix();
    }

    /**
     *
     * @param offlinePlayer
     * @return Player Group Prefix of the given player
     * Gets the prefix of the group by the given player
     */

    public String getPrefix(OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getPrefix();
    }

    /**
     *
     * @param uuid
     * @return Player Group Prefix of the given UUID
     * Gets the prefix of the group by the given UUID
     */

    public String getPrefix(UUID uuid)
    {
        return CloudAPI.getInstance().getOfflinePlayer(uuid).getPermissionEntity().getHighestPermissionGroup(CloudAPI.
                getInstance().getPermissionPool()).getPrefix();
    }

    /**
     *
     * @param offlinePlayer
     * @return Player Group Name of the given player
     * Gets the Highest Player group by the given player
     */

    public String getGroupName(OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName();
    }

    /**
     *
     * @param uuid
     * @return Player Group Name of the given UUID
     * Gets the Highest Player group by the given UUID
     */

    public String getGroupName(UUID uuid)
    {
        return CloudAPI.getInstance().getOfflinePlayer(uuid).getPermissionEntity().getHighestPermissionGroup(CloudAPI.
                getInstance().getPermissionPool()).getName();
    }

    /**
     *
     * @param groupName
     * @return Group Prefix of the given group or null if the group doesn't exists
     * Gets the perfix of the given group
     */

    public String getGroupPrefix(String groupName)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(groupName);
            return permissionGroup.getPrefix();
        }
        return null;
    }

    /**
     *
     * @param groupName
     * @return Group Suffix of the given group or null if the group doesn't exists
     * Gets the suffix of the given group
     */

    public String getGroupSuffix(String groupName)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(groupName);
            return permissionGroup.getSuffix();
        }
        return null;
    }

    /**
     *
     * @param groupName
     * @param offlinePlayer
     * @return Checks if player is in the specified group or if it´s false the player isn´t in the group
     * Checks if the player is in the specified permission group
     */

    public boolean isInGroup(String groupName, OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().isInGroup(groupName);
    }

    /**
     *
     * @param groupName
     * @return Group Display of the given group or null if the group doesn't exists
     * Gets the permission group display by the given groupname
     */

    public String getGroupDisplay(String groupName)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(groupName);
            return permissionGroup.getDisplay();
        }
        return null;
    }

    /**
     *
     * @param offlinePlayer
     * @return List of implemented servergroups of the given player or null if the group doesn't exists
     * Gets the implemented permission groups of the specified player
     */

    public Collection<String> getImplementedPlayerGroups(OfflinePlayer offlinePlayer)
    {
        return Collections.unmodifiableCollection(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.
        getInstance().getPermissionPool()).getImplementGroups());
    }

    /**
     *
     * @param groupName
     * @return List of implemented servergroups of the given permissiongroup or null if the group doesn't exists
     * Gets the implemented groups of the specified permission group
     */

    public Collection<String> getImplementedGroups(String groupName)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(groupName);
            return Collections.unmodifiableCollection(permissionGroup.getImplementGroups());
        }
        return null;
    }

    /**
     *
     * @param player
     * @return Group Join Power of the given player
     * Gets the join power of the given player by his highest permission group
     */

    public Integer getJoinPower(OfflinePlayer player)
    {
        return player.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getJoinPower();
    }

    /**
     *
     * @param groupName
     * @return Group Join Power of the given permissiongroup or null if the group doesn't exists
     * Gets the join power of the specified group
     */

    public Integer getGroupJoinPower(String groupName)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(groupName);
            return permissionGroup.getJoinPower();
        }
        return null;
    }

    /**
     *
     * @param offlinePlayer
     * @return Player Permissions of the given player
     * Gets the player permissions
     */

    public Map<String, Boolean> getPlayerPermissions(OfflinePlayer offlinePlayer)
    {
        return Collections.unmodifiableMap(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.
        getInstance().getPermissionPool()).getPermissions());
    }

    /**
     *
     * @param offlinePlayer
     * @return Player Permissions on Servergroups of the given player
     * Gets the Permissions of the server groups
     */

    public Map<String, Object> getPlayerServerPermissions(OfflinePlayer offlinePlayer)
    {
        return Collections.unmodifiableMap(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.
        getInstance().getPermissionPool()).getOptions());
    }

    /**
     *
     * @param offlinePlayer
     * @return Player Group TagID of the given player
     * Gets the TagID of the group by the given player
     */

    public int getPlayerGroupTagID(OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getTagId();
    }

    /**
     *
     * @param offlinePlayer
     * @return Player Group RemaingTime of the given player or -1 if it´s lifetime
     * Gets the remaining Time of the player in the given group
     */

    public long getPlayerGroupRemainingTime(OfflinePlayer offlinePlayer)
    {
        for (GroupEntityData groupEntityData : offlinePlayer.getPermissionEntity().getGroups())
        {
            return groupEntityData.getTimeout();
        }
        return -1;
    }

    /**
     *
     * @param offlinePlayer
     * @param permission
     * Adds a permission to the given Player
     */

    public void addPlayerPermission(OfflinePlayer offlinePlayer, String permission)
    {
        offlinePlayer.getPermissionEntity().getPermissions().put(permission.replaceFirst("-", ""), !permission.startsWith("-"));
        this.updatePlayer(offlinePlayer);
    }

    /**
     *
     * @param offlinePlayer
     * @param permission
     * Removes a permission from the given player
     */

    public void removePlayerPermission(OfflinePlayer offlinePlayer, String permission)
    {
        offlinePlayer.getPermissionEntity().getPermissions().remove(permission);
        this.updatePlayer(offlinePlayer);
    }

    /**
     *
     * @param permissiongroup
     * @return The new Permissiongroup if not successful null
     * Creates the a new permission group by the given name
     */

    public PermissionGroup createPermissionGroup(String permissiongroup)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (!permissionPool.getGroups().containsKey(permissiongroup))
        {
            PermissionGroup permissionGroup = new DefaultPermissionGroup(permissiongroup);
            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
            return permissionGroup;
        }
        return null;
    }

    /**
     *
     * @param permissiongroup
     * @param permission
     * Adds the given permission to the given permissiongroup
     */

    public void addPermission(String permissiongroup, String permission)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(permissiongroup))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(permissiongroup);
            permissionGroup.getPermissions().put(permission.replaceFirst("-", ""), !permission.startsWith("-"));
            this.updatePermissionGroup(permissionGroup);
        }
    }

    /**
     *
     * @param permissiongroup
     * @param permission
     * Removes the given permission from the given permissiongroup
     */

    public void removePermission(String permissiongroup, String permission)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(permissiongroup))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(permissiongroup);
            permissionGroup.getPermissions().remove(permission);
            this.updatePermissionGroup(permissionGroup);
        }
    }

    /**
     *
     * @param permissiongroup
     * @param permission
     * @param servergroup
     */

    public void addServergroupPermission(String permissiongroup, String permission, String servergroup)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(permissiongroup))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(permissiongroup);
            if (!permissionGroup.getServerGroupPermissions().containsKey(permission))
            {
                permissionGroup.getServerGroupPermissions().put(permission, new ArrayList<>());
            }
            permissionGroup.getServerGroupPermissions().get(servergroup).add(permission.replaceFirst("-", ""));
            this.updatePermissionGroup(permissionGroup);
        }
    }

    /**
     *
     * @param permissiongroup
     * @param permission
     * @param servergroup
     * Adds the given permission to the given group on the given servergroup
     */

    public void removeServerGroupPermission(String permissiongroup, String permission, String servergroup)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(permissiongroup))
        {
            PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(permissiongroup);
            if (!permissionGroup.getServerGroupPermissions().containsKey(permission))
            {
                permissionGroup.getServerGroupPermissions().put(permission, new ArrayList<>());
            }
            permissionGroup.getServerGroupPermissions().get(servergroup).remove(permission.replaceFirst("-", ""));
            this.updatePermissionGroup(permissionGroup);
        }
    }

    /**
     *
     * @param offlinePlayer
     * @param group
     * @param timeindays
     * Sets the given player permissiongroup(s)
     */

    public void setPlayerGroups(OfflinePlayer offlinePlayer, String group, Integer timeindays)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            offlinePlayer.getPermissionEntity().getGroups().clear();
            offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(group,
                    (timeindays.equals(-1) ? 0L : calculateDays(timeindays))));
            this.updatePlayer(offlinePlayer);
        }
    }

    /**
     *
     * @param player
     * @param group
     * @param timeindays
     * Adds the given player to the given permissiongroup(s)
     */

    public void addPlayerGroup(OfflinePlayer player, String group, Integer timeindays)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            player.getPermissionEntity().getGroups().add(new GroupEntityData(group,
                    (timeindays.equals(-1) ? 0L : calculateDays(timeindays))));
            this.updatePlayer(player);
        }
    }

    /**
     *
     * @param group
     * @param player
     * Removes the given player from the given permissiongroup
     */

    public void removePlayerGroup(String group, OfflinePlayer player)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            GroupEntityData groupEntityData = null;
            for (GroupEntityData groupEntity : player.getPermissionEntity().getGroups())
            {
                if (groupEntity.getGroup().equalsIgnoreCase(group))
                {
                    groupEntityData = groupEntity;
                    break;
                }
            }
            if (groupEntityData != null)
            {
                player.getPermissionEntity().getGroups().remove(groupEntityData);
            }
            if (player.getPermissionEntity().getGroups().size() == 0)
            {
                player.getPermissionEntity().getGroups().add(new GroupEntityData(permissionPool.getDefaultGroup().getName(), 0));
            }
            this.updatePlayer(player);
        }
    }

    /**
     *
     * @param offlinePlayer
     * @return Player Groups of the given player as String or  null if the player doesn't exists
     * Gets the given player permission groups
     */

    public String getPlayerGroups(OfflinePlayer offlinePlayer) {
        if (offlinePlayer != null && offlinePlayer.getPermissionEntity() != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (GroupEntityData groupEntityData : offlinePlayer.getPermissionEntity().getGroups()) {
                stringBuilder.append(groupEntityData.getGroup() + "@" + (groupEntityData.getTimeout() == 0 ||
                        groupEntityData.getTimeout() == -1 ? "LIFETIME" : simpleDateFormat.format(groupEntityData.getTimeout()))
                        + "");
            }
            return stringBuilder.substring(0);
        }
        return null;
    }

    /**
     *
     * @return Permission Groups or null
     * Returns all servergroups
     */

    public Collection<PermissionGroup> getGroups()
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        for (PermissionGroup permissionGroup : permissionPool.getGroups().values())
        {
            return Collections.unmodifiableCollection(permissionPool.getGroups().values());
        }
        return null;
    }

    /**
     *
     * @param group
     * @return the specified permissongroup or null if the group doesn't exists
     * Returns the specified Permission group
     */

    public PermissionGroup getPermissionGroup(String group)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            return permissionPool.getGroups().get(group);
        }
        return null;
    }

    /**
     *
     * @param group
     * @param display
     * Sets the display name by the given group
     */

    public void setDisplay(String group, String display)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            permissionGroup.setDisplay(display.replace("_", " "));
            this.updatePermissionGroup(permissionGroup);
        }
    }

    /**
     *
     * @param group
     * @param prefix
     * Sets the prefix name by the given group
     */

    public void setPrefix(String group, String prefix)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            permissionGroup.setPrefix(prefix.replace("_", " "));
            this.updatePermissionGroup(permissionGroup);
        }
    }

    /**
     *
     * @param group
     * @param suffix
     * Sets the suffix name by the given group
     */

    public void setSuffix(String group, String suffix)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            permissionGroup.setSuffix(suffix.replace("_", " "));
            this.updatePermissionGroup(permissionGroup);
        }
    }

    /**
     *
     * @param group
     * Sets the default permission group
     */

    public void setDefaultGroup(String group)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            PermissionGroup olddefault = permissionPool.getDefaultGroup();
            olddefault.setDefaultGroup(false);
            permissionGroup.setDefaultGroup(true);
            this.updatePermissionGroup(olddefault);
            this.updatePermissionGroup(permissionGroup);
        }
    }

    /**
     *
     * @param group
     * @param joinpower
     * Sets the joinpower of the specified permissiongroup
     */

    public void setJoinPower(String group, int joinpower)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            permissionGroup.setJoinPower(joinpower);
            this.updatePermissionGroup(permissionGroup);
        }
    }

    /**
     *
     * @param group
     * @param TagID
     * Sets the TagID of the given permissiongroup
     */

    public void setTagID(String group, int TagID)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            permissionGroup.setTagId(TagID);
            this.updatePermissionGroup(permissionGroup);
        }
    }
}