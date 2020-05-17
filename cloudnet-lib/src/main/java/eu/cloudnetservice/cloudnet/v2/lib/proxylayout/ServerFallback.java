package eu.cloudnetservice.cloudnet.v2.lib.proxylayout;

/**
 * Created by Tareko on 05.10.2017.
 */
public class ServerFallback {

    private final String group;

    private final String permission;

    public ServerFallback(String group, String permission) {
        this.group = group;
        this.permission = permission;
    }

    public String getGroup() {
        return group;
    }

    public String getPermission() {
        return permission;
    }
}