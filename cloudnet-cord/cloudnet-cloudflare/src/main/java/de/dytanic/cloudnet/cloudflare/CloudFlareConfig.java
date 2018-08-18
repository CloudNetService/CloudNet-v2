/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

/**
 * Container for the CloudFlare configuration.
 */
@Getter
@AllArgsConstructor
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

}
