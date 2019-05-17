package de.dytanic.cloudnetwrapper.models;

public class PaperMCProjectVersion {
  private final String project;
  private final String version;
  private final PaperMCProjectBuild builds;


  public PaperMCProjectVersion(String project, String version,
      PaperMCProjectBuild builds) {
    this.project = project;
    this.version = version;
    this.builds = builds;
  }

  public String getProject() {
    return project;
  }

  public PaperMCProjectBuild getBuilds() {
    return builds;
  }

  public String getVersion() {
    return version;
  }
}
