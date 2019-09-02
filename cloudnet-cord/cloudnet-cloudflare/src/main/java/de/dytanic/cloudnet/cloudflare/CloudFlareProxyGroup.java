/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Container for a CloudFlare proxy group.
 */
@ToString
@EqualsAndHashCode
public class CloudFlareProxyGroup {

	/**
	 * Name of the BungeeCord group
	 */
	private String name;

	/**
	 * Name of the sub-domain
	 */
	private String sub;

	public CloudFlareProxyGroup(String name, String sub) {
		this.name = name;
		this.sub = sub;
	}

	public String getName() {
		return name;
	}

	public String getSub() {
		return sub;
	}
}
