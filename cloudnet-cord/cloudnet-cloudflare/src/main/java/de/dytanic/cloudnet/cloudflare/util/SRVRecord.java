/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare.util;

import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * A representation of an SRV DNS record
 */
public class SRVRecord extends DNSRecord {

    /**
     * Constructs a new {@link DNSRecord} with parameters specialized for an SRV record.
     * This is not proxied and has an automatic TTL.
     *
     * @param name     the name of this record
     * @param content  the content of this record
     * @param service  the service this SRV record should belong to
     * @param proto    the protocol that serves the {@code service}
     * @param name_    the domain this record is associated to
     * @param priority the priority of this SRV record
     * @param weight   the weight of this SRV record
     * @param port     the port this record points at
     * @param target   the target this records points to
     */
    public SRVRecord(String name,
                     String content,
                     String service,
                     String proto,
                     String name_,
                     int priority,
                     int weight,
                     int port,
                     String target) {
        super(DNSType.SRV.name(), name, content, 1, false, new Document().append("service", service)
                                                                         .append("proto", proto)
                                                                         .append("name",
                                                                                 name_)
                                                                         .append("priority", priority)
                                                                         .append("weight", weight)
                                                                         .append("port", port)
                                                                         .append("target", target)
                                                                         .obj());
    }

}
