package de.dytanic.cloudnet.bridge.vault;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

/**
 * Created by Tareko on 21.12.2017.
 */
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
    public String getPlayerPrefix(String s, String s1) {
        OfflinePlayer offlinePlayer = getPlayer(s1);
        return offlinePlayer.getPermissionEntity().getPrefix() != null ? offlinePlayer.getPermissionEntity()
                                                                                      .getPrefix() : offlinePlayer.getPermissionEntity()
                                                                                                                  .getHighestPermissionGroup(
                                                                                                                      CloudAPI.getInstance()
                                                                                                                              .getPermissionPool())
                                                                                                                  .getPrefix();
    }

    @Override
    public void setPlayerPrefix(String s, String s1, String s2) {
        OfflinePlayer offlinePlayer = getPlayer(s1);
        offlinePlayer.getPermissionEntity().setPrefix(s2);
        update(offlinePlayer);
    }

    @Override
    public String getPlayerSuffix(String s, String s1) {
        OfflinePlayer offlinePlayer = getPlayer(s1);
        return offlinePlayer.getPermissionEntity().getSuffix() != null ? offlinePlayer.getPermissionEntity()
                                                                                      .getSuffix() : offlinePlayer.getPermissionEntity()
                                                                                                                  .getHighestPermissionGroup(
                                                                                                                      CloudAPI.getInstance()
                                                                                                                              .getPermissionPool())
                                                                                                                  .getSuffix();
    }

    @Override
    public void setPlayerSuffix(String s, String s1, String s2) {
        OfflinePlayer offlinePlayer = getPlayer(s1);
        offlinePlayer.getPermissionEntity().setSuffix(s2);
        update(offlinePlayer);
    }

    @Override
    public String getGroupPrefix(String s, String s1) {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(s1);
        if (permissionGroup != null) {
            return permissionGroup.getPrefix();
        } else {
            return null;
        }
    }

    @Override
    public void setGroupPrefix(String s, String s1, String s2) {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(s1);
        if (permissionGroup != null) {
            permissionGroup.setPrefix(s2);
            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        }
    }

    @Override
    public String getGroupSuffix(String s, String s1) {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(s1);
        if (permissionGroup != null) {
            return permissionGroup.getSuffix();
        } else {
            return null;
        }
    }

    @Override
    public void setGroupSuffix(String s, String s1, String s2) {
        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionPool().getGroups().get(s1);
        if (permissionGroup != null) {
            permissionGroup.setSuffix(s2);
            CloudAPI.getInstance().updatePermissionGroup(permissionGroup);
        }
    }

    @Override
    public int getPlayerInfoInteger(String s, String s1, String s2, int i) {
        return 0;
    }

    @Override
    public void setPlayerInfoInteger(String s, String s1, String s2, int i) {

    }

    @Override
    public int getGroupInfoInteger(String s, String s1, String s2, int i) {
        return 0;
    }

    @Override
    public void setGroupInfoInteger(String s, String s1, String s2, int i) {

    }

    @Override
    public double getPlayerInfoDouble(String s, String s1, String s2, double v) {
        return 0;
    }

    @Override
    public void setPlayerInfoDouble(String s, String s1, String s2, double v) {

    }

    @Override
    public double getGroupInfoDouble(String s, String s1, String s2, double v) {
        return 0;
    }

    @Override
    public void setGroupInfoDouble(String s, String s1, String s2, double v) {

    }

    @Override
    public boolean getPlayerInfoBoolean(String s, String s1, String s2, boolean b) {
        return false;
    }

    @Override
    public void setPlayerInfoBoolean(String s, String s1, String s2, boolean b) {

    }

    @Override
    public boolean getGroupInfoBoolean(String s, String s1, String s2, boolean b) {
        return false;
    }

    @Override
    public void setGroupInfoBoolean(String s, String s1, String s2, boolean b) {

    }

    @Override
    public String getPlayerInfoString(String s, String s1, String s2, String s3) {
        return null;
    }

    @Override
    public void setPlayerInfoString(String s, String s1, String s2, String s3) {

    }

    @Override
    public String getGroupInfoString(String s, String s1, String s2, String s3) {
        return null;
    }

    @Override
    public void setGroupInfoString(String s, String s1, String s2, String s3) {

    }

    private void update(OfflinePlayer offlinePlayer) {
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
