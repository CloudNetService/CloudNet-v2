package de.dytanic.cloudnetwrapper.util;

import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class ShutdownHook implements Runnable {

  private CloudNetWrapper cloudNetWrapper;
  public ShutdownHook(CloudNetWrapper wrapper) {
    this.cloudNetWrapper = wrapper;
  }

  @Override
  public void run() {
    if(SpigotBuilder.getExec() != null) SpigotBuilder.getExec().destroyForcibly();
    if(PaperBuilder.getExec() != null) PaperBuilder.getExec().destroyForcibly();
    cloudNetWrapper.shutdown();
  }
}
