package eu.cloudnetservice.cloudnet.v2.master.network.components;

import java.util.Objects;

/**
 * Created by Tareko on 24.07.2017.
 */
public class WrapperMeta {

    private final String id;

    private final String hostName;

    private final String user;

    public WrapperMeta(String id, String hostName, String user) {
        this.id = id;
        this.hostName = hostName;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getHostName() {
        return hostName;
    }

    public String getUser() {
        return user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hostName, user);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WrapperMeta)) {
            return false;
        }
        final WrapperMeta that = (WrapperMeta) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(hostName, that.hostName) &&
            Objects.equals(user, that.user);
    }
}
