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

public class PermissionProvider
{
    /**
     *
     * Calculate the permission timeout for the given days.
     * @see java.util.concurrent.TimeUnit
     */
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");

    /**
     * Update the Player in the PlayerDatabase
     *
     * @param offlinePlayer
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */
    private static void updatePlayer(OfflinePlayer offlinePlayer)
    {
        CloudAPI.getInstance().updatePlayer(offlinePlayer);
    }

    /**
     * Updates the Permissiongroup in the Database.
     *
     * @param permissionGroup
     * @see de.dytanic.cloudnet.lib.player.permission.PermissionGroup
     */

    private static void updatePermissionGroup(PermissionGroup permissionGroup)
    {
        CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
    }

    /**
     * Calculate the Permission group time by the given int.
     *
     * @param value
     * @return Time Value for Permission Time
     */
    private static long calculateDays(int value) { return System.currentTimeMillis() + ((TimeUnit.DAYS.toMillis(value))); }

    /**
     * Gets the Display of the given player by his highest permission group.
     *
     * @param offlinePlayer
     * @return Player Group Display of the given player
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */
    public static String getDisplay(OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getDisplay();
    }

    /**
     * Gets the Display of the given uuid by the highest permission group.
     *
     * @param uuid
     * @return Player Group Display of the given UUID
     */
    public static String getDisplay(UUID uuid)
    {
        return CloudAPI.getInstance().getOfflinePlayer(uuid).getPermissionEntity().getHighestPermissionGroup(CloudAPI.
        getInstance().getPermissionPool()).getDisplay();
    }

    /**
     * Gets the Suffix of the given player by his highest permission group.
     *
     * @param offlinePlayer
     * @return Player Group Suffix of the given player
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static String getSuffix(OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getSuffix();
    }

    /**
     * Gets the Suffix of the given uuid by the highest permission group.
     *
     * @param uuid
     * @return Player group Suffix of the given UUID
     */

    public static String getSuffix(UUID uuid)
    {
        return CloudAPI.getInstance().getOfflinePlayer(uuid).getPermissionEntity().getHighestPermissionGroup(CloudAPI.
            getInstance().getPermissionPool()).getSuffix();
    }

    /**
     * Gets the Prefix of the given player by his highest permission group.
     *
     * @param offlinePlayer
     * @return Player Group Prefix of the given player
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static String getPrefix(OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getPrefix();
    }

    /**
     * Gets the Prefix of the given uuid by the highest permission group.
     *
     * @param uuid
     * @return Player Group Prefix of the given UUID
     */

    public static String getPrefix(UUID uuid)
    {
        return CloudAPI.getInstance().getOfflinePlayer(uuid).getPermissionEntity().getHighestPermissionGroup(CloudAPI.
                getInstance().getPermissionPool()).getPrefix();
    }

    /**
     * Gets the Highest Player permission group by the given player.
     *
     * @param offlinePlayer
     * @return Player Group Name of the given player
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static String getGroupName(OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName();
    }

    /**
     * Gets the Highest Player permission group by the given UUID.
     *
     * @param uuid
     * @return Player Group Name of the given UUID
     */

    public static String getGroupName(UUID uuid)
    {
        return CloudAPI.getInstance().getOfflinePlayer(uuid).getPermissionEntity().getHighestPermissionGroup(CloudAPI.
                getInstance().getPermissionPool()).getName();
    }

    /**
     * Gets the perfix of the given permission group.
     *
     * @param groupName
     * @return Group Prefix of the given group or null if the group doesn't exists
     */

    public static String getGroupPrefix(String groupName)
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
     * Gets the suffix of the given permission group.
     *
     * @param groupName
     * @return Group Suffix of the given group or null if the group doesn't exists
     */

    public static String getGroupSuffix(String groupName)
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
     * Checks if the player is in the specified permission group.
     *
     * @param groupName
     * @param offlinePlayer
     * @return Checks if player is in the specified group or if it´s false the player isn´t in the group
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static boolean isInGroup(String groupName, OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().isInGroup(groupName);
    }

    /**
     * Gets the permission group display by the given permission group.
     *
     * @param groupName
     * @return Group Display of the given group or null if the group doesn't exists
     */

    public static String getGroupDisplay(String groupName)
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
     * Gets the implemented permission groups of the specified player.
     *
     * @param offlinePlayer
     * @return List of implemented servergroups of the given player or null if the group doesn't exists
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static Collection<String> getImplementedPlayerGroups(OfflinePlayer offlinePlayer)
    {
        return Collections.unmodifiableCollection(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.
        getInstance().getPermissionPool()).getImplementGroups());
    }

    /**
     * Gets the implemented groups of the specified permission group.
     *
     * @param groupName
     * @return List of implemented servergroups of the given permissiongroup or null if the group doesn't exists
     */

    public static Collection<String> getImplementedGroups(String groupName)
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
     * Gets the join power of the given player by his highest permission group.
     *
     * @param player
     * @return Group Join Power of the given player
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static Integer getJoinPower(OfflinePlayer player)
    {
        return player.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getJoinPower();
    }

    /**
     * Gets the join power of the specified permission group.
     *
     * @param groupName
     * @return Group Join Power of the given permissiongroup or null if the group doesn't exists
     */

    public static Integer getGroupJoinPower(String groupName)
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
     * Gets the player permissions.
     *
     * @param offlinePlayer
     * @return Player Permissions of the given player
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static Map<String, Boolean> getPlayerPermissions(OfflinePlayer offlinePlayer)
    {
        return Collections.unmodifiableMap(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.
        getInstance().getPermissionPool()).getPermissions());
    }

    /**
     * Gets the Permissions of the given player by his highest permission group.
     *
     * @param offlinePlayer
     * @return Player Permissions on Servergroups of the given player
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static Map<String, Object> getPlayerServerPermissions(OfflinePlayer offlinePlayer)
    {
        return Collections.unmodifiableMap(offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.
        getInstance().getPermissionPool()).getOptions());
    }

    /**
     * Gets the TagID of the group by the given player.
     *
     * @param offlinePlayer
     * @return Player Group TagID of the given player
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static int getPlayerGroupTagID(OfflinePlayer offlinePlayer)
    {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getTagId();
    }

    /**
     * Gets the remaining Time of the player in the given group.
     *
     * @param offlinePlayer
     * @return Player Group RemaingTime of the given player or -1 if it´s lifetime
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static long getPlayerGroupRemainingTime(OfflinePlayer offlinePlayer)
    {
        for (GroupEntityData groupEntityData : offlinePlayer.getPermissionEntity().getGroups())
        {
            return groupEntityData.getTimeout();
        }
        return -1;
    }

    /**
     * Adds a permission to the given Player.
     *
     * @param offlinePlayer
     * @param permission
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static void addPlayerPermission(OfflinePlayer offlinePlayer, String permission)
    {
        offlinePlayer.getPermissionEntity().getPermissions().put(permission.replaceFirst("-", ""), !permission.startsWith("-"));
        updatePlayer(offlinePlayer);
    }

    /**
     * Removes a permission from the given player.
     *
     * @param offlinePlayer
     * @param permission
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static void removePlayerPermission(OfflinePlayer offlinePlayer, String permission)
    {
        offlinePlayer.getPermissionEntity().getPermissions().remove(permission);
        updatePlayer(offlinePlayer);
    }

    /**
     * Creates the a new permission group by the given name.
     *
     * @param permissiongroup
     * @return The new Permissiongroup if not successful null
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
     * Adds the given permission to the given permission group.
     *
     * @param permissiongroup
     * @param permission
     */

    public static void addPermission(String permissiongroup, String permission)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(permissiongroup))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(permissiongroup);
            permissionGroup.getPermissions().put(permission.replaceFirst("-", ""), !permission.startsWith("-"));
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Removes the given permission from the given permission vgroup.
     *
     * @param permissiongroup
     * @param permission
     */

    public static void removePermission(String permissiongroup, String permission)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(permissiongroup))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(permissiongroup);
            permissionGroup.getPermissions().remove(permission);
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Adds the given permission to the given permission group on the given server group.
     *
     * @param permissiongroup
     * @param permission
     * @param servergroup
     */

    public static void addServergroupPermission(String permissiongroup, String permission, String servergroup)
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
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Adds the given permission to the given group on the given server group.
     *
     * @param permissiongroup
     * @param permission
     * @param servergroup
     */

    public static void removeServerGroupPermission(String permissiongroup, String permission, String servergroup)
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
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets the given player permission group(s).
     *
     * @param offlinePlayer
     * @param group
     * @param timeindays
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static void setPlayerGroups(OfflinePlayer offlinePlayer, String group, Integer timeindays)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            offlinePlayer.getPermissionEntity().getGroups().clear();
            offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(group,
                    (timeindays.equals(-1) ? 0L : calculateDays(timeindays))));
            updatePlayer(offlinePlayer);
        }
    }

    /**
     * Adds the given player to the given permission group(s).
     *
     * @param player
     * @param group
     * @param timeindays
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static void addPlayerGroup(OfflinePlayer player, String group, Integer timeindays)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            player.getPermissionEntity().getGroups().add(new GroupEntityData(group,
                    (timeindays.equals(-1) ? 0L : calculateDays(timeindays))));
            updatePlayer(player);
        }
    }

    /**
     * Removes the given player from the given permission group.
     *
     * @param group
     * @param offlinePlayer
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static void removePlayerGroup(String group, OfflinePlayer offlinePlayer)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            GroupEntityData groupEntityData = null;
            for (GroupEntityData groupEntity : offlinePlayer.getPermissionEntity().getGroups())
            {
                if (groupEntity.getGroup().equalsIgnoreCase(group))
                {
                    groupEntityData = groupEntity;
                    break;
                }
            }
            if (groupEntityData != null)
            {
                offlinePlayer.getPermissionEntity().getGroups().remove(groupEntityData);
            }
            if (offlinePlayer.getPermissionEntity().getGroups().size() == 0)
            {
                offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(permissionPool.getDefaultGroup().getName(), 0));
            }
            updatePlayer(offlinePlayer);
        }
    }

    /**
     * Gets the given player permission groups.
     *
     * @param offlinePlayer
     * @return Player Groups of the given player as String or  null if the player doesn't exists
     * @see de.dytanic.cloudnet.lib.player.OfflinePlayer
     */

    public static String getPlayerGroups(OfflinePlayer offlinePlayer) {
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
     * Returns all server groups.
     *
     * @return Permission Groups or null
     */

    public static Collection<PermissionGroup> getGroups()
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        for (PermissionGroup permissionGroup : permissionPool.getGroups().values())
        {
            return Collections.unmodifiableCollection(permissionPool.getGroups().values());
        }
        return null;
    }

    /**
     * Returns the specified permission group.
     *
     * @param group
     * @return the specified permission group or null if the group doesn't exists
     */

    public static PermissionGroup getPermissionGroup(String group)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            return permissionPool.getGroups().get(group);
        }
        return null;
    }

    /**
     * Sets the display name by the given group.
     *
     * @param group
     * @param display
     */

    public static void setDisplay(String group, String display)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            permissionGroup.setDisplay(display.replace("_", " "));
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets the prefix name by the given group.
     *
     * @param group
     * @param prefix
     */

    public static void setPrefix(String group, String prefix)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            permissionGroup.setPrefix(prefix.replace("_", " "));
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets the suffix name by the given group.
     *
     * @param group
     * @param suffix
     */

    public static void setSuffix(String group, String suffix)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            permissionGroup.setSuffix(suffix.replace("_", " "));
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets the default permission group.
     *
     * @param group
     */

    public static void setDefaultGroup(String group)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            PermissionGroup olddefault = permissionPool.getDefaultGroup();
            olddefault.setDefaultGroup(false);
            permissionGroup.setDefaultGroup(true);
            updatePermissionGroup(olddefault);
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets the joinpower of the specified permission group.
     *
     * @param group
     * @param joinpower
     */

    public static void setJoinPower(String group, int joinpower)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            permissionGroup.setJoinPower(joinpower);
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets the TagID of the given permission group.
     *
     * @param group
     * @param TagID
     */

    public static void setTagID(String group, int TagID)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            permissionGroup.setTagId(TagID);
            updatePermissionGroup(permissionGroup);
        }
    }
}