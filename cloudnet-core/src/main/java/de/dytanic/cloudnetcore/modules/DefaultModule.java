package de.dytanic.cloudnetcore.modules;

import de.dytanic.cloudnetcore.CloudNet;

import java.io.InputStream;

/**
 * Created by Tareko on 22.10.2017.
 */
public final class DefaultModule {

    private String moduleName;

    private String moduleVersion;

    DefaultModule(String moduleName, String moduleVersion) {
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
    }

    InputStream stream() {
        return CloudNet.class.getClassLoader().getResourceAsStream("modules/" + moduleName + ".jar");
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }
}
