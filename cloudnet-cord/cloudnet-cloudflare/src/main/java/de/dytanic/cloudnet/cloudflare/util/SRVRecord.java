/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare.util;

import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.Getter;

/**
 * Created by Tareko on 26.08.2017.
 */
@Getter
public class SRVRecord extends DNSRecord {

    public SRVRecord(String name, String content, String service, String proto, String name_, int priority, int weight, int port, String target)
    {
        super(DNSType.SRV.name(), name, content, 1, false, new Document().append("service", service).append("proto", proto).append("name", name_).append("priority", priority).append("weight", weight).append("port", port).append("target", target).obj());
    }

}