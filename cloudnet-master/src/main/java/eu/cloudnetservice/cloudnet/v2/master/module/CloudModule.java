package eu.cloudnetservice.cloudnet.v2.master.module;

import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.module.model.CloudModuleDescriptionFile;

import java.nio.file.Path;

public interface CloudModule {

    void onLoad();

    void onEnable();

    void onDisable();

    CloudModuleDescriptionFile getModuleJson();

    Path getDataFolder();

    CloudNet getCloud();

    CloudModuleLogger getModuleLogger();

    boolean isEnabled();
}
