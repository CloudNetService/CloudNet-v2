/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

public class TabList {

    private boolean enabled;

    private String header;

    private String footer;

    public TabList(boolean enabled, String header, String footer) {
        this.enabled = enabled;
        this.header = header;
        this.footer = footer;
    }

    public String getFooter() {
        return footer;
    }

    public String getHeader() {
        return header;
    }

    public boolean isEnabled() {
        return enabled;
    }
}