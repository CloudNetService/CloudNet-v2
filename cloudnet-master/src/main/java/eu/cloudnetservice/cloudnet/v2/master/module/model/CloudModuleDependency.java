package eu.cloudnetservice.cloudnet.v2.master.module.model;

/**
 * This class returns the dependency for the module with the fields module name, version and group id
 */
public final class CloudModuleDependency {

    private final String groupId;
    private final String name;
    private final String version;

    public CloudModuleDependency(String groupId, String name, String version) {
        this.groupId = groupId;
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getVersion() {
        return version;
    }
}
