/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import java.util.Objects;

/**
 * Container for a CloudFlare proxy group.
 */
public class CloudFlareProxyGroup {
    /**
     * Name of the BungeeCord group
     */
    private String name;
    /**
     * Name of the sub-domain
     */
    private String sub;

    public CloudFlareProxyGroup(String name, String sub) {
        this.name = name;
        this.sub = sub;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (sub != null ? sub.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CloudFlareProxyGroup)) {
            return false;
        }
        final CloudFlareProxyGroup that = (CloudFlareProxyGroup) o;
        return Objects.equals(name, that.name) && Objects.equals(sub, that.sub);
    }

    @Override
    public String toString() {
        return "CloudFlareProxyGroup{" + "name='" + name + '\'' + ", sub='" + sub + '\'' + '}';
    }

    public String getName() {
        return name;
    }

    public String getSub() {
        return sub;
    }
}
