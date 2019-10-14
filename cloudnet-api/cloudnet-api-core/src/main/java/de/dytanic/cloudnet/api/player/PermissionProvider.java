package de.dytanic.cloudnet.api.player;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class PermissionProvider {
    /**
     * The date format for print statements regarding dates.
     */
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");

    /**
     * Calculates the permission group timeout for the given days in future.
     *
     * @param value the amount of days
     *
     * @return timeout value for use with {@link PermissionGroup}
     */
    public static long calculateDays(int value) {
        return System.currentTimeMillis() + ((TimeUnit.DAYS.toMillis(value)));
    }

    /**
     * Gets the display for the given player by their highest permission group.
     *
     * @param offlinePlayer the player to get the display for
     *
     * @return the display for the given player
     *
     * @see PermissionGroup
     * @see #getDisplay(UUID)
     */
    public static String getDisplay(OfflinePlayer offlinePlayer) {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getDisplay();
    }

    /**
     * Gets the display of the player, identified by the given UUID
     * by their highest permission group.
     *
     * @param uuid the UUID of the player
     *
     * @return the display for the given player
     *
     * @see PermissionGroup
     * @see #getDisplay(OfflinePlayer)
     */
    public static String getDisplay(UUID uuid) {
        return CloudAPI.getInstance()
                       .getOfflinePlayer(uuid)
                       .getPermissionEntity()
                       .getHighestPermissionGroup(CloudAPI.
                                                              getInstance()
                                                          .getPermissionPool())
                       .getDisplay();
    }

    /**
     * Gets the suffix of the given player by their highest permission group.
     *
     * @param offlinePlayer the player to get the suffix for
     *
     * @return the suffix for the given player
     *
     * @see PermissionGroup
     * @see #getSuffix(UUID)
     */
    public static String getSuffix(OfflinePlayer offlinePlayer) {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getSuffix();
    }

    /**
     * Gets the suffix of the given player identified by their UUID
     * by their highest permission group.
     *
     * @param uuid the UUID of the player
     *
     * @return the suffix for the given player
     *
     * @see PermissionGroup
     * @see #getSuffix(OfflinePlayer)
     */
    public static String getSuffix(UUID uuid) {
        return CloudAPI.getInstance()
                       .getOfflinePlayer(uuid)
                       .getPermissionEntity()
                       .getHighestPermissionGroup(CloudAPI.
                                                              getInstance()
                                                          .getPermissionPool())
                       .getSuffix();
    }

    /**
     * Gets the prefix of the given player by their highest permission group.
     *
     * @param offlinePlayer the player to get the prefix for
     *
     * @return the prefix for the given player
     *
     * @see PermissionGroup
     * @see #getPrefix(UUID)
     */
    public static String getPrefix(OfflinePlayer offlinePlayer) {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getPrefix();
    }

    /**
     * Gets the prefix of the given player identified by their UUID
     * by their highest permission group.
     *
     * @param uuid the UUID of the player
     *
     * @return the prefix for the given player
     *
     * @see PermissionGroup
     * @see #getPrefix(OfflinePlayer)
     */
    public static String getPrefix(UUID uuid) {
        return CloudAPI.getInstance()
                       .getOfflinePlayer(uuid)
                       .getPermissionEntity()
                       .getHighestPermissionGroup(CloudAPI.
                                                              getInstance()
                                                          .getPermissionPool())
                       .getPrefix();
    }

    /**
     * Gets the name of the highest permission group for the given player
     * identified by their UUID.
     *
     * @param uuid the UUID of the player
     *
     * @return the name of the highest permission group of the given player
     *
     * @see #getGroupName(OfflinePlayer)
     * @see PermissionGroup
     * @see PermissionEntity
     */
    public static String getGroupName(UUID uuid) {
        return CloudAPI.getInstance()
                       .getOfflinePlayer(uuid)
                       .getPermissionEntity()
                       .getHighestPermissionGroup(CloudAPI.
                                                              getInstance()
                                                          .getPermissionPool())
                       .getName();
    }

    /**
     * Gets the prefix of the given permission group.
     *
     * @param groupName the name of the permission group
     *
     * @return the group prefix of the given group or {@code null},
     * if the group doesn't exist.
     *
     * @see PermissionGroup
     * @see PermissionPool
     */
    public static String getGroupPrefix(String groupName) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            return permissionPool.getGroups().get(groupName).getPrefix();
        }

        return null;
    }

    /**
     * Gets the suffix of the given permission group.
     *
     * @param groupName the name of the group
     *
     * @return the group suffix of the given group or {@code null},
     * if the group doesn't exist.
     *
     * @see PermissionGroup
     * @see PermissionPool
     */
    public static String getGroupSuffix(String groupName) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            return permissionPool.getGroups().get(groupName).getSuffix();
        }

        return null;
    }

    /**
     * Checks if the player is in the specified permission group.
     *
     * @param groupName     the name of the group
     * @param offlinePlayer the player to check for group membership
     *
     * @return whether or not the player is in the requested group
     *
     * @see PermissionEntity
     */
    public static boolean isInGroup(String groupName, OfflinePlayer offlinePlayer) {
        return offlinePlayer.getPermissionEntity().isInGroup(groupName);
    }

    /**
     * Gets the permission group display for the given permission group name.
     *
     * @param groupName the name of the group
     *
     * @return the group display of the given group or null if the group doesn't exist
     *
     * @see PermissionGroup
     */
    public static String getGroupDisplay(String groupName) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            return permissionPool.getGroups().get(groupName).getDisplay();
        }

        return null;
    }

    /**
     * Gets the implemented permission groups of the specified permission group.
     *
     * @param groupName the permission group
     *
     * @return Collection of implemented permission groups of the given
     * permission group or null, if the group doesn't exist.
     *
     * @see PermissionGroup
     */
    public static Collection<String> getImplementedGroups(String groupName) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            return Collections.unmodifiableCollection(permissionPool.getGroups().get(groupName).getImplementGroups());
        }

        return null;
    }

    /**
     * Gets the highest join power of the given player.
     *
     * @param player the player
     *
     * @return the highest join power
     *
     * @see PermissionGroup
     */
    public static int getJoinPower(OfflinePlayer player) {
        PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        return player.getPermissionEntity().getGroups().stream().mapToInt(groupEntityData -> permissionPool.getGroups()
                                                                                                           .get(groupEntityData.getGroup())
                                                                                                           .getJoinPower()).max().orElse(0);
    }

    /**
     * Gets the join power of the specified permission group.
     *
     * @param groupName the name of the group
     *
     * @return the group join power of the given permission group or
     * null, if the group doesn't exist.
     */
    public static Integer getGroupJoinPower(String groupName) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            return permissionPool.getGroups().get(groupName).getJoinPower();
        }

        return null;
    }

    /**
     * Gets the TagID of the highest permission group of the given player.
     *
     * @param offlinePlayer the player
     *
     * @return The permission group's TagID of the highest permission group
     * of the given player
     *
     * @see PermissionGroup
     */
    public static int getPlayerGroupTagID(OfflinePlayer offlinePlayer) {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getTagId();
    }

    /**
     * Gets the timeout of the player in their highest permission group.
     *
     * @param offlinePlayer the player to get the timeout for
     *
     * @return The timeout of the player in their highest permission group
     * or -1 if it´s lifetime.
     *
     * @see GroupEntityData
     */
    public static long getPlayerGroupRemainingTime(OfflinePlayer offlinePlayer) {
        return offlinePlayer.getPermissionEntity()
                            .getGroups()
                            .stream()
                            .filter(e -> e.getGroup().equals(getGroupName(offlinePlayer)))
                            .findFirst()
                            .map(GroupEntityData::getTimeout)
                            .orElse(-1L);
    }

    /**
     * Gets the name of the highest permission group for the given player.
     *
     * @param offlinePlayer the player to get the name of their highest
     *                      permission group for.
     *
     * @return the name of the highest permission group of the given player
     *
     * @see #getGroupName(UUID)
     * @see PermissionGroup
     * @see PermissionEntity
     */
    public static String getGroupName(OfflinePlayer offlinePlayer) {
        return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName();
    }

    /**
     * Adds a permission to the given player.
     *
     * @param offlinePlayer the player to add the permission to
     * @param permission    the permission to add
     */
    public static void addPlayerPermission(OfflinePlayer offlinePlayer, String permission) {
        offlinePlayer.getPermissionEntity().getPermissions().put(permission.replaceFirst("-", ""), !permission.startsWith("-"));
        updatePlayer(offlinePlayer);
    }

    /**
     * Updates the given player in the player database.
     *
     * @param offlinePlayer the player to update
     */
    public static void updatePlayer(OfflinePlayer offlinePlayer) {
        CloudAPI.getInstance().updatePlayer(offlinePlayer);
    }

    /**
     * Removes a permission from the given player.
     *
     * @param offlinePlayer the player to remove the permission from
     * @param permission    the permission to remove
     */
    public static void removePlayerPermission(OfflinePlayer offlinePlayer, String permission) {
        offlinePlayer.getPermissionEntity().getPermissions().remove(permission);
        updatePlayer(offlinePlayer);
    }

    /**
     * Adds the given permission to the given permission group.
     *
     * @param permissionGroupName the name of the permission group
     *                            to add the permission to.
     * @param permission          the permission to add
     */
    public static void addPermission(String permissionGroupName, String permission) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(permissionGroupName)) {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(permissionGroupName);
            permissionGroup.getPermissions().put(permission.replaceFirst("-", ""), !permission.startsWith("-"));
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Updates the permission group in the database.
     *
     * @param permissionGroup the permission group to update
     */
    public static void updatePermissionGroup(PermissionGroup permissionGroup) {
        CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
    }

    /**
     * Removes the given permission from the given permission group.
     *
     * @param permissionGroupName the name of the permission group to remove the
     *                            permission from.
     * @param permission          the permission to remove.
     */
    public static void removePermission(String permissionGroupName, String permission) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(permissionGroupName)) {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(permissionGroupName);
            permissionGroup.getPermissions().remove(permission);
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Adds the given permission to the given permission group for the given
     * server group.
     *
     * @param permissionGroupName the name of the permission group
     *                            to add the permission to.
     * @param permission          the permission to add
     * @param serverGroup         the server group to restrict the permission to
     */
    public static void addServerGroupPermission(String permissionGroupName, String permission, String serverGroup) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(permissionGroupName)) {
            PermissionGroup permissionGroup = permissionPool.getGroups().get(permissionGroupName);
            if (!permissionGroup.getServerGroupPermissions().containsKey(permission)) {
                permissionGroup.getServerGroupPermissions().put(permission, new ArrayList<>());
            }
            permissionGroup.getServerGroupPermissions().get(serverGroup).add(permission.replaceFirst("-", ""));
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Removes the given permission from the given permission group for the given
     * server group.
     *
     * @param permissionGroupName the name of the permission group
     *                            to remove the permission from.
     * @param permission          the permission to remove
     * @param serverGroup         the server group to remove the permission from
     */
    public static void removeServerGroupPermission(String permissionGroupName, String permission, String serverGroup) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(permissionGroupName)) {
            PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(permissionGroupName);
            if (permissionGroup == null) {
                return;
            }

            if (!permissionGroup.getServerGroupPermissions().containsKey(permission)) {
                permissionGroup.getServerGroupPermissions().put(permission, new ArrayList<>());
            }

            permissionGroup.getServerGroupPermissions().get(serverGroup).remove(permission.replaceFirst("-", ""));
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets permission group for the given player.
     *
     * @param offlinePlayer the player to set the permission groups for
     * @param groupName     the group name of the group to add
     * @param time          the time after which the group will be removed
     *
     * @see GroupEntityData
     */
    public static void setPlayerGroup(OfflinePlayer offlinePlayer, String groupName, long time) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            offlinePlayer.getPermissionEntity().getGroups().clear();
            offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(groupName, (time == -1 ? 0 : time)));
            updatePlayer(offlinePlayer);
        }
    }

    /**
     * Adds the given permission group by name to the player.
     *
     * @param player    the player to add the permission group to.
     * @param groupName the group to add to the player
     * @param time      the time after which the group will be removed
     *
     * @see GroupEntityData
     */
    public static void addPlayerGroup(OfflinePlayer player, String groupName, long time) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            player.getPermissionEntity().getGroups().add(new GroupEntityData(groupName, (time == -1 ? 0 : time)));
            updatePlayer(player);
        }
    }

    /**
     * Removes the given permission group by name from the given player.
     *
     * @param groupName     the name of the permission group to remove
     * @param offlinePlayer the player to remove the permission group from
     *
     * @see PermissionGroup
     */
    public static void removePlayerGroup(String groupName, OfflinePlayer offlinePlayer) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        final List<GroupEntityData> groupEntityData = new ArrayList<>(offlinePlayer.getPermissionEntity().getGroups());
        groupEntityData.forEach(group -> {
            if (group.getGroup().equals(groupName)) {
                offlinePlayer.getPermissionEntity().getGroups().remove(group);
            }
        });

        if (offlinePlayer.getPermissionEntity().getGroups().size() == 0) {
            offlinePlayer.getPermissionEntity().getGroups().add(new GroupEntityData(permissionPool.getDefaultGroup().getName(), 0));
        }

        updatePlayer(offlinePlayer);
    }

    /**
     * Gets a string representation of the permission groups of the given player.
     *
     * @param offlinePlayer the player to get the groups as a string for
     *
     * @return Player Groups of the given player as String or  null if the player doesn't exists
     */
    public static String getPlayerGroups(OfflinePlayer offlinePlayer) {
        if (offlinePlayer != null && offlinePlayer.getPermissionEntity() != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (GroupEntityData groupEntityData : offlinePlayer.getPermissionEntity().getGroups()) {
                stringBuilder.append(groupEntityData.getGroup())
                             .append('@')
                             .append(groupEntityData.getTimeout() == 0 || groupEntityData.getTimeout() == -1 ? "LIFETIME" : simpleDateFormat
                                 .format(groupEntityData.getTimeout()))
                             .append(Character.LINE_SEPARATOR);
            }
            return stringBuilder.toString();
        }
        return null;
    }

    /**
     * Returns all permission groups.
     *
     * @return All permission groups
     */
    public static Collection<PermissionGroup> getGroups() {
        return Collections.unmodifiableCollection(CloudAPI.getInstance().getPermissionPool().getGroups().values());
    }

    /**
     * Returns the specified permission group by name.
     *
     * @param groupName the name of the group to get
     *
     * @return the specified permission group or null if the group doesn't exist
     */
    public static PermissionGroup getPermissionGroup(String groupName) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            return permissionPool.getGroups().get(groupName);
        }

        return null;
    }

    /**
     * Sets the display for the given group by name.
     *
     * @param groupName the name of the group
     * @param display   the display to set
     */
    public static void setDisplay(String groupName, String display) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            final PermissionGroup permissionGroup = permissionPool.getGroups().get(groupName);
            permissionGroup.setDisplay(display);
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets the prefix for the given group by name.
     *
     * @param groupName the name of the group
     * @param prefix    the prefix to set
     */
    public static void setPrefix(String groupName, String prefix) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            final PermissionGroup permissionGroup = permissionPool.getGroups().get(groupName);
            permissionGroup.setPrefix(prefix);
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets the suffix for the given group by name.
     *
     * @param groupName the name of the group
     * @param suffix    the suffix to set
     */
    public static void setSuffix(String groupName, String suffix) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            final PermissionGroup permissionGroup = permissionPool.getGroups().get(groupName);
            permissionGroup.setSuffix(suffix);
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets the default permission group.
     *
     * @param groupName the name of the group to set as default
     */
    public static void setDefaultGroup(String groupName) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            final PermissionGroup permissionGroup = permissionPool.getGroups().get(groupName);
            final PermissionGroup old = permissionPool.getDefaultGroup();
            old.setDefaultGroup(false);
            permissionGroup.setDefaultGroup(true);
            updatePermissionGroup(old);
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets the join power of the specified permission group by name.
     *
     * @param groupName the name of the group
     * @param joinPower the new join power of the group
     */
    public static void setJoinPower(String groupName, int joinPower) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            final PermissionGroup permissionGroup = permissionPool.getGroups().get(groupName);
            permissionGroup.setJoinPower(joinPower);
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Sets the tag id of the given permission group by name.
     *
     * @param groupName the name of the group
     * @param tagId     the new tag id.
     */
    public static void setTagID(String groupName, int tagId) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (permissionPool.getGroups().containsKey(groupName)) {
            final PermissionGroup permissionGroup = permissionPool.getGroups().get(groupName);
            permissionGroup.setTagId(tagId);
            updatePermissionGroup(permissionGroup);
        }
    }

    /**
     * Creates the a new permission group with the given name.
     *
     * @param permissionGroupName the name of the new permission group
     *
     * @return the new permission group or {@code null}, if the group already
     * exists.
     */
    public PermissionGroup createPermissionGroup(String permissionGroupName) {
        final PermissionPool permissionPool = CloudAPI.getInstance().getPermissionPool();
        if (!permissionPool.getGroups().containsKey(permissionGroupName)) {
            final PermissionGroup permissionGroup = new DefaultPermissionGroup(permissionGroupName);
            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
            return permissionGroup;
        }
        return null;
    }
}
