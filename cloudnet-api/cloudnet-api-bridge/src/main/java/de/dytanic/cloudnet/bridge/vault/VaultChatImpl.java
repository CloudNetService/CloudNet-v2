package de.dytanic.cloudnet.bridge.vault;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

/**
 * Created by Tareko on 21.12.2017.
 */
@SuppressWarnings("deprecation")
public class VaultChatImpl extends Chat {

    public VaultChatImpl(Permission perms) {
        super(perms);
    }

    @Override
    public String getName() {
        return "CloudNet-Chat";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
        final String prefix = offlinePlayer.getPermissionEntity().getPrefix();
        if (prefix != null) {
            return prefix;
        } else {
            return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getPrefix();
        }
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
        offlinePlayer.getPermissionEntity().setPrefix(prefix);
        CloudAPI.getInstance().updatePlayer(offlinePlayer);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
        final String suffix = offlinePlayer.getPermissionEntity().getSuffix();
        if (suffix != null) {
            return suffix;
        } else {
            return offlinePlayer.getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool()).getSuffix();
        }
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        OfflinePlayer offlinePlayer = CloudAPI.getInstance().getOfflinePlayer(player);
        offlinePlayer.getPermissionEntity().setSuffix(suffix);
        CloudAPI.getInstance().updatePlayer(offlinePlayer);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(group);
        if (permissionGroup != null) {
            return permissionGroup.getPrefix();
        } else {
            return null;
        }
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(group);
        if (permissionGroup != null) {
            permissionGroup.setPrefix(prefix);
            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        }
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(group);
        if (permissionGroup != null) {
            return permissionGroup.getSuffix();
        } else {
            return null;
        }
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(group);
        if (permissionGroup != null) {
            permissionGroup.setSuffix(suffix);
            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        }
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        return defaultValue;
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {

    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {

    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        return defaultValue;
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {

    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {

    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {

    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {

    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        return defaultValue;
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {

    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        return defaultValue;
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {

    }

}
