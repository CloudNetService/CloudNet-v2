package de.dytanic.cloudnet.lib.player.permission;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Tareko on 01.06.2017.
 */
@Data
@AllArgsConstructor
public class PermissionGroup {

    protected String name;
    protected String prefix;
    protected String suffix;
    protected String display;
    protected int tagId;
    protected int joinPower;
    protected boolean defaultGroup;
    protected HashMap<String, Boolean> permissions;
    protected java.util.Map<String, List<String>> serverGroupPermissions;
    protected java.util.Map<String, Object> options;
    protected java.util.List<String> implementGroups;

}