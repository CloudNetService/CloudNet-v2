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
 * Created by _Klaro on 25.06.2018.
 */

public class PermissionProvider
{
    /*------------------------*/
    /**
     *
     * Returns the SimpleDateFormat for the Permission Time (Do NOT EDIT this!)
     */
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
    private boolean debug;
    /*------------------------*/

    /*------------------------*/

    /**
     *
     * @param player
     * @return HigehstPlayerPermissionGroup by the given player
     */
    private PermissionGroup getPlayerGroup(String player)
    {
        this.sendDebug("A Player PermissionGroup Request was send!");
        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
    }

    /**
     *
     * @param uuid
     * @return HigehstPlayerPermissionGroup by the given UUID
     */
    private PermissionGroup getPlayerGroup(UUID uuid)
    {
        this.sendDebug("A Player PermissionGroup Request was send!");
        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(uuid);
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
    }

    /**
     *
     * @param offlinePlayer
     */
    private void updatePlayer(OfflinePlayer offlinePlayer)
    {
        this.sendDebug("A Player has been updated!");
        CloudAPI.getInstance().updatePlayer(offlinePlayer);
    }

    /**
     *
     * @param debug
     */

    public void setDebug(boolean debug) { this.debug = debug; }

    /**
     *
     * @return if the debug is enabled if not it´s false
     */

    public boolean isDebug() { return this.debug; }

    /**
     *
     * @param permissionGroup
     */
    private void updatePermissionGroup(PermissionGroup permissionGroup)
    {
        this.sendDebug("A Permissiongroup has been updated!");
        CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
    }

    /**
     *
     * @param value
     * @return Time Value for Permission Time
     */
    private long claculateDays(int value) { return (System.currentTimeMillis() + ((TimeUnit.DAYS.toMillis(value)))); }
    /*------------------------*/

    /**
     *
     * @param player
     * @return Player Group Display of the given player
     */
    public String getDisplay(String player)
    {
        return this.getPlayerGroup(player).getDisplay();
    }

    /**
     *
     * @param uuid
     * @return Player Group Display of the given UUID
     */
    public String getDisplay(UUID uuid)
    {
        return this.getPlayerGroup(uuid).getDisplay();
    }

    /**
     *
     * @param player
     * @return Player Group Suffix of the given player
     */

    public String getSuffix(String player)
    {
        return this.getPlayerGroup(player).getSuffix();
    }

    /**
     *
     * @param uuid
     * @return Player group Suffix of the given UUID
     */

    public String getSuffix(UUID uuid)
    {
        return this.getPlayerGroup(uuid).getSuffix();
    }

    /**
     *
     * @param player
     * @return Player Group Prefix of the given player
     */

    public String getPrefix(String player)
    {
        return this.getPlayerGroup(player).getPrefix();
    }

    /**
     *
     * @param uuid
     * @return Player Group Prefix of the given UUID
     */

    public String getPrefix(UUID uuid)
    {
        return this.getPlayerGroup(uuid).getPrefix();
    }

    /**
     *
     * @param player
     * @return Player Group Name of the given player
     */

    public String getGroupName(String player)
    {
        return this.getPlayerGroup(player).getName();
    }

    /**
     *
     * @param uuid
     * @return Player Group Name of the given UUID
     */

    public String getGroupName(UUID uuid)
    {
        return this.getPlayerGroup(uuid).getName();
    }

    /**
     *
     * @param group
     * @return Group Prefix of the given group or null if the group doesn't exists
     */

    public String getGroupPrefix(String group)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            return permissionGroup.getPrefix();
        }
        return null;
    }

    /**
     *
     * @param message
     */
    public void sendDebug(String message)
    {
        if (this.isDebug()) { CloudAPI.getInstance().dispatchConsoleMessage("[PermissionProvider] [DEBUG] | " + message); }
    }

    /**
     *
     * @param group
     * @return Group Suffix of the given group or null if the group doesn't exists
     */

    public String getGroupSuffix(String group)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            return permissionGroup.getSuffix();
        }
        return null;
    }

    /**
     *
     * @param group
     * @param player
     * @return Checks if player is in the specified group or if it´s false the player isn´t in the group
     */

    public boolean isInGroup(String group, String player)
    {
        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
        return offlinePlayer.getPermissionEntity().isInGroup(group);
    }

    /**
     *
     * @param group
     * @return Group Display of the given group or null if the group doesn't exists
     */

    public String getGroupDisplay(String group)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            return permissionGroup.getDisplay();
        }
        return null;
    }

    /**
     *
     * @param player
     * @return List of implemented servergroups of the given player or null if the group doesn't exists
     */

    public Collection<String> getImplementedGroups(String player)
    {
        return Collections.unmodifiableCollection(this.getPlayerGroup(player).getImplementGroups());
    }

    /**
     *
     * @param group
     * @return List of implemented servergroups of the given permissiongroup or null if the group doesn't exists
     */

    public Collection<String> getImplementedGroup(String group)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            return Collections.unmodifiableCollection(permissionGroup.getImplementGroups());
        }
        return null;
    }

    /**
     *
     * @param player
     * @return Group Join Power of the given player
     */

    public Integer getJoinPower(String player)
    {
        return this.getPlayerGroup(player).getJoinPower();
    }

    /**
     *
     * @param group
     * @return Group Join Power of the given permissiongroup or null if the group doesn't exists
     */

    public Integer getGroupJoinPower(String group)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(group);
            return permissionGroup.getJoinPower();
        }
        return null;
    }

    /**
     *
     * @param player
     * @return Player Permissions of the given player
     */

    public Map<String, Boolean> getPlayerPermissions(String player)
    {
        return Collections.unmodifiableMap(this.getPlayerGroup(player).getPermissions());
    }

    /**
     *
     * @param player
     * @return Player Permissions on Servergroups of the given player
     */

    public Map<String, Object> getPlayerServerPermissions(String player)
    {
        return Collections.unmodifiableMap(this.getPlayerGroup(player).getOptions());
    }

    /**
     *
     * @param player
     * @return Player Group TagID of the given player
     */

    public Integer getPlayerGroupTagID(String player)
    {
        return this.getPlayerGroup(player).getTagId();
    }

    /**
     *
     * @param player
     * @return Player Group RemaingTime of the given player or -1 if it´s lifetime
     */

    public long getPlayerGroupRemainingTime(String player)
    {
        for (GroupEntityData groupEntityData : CloudAPI.getInstance().getOfflinePlayer(player).getPermissionEntity().getGroups())
        {
            return groupEntityData.getTimeout();
        }
        return -1;
    }

    /**
     *
     * @param player
     * @param permission
     */

    public void addPermissiontoPlayer(String player, String permission)
    {
        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
        offlinePlayer.getPermissionEntity().getPermissions().put(permission.replaceFirst("-", ""), !permission.startsWith("-"));
        this.updatePlayer(offlinePlayer);
    }

    /**
     *
     * @param player
     * @param permission
     */

    public void removePermissionfromPlayer(String player, String permission)
    {
        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
        offlinePlayer.getPermissionEntity().getPermissions().remove(permission);
        this.updatePlayer(offlinePlayer);
    }

    /**
     *
     * @param permissiongroup
     */

    public void createPermissionGroup(String permissiongroup)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (!permissionPool.getGroups().containsKey(permissiongroup))
        {
            PermissionGroup permissionGroup = new DefaultPermissionGroup(permissiongroup);
            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        }
    }

    /**
     *
     * @param permissiongroup
     * @param permission
     */

    public void addPermissiontoPermissionGroup(String permissiongroup, String permission)
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
     */

    public void removePermissionfromPermissionGroup(String permissiongroup, String permission)
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

    public void addPermissiontoPermissionGrouponServergroup(String permissiongroup, String permission, String servergroup)
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
     */

    public void removePermissionfromPermissionGrouponServergroup(String permissiongroup, String permission, String servergroup)
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
     * @param player
     * @param group
     * @param timeindays
     */

    public void setPlayerGroups(String player, String group, Integer timeindays)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
            offlinePlayer.getPermissionEntity().getGroups().clear();
            offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(group,
                    (timeindays.equals(-1) ? 0L : claculateDays(timeindays))));
            this.updatePlayer(offlinePlayer);
        }
    }

    /**
     *
     * @param player
     * @param group
     * @param timeindays
     */

    public void addPlayertoGroup(String player, String group, Integer timeindays)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
            offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(group,
                    (timeindays.equals(-1) ? 0L : claculateDays(timeindays))));
            this.updatePlayer(offlinePlayer);
        }
    }

    /**
     *
     * @param group
     * @param player
     */

    public void removePlayerfromGroup(String group, String player)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(group))
        {
            OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
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
            this.updatePlayer(offlinePlayer);
        }
    }

    /**
     *
     * @param player
     * @return Player Groups of the given player as String or  null if the player doesn't exists
     */

    public String getPlayerGroups(String player)
    {
        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
        if (offlinePlayer.getUniqueId() != null)
        {
            if (offlinePlayer != null && offlinePlayer.getPermissionEntity() != null)
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (GroupEntityData groupEntityData : offlinePlayer.getPermissionEntity().getGroups()) {
                    stringBuilder.append(groupEntityData.getGroup() + "@" + (groupEntityData.getTimeout() == 0 ||
                            groupEntityData.getTimeout() == -1 ? "LIFETIME" : simpleDateFormat.format(groupEntityData.getTimeout()))
                            + "");
                }
                return stringBuilder.substring(0);
            }
        }
        return null;
    }

    /**
     *
     * @return Permission Groups or null
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
     * @return a specified permissongroup
     */

    public PermissionGroup getPermissionGroup(String group)
    {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        return permissionPool.getGroups().get(group);
    }

    /**
     *
     * @param group
     * @param display
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
     */

    public void setJoinPower(String group, Integer joinpower)
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
     */

    public void setTagID(String group, Integer TagID)
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