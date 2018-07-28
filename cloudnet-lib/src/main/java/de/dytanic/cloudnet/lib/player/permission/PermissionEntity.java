package de.dytanic.cloudnet.lib.player.permission;

import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Calls
 */
@AllArgsConstructor
@Getter
@Setter
public class PermissionEntity {
    protected UUID uniqueId;
    protected java.util.Map<String, Boolean> permissions;

    protected String prefix;
    @Setter
    protected String suffix;

    protected Collection<GroupEntityData> groups;

    public boolean hasPermission(PermissionPool permissionPool, String permission, String group)
    {
        if (checkAdminPermission(permission)) return true;

        if (permissionPool == null || permission == null) return false;

        String adminPermission = null;
        String[] block = permission.split("\\.");

        if (block.length > 1) adminPermission = block[0] + ".*";

        if (permissions.containsKey(permission) && !permissions.get(permission))
            return false;
        else if (adminPermission != null && permissions.containsKey(adminPermission) && !permissions.get(adminPermission))
            return false;
        else if (permissions.containsKey("*") && permissions.get("*"))
            return true;
        else if ((permissions.containsKey(permission)) && permissions.get(permission)) return true;
        else if (adminPermission != null && permissions.containsKey(adminPermission) && permissions.get(adminPermission))
            return true;

        for (GroupEntityData implg : groups)
        {
            if (!permissionPool.getGroups().containsKey(implg.getGroup())) continue;
            PermissionGroup permissionGroup = permissionPool.getGroups().get(implg.getGroup());

            if (checkAdminPermission(permissionGroup, permission)) return true;

            if (checkAccess(permissionGroup, permission, adminPermission, group)) return true;

            for (String implGroup : permissionGroup.getImplementGroups())
            {
                if (!permissionPool.getGroups().containsKey(implGroup)) continue;

                PermissionGroup subGroup = permissionPool.getGroups().get(implGroup);
                if (checkAccess(subGroup, permission, adminPermission, group)) return true;
            }

        }

        return false;
    }

    private boolean checkAdminPermission(String permission)
    {
        List<String> parts = Arrays.asList(permission.split("\\."));
        if (parts.size() < 2) return false;
        for (int i = parts.size() - 2; i >= 1; i--)
        {
            String perm = String.join(".", parts.subList(0, i)) + ".*";
            if (permissions.containsKey(perm)) return permissions.get(perm);
        }
        return false;
    }

    private boolean checkAdminPermission(PermissionGroup permissionGroup, String permission)
    {
        List<String> parts = Arrays.asList(permission.split("\\."));
        if (parts.size() < 2) return false;
        for (int i = parts.size() - 2; i >= 1; i--)
        {
            String perm = String.join(".", parts.subList(0, i)) + ".*";
            if (permissionGroup.permissions.containsKey(perm))
                return permissionGroup.permissions.get(perm);
        }
        return false;
    }

    public PermissionGroup getHighestPermissionGroup(PermissionPool permissionPool)
    {
        PermissionGroup permissionGroup = null;

        for (GroupEntityData groupEntityData : getGroups())
        {
            if (permissionGroup == null)
                permissionGroup = permissionPool.getGroups().get(groupEntityData.getGroup());
            else
            {
                if (permissionGroup.getJoinPower() < permissionPool.getGroups().get(groupEntityData.getGroup()).getJoinPower())
                {
                    permissionGroup = permissionPool.getGroups().get(groupEntityData.getGroup());
                }
            }
        }
        return permissionGroup;
    }

    public boolean isInGroup(String group)
    {
        return CollectionWrapper.filter(this.groups, value -> value.getGroup().equals(group)) != null;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    /*= -------------------------------------------------------------------------------- =*/

    private boolean checkAccess(PermissionGroup permissionGroup, String permission, String adminPermission, String group)
    {
        if ((adminPermission != null && (permissionGroup.getPermissions().containsKey(adminPermission) &&
                !permissionGroup.getPermissions().get(adminPermission))) ||
                (permissionGroup.getPermissions().containsKey("*") && !permissionGroup.getPermissions().get("*")) ||
                (permissionGroup.getPermissions().containsKey(permission) && !permissionGroup.getPermissions().get(permission)))
            return false;

        if ((permissionGroup.getPermissions().containsKey("*") && permissionGroup.getPermissions().get("*")))
            return true;

        if ((permissionGroup.getPermissions().containsKey(permission) && permissionGroup.getPermissions().get(permission)) ||
                (adminPermission != null && (permissionGroup.getPermissions().containsKey(adminPermission) && permissionGroup.getPermissions().get(adminPermission))))
            return true;

        if (permissionGroup.getServerGroupPermissions().containsKey(group))
        {
            return permissionGroup.getServerGroupPermissions().get(group).contains(permission) ||
                    permissionGroup.getServerGroupPermissions().get(group).contains("*")
                    || (adminPermission != null && permissionGroup.getServerGroupPermissions().get(group).contains(adminPermission));
        }

        return false;
    }

}
