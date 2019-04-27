/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.modules;

import de.dytanic.cloudnetcore.CloudNet;
import java.io.InputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 22.10.2017.
 */
@Getter
@AllArgsConstructor
public final class DefaultModule {

    private String moduleName;

    private String moduleVersion;

    protected InputStream stream()
    {
        return CloudNet.class.getClassLoader().getResourceAsStream("modules/" + moduleName + ".jar");
    }
}