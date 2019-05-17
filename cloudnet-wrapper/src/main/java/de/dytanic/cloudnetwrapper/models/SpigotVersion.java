package de.dytanic.cloudnetwrapper.models;

public class SpigotVersion {

  private final String name;
  private final String path;

  public SpigotVersion(String name, String path) {
    this.name = name;
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

}
