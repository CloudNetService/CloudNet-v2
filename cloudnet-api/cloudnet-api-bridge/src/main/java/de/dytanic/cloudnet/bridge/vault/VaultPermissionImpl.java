package de.dytanic.cloudnet.bridge.vault;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import net.milkbowl.vault.permission.Permission;

import java.util.Optional;

/**
 * Created by Tareko on 25.11.2017.
 */
public class VaultPermissionImpl extends Permission {

    @Override
    public String getName() {
        return "CloudNet-Permission";
    }

    @Override
    public boolean isEnabled() {
        return CloudAPI.getInstance().getPermissionPool() != null;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        OfflinePlayer offlinePlayer = getPlayer(player);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        boolean hasPermission = permissionEntity.hasPermission(CloudAPI.getInstance().getPermissionPool(), permission, null);
        CloudAPI.getInstance().getLogger().finest(player + " hasPermission \"" + permission + "\": " + hasPermission);
        return hasPermission;
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        OfflinePlayer offlinePlayer = getPlayer(player);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        permissionEntity.getPermissions().put(permission, true);
        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        CloudAPI.getInstance().getLogger().finest(player + " added permission \"" + permission + '"');
        return true;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        OfflinePlayer offlinePlayer = getPlayer(player);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        permissionEntity.getPermissions().remove(permission);
        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        CloudAPI.getInstance().getLogger().finest(player + " removed permission \"" + permission + '"');
        return true;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(group);
        return permissionGroup.getPermissions().getOrDefault(permission, false);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(group);
        permissionGroup.getPermissions().put(permission, true);
        CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        CloudAPI.getInstance().getLogger().finest(group + " added permission \"" + permission + '"');
        return true;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(group);
        permissionGroup.getPermissions().remove(permission);
        CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        CloudAPI.getInstance().getLogger().finest(group + " removed permission \"" + permission + '"');
        return true;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        OfflinePlayer offlinePlayer = getPlayer(player);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        return permissionEntity.isInGroup(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        OfflinePlayer offlinePlayer = getPlayer(player);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();

        Optional<GroupEntityData> groupEntityData = permissionEntity.getGroups()
                                                                    .stream()
                                                                    .filter(ged -> ged.getGroup()
                                                                                      .equalsIgnoreCase(group))
                                                                    .findFirst();
        groupEntityData.ifPresent(entityData -> permissionEntity.getGroups().remove(entityData));

        permissionEntity.getGroups().add(new GroupEntityData(group, 0));
        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        CloudAPI.getInstance().getLogger().finest(player + " added to group \"" + group + '"');
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        OfflinePlayer offlinePlayer = getPlayer(player);
        PermissionEntity permissionEntity = offlinePlayer.getPermissionEntity();
        permissionEntity.getGroups().stream().filter(ged -> ged.getGroup().equalsIgnoreCase(group)).findFirst().ifPresent(ged -> {
            permissionEntity.getGroups().remove(ged);
        });

        offlinePlayer.setPermissionEntity(permissionEntity);
        updatePlayer(offlinePlayer);
        CloudAPI.getInstance().getLogger().finest(player + " removed from group \"" + group + '"');
        return true;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        PermissionEntity permissionEntity = getPlayer(player).getPermissionEntity();
        return permissionEntity.getGroups().stream().map(GroupEntityData::getGroup).toArray(String[]::new);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return getPlayer(player).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getName();
    }

    @Override
    public String[] getGroups() {
        return CloudAPI.getInstance().getPermissionPool().getGroups().keySet().toArray(new String[0]);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    private void updatePlayer(OfflinePlayer offlinePlayer) {
        CloudAPI.getInstance().updatePlayer(offlinePlayer);
    }

    private OfflinePlayer getPlayer(String name) {
        OfflinePlayer offlinePlayer = CloudServer.getInstance().getCachedPlayer(name);

        if (offlinePlayer == null) {
            offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(name);
        }

        return offlinePlayer;
    }
}
