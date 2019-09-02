package de.dytanic.cloudnet.setup.models;

public class PaperMCProjectVersion {

    private final String project;
    private final String version;
    private final PaperMCProjectBuilds builds;


    public PaperMCProjectVersion(String project, String version, PaperMCProjectBuilds builds) {
        this.project = project;
        this.version = version;
        this.builds = builds;
    }

    public String getProject() {
        return project;
    }

    public PaperMCProjectBuilds getBuilds() {
        return builds;
    }

    public String getVersion() {
        return version;
    }
}
