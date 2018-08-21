/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import de.dytanic.cloudnet.cloudflare.util.DNSRecord;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Container for CloudFlare responses.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class PostResponse {

    /**
     * The DNS record that created the response
     */
    private DNSRecord dnsRecord;

    /**
     * The ID at CloudFlare that identifies this DNS record
     */
    private String id;

}
