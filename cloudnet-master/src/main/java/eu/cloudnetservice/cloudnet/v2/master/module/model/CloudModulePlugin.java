package eu.cloudnetservice.cloudnet.v2.master.module.model;

public final class CloudModulePlugin {

    private final String path;
    private final String role;

    public CloudModulePlugin(final String path, final String role) {
        this.path = path;
        this.role = role;
    }

    public String getPath() {
        return path;
    }

    public String getRole() {
        return role;
    }
}
