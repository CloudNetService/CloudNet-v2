package de.dytanic.cloudnet.lib.player.permission;

import java.util.Objects;

/**
 * Created by Tareko on 28.07.2017.
 */
public class GroupEntityData {

    private final String group;
    private final long timeout;

    public GroupEntityData(String group, long timeout) {
        this.group = group;
        this.timeout = timeout;
    }

    @Override
    public int hashCode() {
        int result = group != null ? group.hashCode() : 0;
        result = 31 * result + (int) (timeout ^ (timeout >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GroupEntityData that = (GroupEntityData) o;

        if (timeout != that.timeout) {
            return false;
        }
        return Objects.equals(group, that.group);
    }

    @Override
    public String toString() {
        return "GroupEntityData{" +
            "group='" + group + '\'' +
            ", timeout=" + timeout +
            '}';
    }

    public String getGroup() {
        return group;
    }

    public long getTimeout() {
        return timeout;
    }
}
