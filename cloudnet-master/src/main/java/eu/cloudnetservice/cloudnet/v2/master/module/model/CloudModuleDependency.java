package eu.cloudnetservice.cloudnet.v2.master.module.model;

import com.vdurmont.semver4j.Semver;

public final class CloudModuleDependency {

    private final String groupId;
    private final String name;
    private final Semver version;


    public CloudModuleDependency(final String groupId, final String name, final String version) {
        this.groupId = groupId;
        this.name = name;
        this.version = new Semver(version, Semver.SemverType.NPM);
    }

    public String getName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public Semver getVersion() {
        return version;
    }
}
