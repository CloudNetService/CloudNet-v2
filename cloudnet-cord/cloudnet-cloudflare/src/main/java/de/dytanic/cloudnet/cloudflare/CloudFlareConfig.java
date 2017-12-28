/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

/**
 * Created by Tareko on 23.08.2017.
 */
@Getter
@AllArgsConstructor
public class CloudFlareConfig {

    private boolean enabled;

    private String email;

    private String token;

    private String domainName;

    private String zoneId;

    private Collection<CloudFlareProxyGroup> groups;

}