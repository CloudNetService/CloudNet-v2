package de.dytanic.cloudnet.lib.player.permission;

/**
 * Created by Tareko on 28.07.2017.
 */
public class GroupEntityData {

    private String group;

    private long timeout;

    public GroupEntityData(String group, long timeout) {
        this.group = group;
        this.timeout = timeout;
    }

    public String getGroup() {
        return group;
    }

    public long getTimeout() {
        return timeout;
    }
}