package eu.cloudnetservice.cloudnet.v2.lib.proxylayout;

public class TabList {

    private final boolean enabled;

    private final String header;

    private final String footer;

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