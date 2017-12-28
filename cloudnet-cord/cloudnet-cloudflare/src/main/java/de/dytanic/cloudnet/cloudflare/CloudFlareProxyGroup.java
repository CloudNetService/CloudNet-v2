/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Tareko on 23.08.2017.
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CloudFlareProxyGroup {

    /**
     * BungeeCord Group Name
     */
    private String name;

    /**
     * SubDomain Name
     */
    private String sub;

}