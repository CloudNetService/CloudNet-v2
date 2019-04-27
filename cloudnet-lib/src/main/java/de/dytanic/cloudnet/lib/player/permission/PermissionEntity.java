package de.dytanic.cloudnet.lib.player.permission;

import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
        if (permission != null && (permission.equals("bukkit.broadcast") || permission.equals("bukkit.broadcast.admin")))
            return true;

        if (permissionPool == null || permission == null) return false;

        if (permissions.containsKey(permission) && !permissions.get(permission))
            return false;
        else if (hasWildcardPermission(permission))
            return true;
        else if (permissions.containsKey("*") && permissions.get("*"))
            return true;
        else if ((permissions.containsKey(permission)) && permissions.get(permission)) return true;

        for (GroupEntityData implg : groups)
        {
            if (!permissionPool.getGroups().containsKey(implg.getGroup())) continue;
            PermissionGroup permissionGroup = permissionPool.getGroups().get(implg.getGroup());

            if (hasWildcardPermission(permissionGroup, permission, group)) return true;

            if (checkAccess(permissionGroup, permission, group)) return true;

            for (String implGroup : permissionGroup.getImplementGroups())
            {
                if (!permissionPool.getGroups().containsKey(implGroup)) continue;

                PermissionGroup subGroup = permissionPool.getGroups().get(implGroup);
                if (checkAccess(subGroup, permission, group)) return true;
            }
        }

        return false;
    }

    public PermissionGroup getHighestPermissionGroup(PermissionPool permissionPool)
    {
        return this.getGroups()
                .stream()
                .map(groupEntityData -> permissionPool.getGroups().get(groupEntityData.getGroup()))
                .min(Comparator.comparingInt(PermissionGroup::getTagId)).orElse(null);
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

    private boolean hasWildcardPermission(PermissionGroup permissionGroup, String permission, String group)
    {
        for (Map.Entry<String, Boolean> entry : permissionGroup.getPermissions().entrySet())
            if (entry.getKey().endsWith("*") && entry.getKey().length() > 1 && permission.startsWith(entry.getKey().substring(0, entry.getKey().length() - 1)))
                return true;

        if (group != null && permissionGroup.getServerGroupPermissions().containsKey(group))
            for (String perms : permissionGroup.getServerGroupPermissions().get(group))
                if (perms.endsWith("*") && perms.length() > 1 && permission.startsWith(perms.substring(0, perms.length() - 1)))
                    return true;

        return false;
    }

    private boolean hasWildcardPermission(String permission)
    {
        for (Map.Entry<String, Boolean> entry : getPermissions().entrySet())
            if (entry.getKey().endsWith("*") && entry.getKey().length() > 1 && permission.startsWith(entry.getKey().substring(0, entry.getKey().length() - 1)))
                return true;

        return false;
    }

    private boolean checkAccess(PermissionGroup permissionGroup, String permission, String group)
    {
        if ((permissionGroup.getPermissions().containsKey("*") && !permissionGroup.getPermissions().get("*")) ||
                (permissionGroup.getPermissions().containsKey(permission) && !permissionGroup.getPermissions().get(permission)))
            return false;

        if ((permissionGroup.getPermissions().containsKey("*") && permissionGroup.getPermissions().get("*")))
            return true;

        if ((permissionGroup.getPermissions().containsKey(permission) && permissionGroup.getPermissions().get(permission)))
            return true;

        if (group != null)
            if (permissionGroup.getServerGroupPermissions().containsKey(group))
            {
                return permissionGroup.getServerGroupPermissions().get(group).contains(permission) ||
                        permissionGroup.getServerGroupPermissions().get(group).contains("*");
            }

        return false;
    }

}
