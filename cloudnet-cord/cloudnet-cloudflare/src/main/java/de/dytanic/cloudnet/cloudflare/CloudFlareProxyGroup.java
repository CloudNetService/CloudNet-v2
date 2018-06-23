/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Container for a CloudFlare proxy group.
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CloudFlareProxyGroup {

    /**
     * Name of the BungeeCord group
     */
    private String name;

    /**
     * Name of the sub-domain
     */
    private String sub;

}
