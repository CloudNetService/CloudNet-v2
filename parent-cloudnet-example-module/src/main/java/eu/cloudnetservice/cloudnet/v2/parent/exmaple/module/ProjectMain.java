package eu.cloudnetservice.cloudnet.v2.parent.exmaple.module;


import eu.cloudnetservice.cloudnet.v2.master.module.JavaCloudModule;

public class ProjectMain extends JavaCloudModule {

    @Override
    public void onLoad() {
        getModuleLogger().info("onLoad YOLO 3... 2... 1...");
    }

    @Override
    public void onEnable() {
        getModuleLogger().info("YOLO 3... 2... 1...");
    }

    @Override
    public void onDisable() {
        getModuleLogger().info("1... 2... 3... YOLO");
    }
}
