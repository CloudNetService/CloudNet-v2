package de.dytanic.cloudnetwrapper.models;

public class PaperMCProjectBuild {

  private final String latest;
  private final String[] all;

  public PaperMCProjectBuild(String latest, String[] all) {
    this.latest = latest;
    this.all = all;
  }

  public String getLatest() {
    return latest;
  }

  public String[] getAll() {
    return all;
  }
}
