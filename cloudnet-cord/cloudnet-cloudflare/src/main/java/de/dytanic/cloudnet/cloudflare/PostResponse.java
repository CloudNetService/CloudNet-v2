/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import de.dytanic.cloudnet.cloudflare.util.DNSRecord;

import java.util.Objects;

/**
 * Container for CloudFlare responses.
 */
public class PostResponse {
    /**
     * The provided configuration for this cached record
     */
    private CloudFlareConfig cloudFlareConfig;
    /**
     * The DNS record that created the response
     */
    private DNSRecord dnsRecord;
    /**
     * The ID at CloudFlare that identifies this DNS record
     */
    private String id;

    public PostResponse(CloudFlareConfig cloudFlareConfig, DNSRecord dnsRecord, String id) {
        this.cloudFlareConfig = cloudFlareConfig;
        this.dnsRecord = dnsRecord;
        this.id = id;
    }

    @Override
    public int hashCode() {
        int result = cloudFlareConfig != null ? cloudFlareConfig.hashCode() : 0;
        result = 31 * result + (dnsRecord != null ? dnsRecord.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostResponse)) {
            return false;
        }
        final PostResponse that = (PostResponse) o;
        return Objects.equals(cloudFlareConfig, that.cloudFlareConfig) && Objects.equals(dnsRecord, that.dnsRecord) && Objects.equals(id,
                                                                                                                                      that.id);
    }

    public CloudFlareConfig getCloudFlareConfig() {
        return cloudFlareConfig;
    }

    public DNSRecord getDnsRecord() {
        return dnsRecord;
    }

    public String getId() {
        return id;
    }
}
