package eu.cloudnetservice.cloudnet.v2.wrapper.setup;

public enum GetBukkitVersion {
    v1_8_8("1.8.8", "https://cdn.getbukkit.org/spigot/spigot-%s-R0.1-SNAPSHOT-latest.jar"),
    v1_9_4("1.9.4", "https://cdn.getbukkit.org/spigot/spigot-%s-R0.1-SNAPSHOT-latest.jar"),
    v1_10_2("1.10.2", "https://cdn.getbukkit.org/spigot/spigot-%s-R0.1-SNAPSHOT-latest.jar"),
    v1_11_2("1.11.2", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_12_2("1.12.2", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_13("1.13", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_13_1("1.13.1", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_13_2("1.13.2", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_14("1.14", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_14_1("1.14.1", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_14_2("1.14.2", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_14_3("1.14.3", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_14_4("1.14.4", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_15("1.15", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_15_1("1.15.1", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_15_2("1.15.2", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_16_1("1.16.1", "https://cdn.getbukkit.org/spigot/spigot-%s.jar"),
    v1_16_2("1.16.2", "https://cdn.getbukkit.org/spigot/spigot-%s.jar");

    private final String version;
    private final String url;

    GetBukkitVersion(final String version, final String url) {
        this.version = version;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }
}
