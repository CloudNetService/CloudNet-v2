/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import java.util.Collection;

/**
 * Container for the CloudFlare configuration.
 */
public class CloudFlareConfig {

    /**
     * Whether or not the module is enabled
     */
    private boolean enabled;

    /**
     * The E-Mail address of the account to use.
     */
    private String email;

    /**
     * The token to authenticate at the CloudFlare API with.
     */
    private String token;

    /**
     * The domain to create records for.
     */
    private String domainName;

    /**
     * The internal zone ID at CloudFlare
     */
    private String zoneId;

    /**
     * All configured BungeeCord groups and their sub-domains
     */
    private Collection<CloudFlareProxyGroup> groups;

    public CloudFlareConfig(boolean enabled,
                            String email,
                            String token,
                            String domainName,
                            String zoneId,
                            Collection<CloudFlareProxyGroup> groups) {
        this.enabled = enabled;
        this.email = email;
        this.token = token;
        this.domainName = domainName;
        this.zoneId = zoneId;
        this.groups = groups;
    }

    public Collection<CloudFlareProxyGroup> getGroups() {
        return groups;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getZoneId() {
        return zoneId;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
