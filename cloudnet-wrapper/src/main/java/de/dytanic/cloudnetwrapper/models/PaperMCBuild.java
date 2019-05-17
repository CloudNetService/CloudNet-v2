package de.dytanic.cloudnetwrapper.models;

public class PaperMCBuild {
  private final String Project;
  private final String Version;
  private final String Build;

  public PaperMCBuild(String project, String version, String build) {
    Project = project;
    Version = version;
    Build = build;
  }

  public String getProject() {
    return Project;
  }

  public String getVersion() {
    return Version;
  }

  public String getBuild() {
    return Build;
  }
}
