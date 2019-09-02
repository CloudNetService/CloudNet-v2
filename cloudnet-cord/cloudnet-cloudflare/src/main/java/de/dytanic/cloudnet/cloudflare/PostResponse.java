/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import de.dytanic.cloudnet.cloudflare.util.DNSRecord;
import lombok.EqualsAndHashCode;

/**
 * Container for CloudFlare responses.
 */
@EqualsAndHashCode
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
