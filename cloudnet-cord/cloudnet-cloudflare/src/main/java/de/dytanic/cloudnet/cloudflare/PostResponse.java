/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import de.dytanic.cloudnet.cloudflare.util.DNSRecord;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by Tareko on 26.08.2017.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class PostResponse {

    private DNSRecord dnsRecord;

    private String id;

}