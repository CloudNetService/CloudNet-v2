package de.dytanic.cloudnet.lib.player.permission;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Created by Tareko on 01.06.2017.
 */
@ToString
@EqualsAndHashCode
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

    public PermissionGroup(String name, String color, String prefix, String suffix, String display, int tagId, int joinPower, boolean defaultGroup, Map<String, Boolean> permissions, Map<String, List<String>> serverGroupPermissions, Map<String, Object> options, List<String> implementGroups) {
        this.name = name;
        this.color = color;
        this.prefix = prefix;
        this.suffix = suffix;
        this.display = display;
        this.tagId = tagId;
        this.joinPower = joinPower;
        this.defaultGroup = defaultGroup;
        this.permissions = permissions;
        this.serverGroupPermissions = serverGroupPermissions;
        this.options = options;
        this.implementGroups = implementGroups;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public int getJoinPower() {
        return joinPower;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public int getTagId() {
        return tagId;
    }

    public List<String> getImplementGroups() {
        return implementGroups;
    }

    public Map<String, List<String>> getServerGroupPermissions() {
        return serverGroupPermissions;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public String getColor() {
        return color;
    }

    public boolean isDefaultGroup() {
        return defaultGroup;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setJoinPower(int joinPower) {
        this.joinPower = joinPower;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setPermissions(Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public void setImplementGroups(List<String> implementGroups) {
        this.implementGroups = implementGroups;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setServerGroupPermissions(Map<String, List<String>> serverGroupPermissions) {
        this.serverGroupPermissions = serverGroupPermissions;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}