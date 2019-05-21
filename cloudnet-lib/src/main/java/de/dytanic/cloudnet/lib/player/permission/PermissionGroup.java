package de.dytanic.cloudnet.lib.player.permission;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by Tareko on 01.06.2017.
 */
@Data
@AllArgsConstructor
public class PermissionGroup {

    protected String name;
    protected String color;
    protected String prefix;
    protected String suffix;
    protected String display;
    protected int tagId;
    protected int joinPower;
    protected boolean defaultGroup;
    protected Map<String, Boolean> permissions;
    protected Map<String, List<String>> serverGroupPermissions;
    protected Map<String, Object> options;
    protected List<String> implementGroups;

}