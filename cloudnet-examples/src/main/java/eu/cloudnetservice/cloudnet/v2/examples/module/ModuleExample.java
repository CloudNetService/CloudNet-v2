/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.examples.module;

import com.vdurmont.semver4j.Semver;
import eu.cloudnetservice.cloudnet.v2.master.module.JavaCloudModule;
import eu.cloudnetservice.cloudnet.v2.master.module.MigrateCloudModule;
import eu.cloudnetservice.cloudnet.v2.master.module.UpdateCloudModule;

import java.io.IOException;
import java.nio.file.Files;

public class ModuleExample extends JavaCloudModule implements MigrateCloudModule, UpdateCloudModule {

    @Override
    public void onLoad() {
        getModuleLogger().info("Onload was triggered from example module");
    }

    @Override
    public void onEnable() {
        try {
            /**
             * Creates one file folder per module for configuration files or other files
             */
            Files.createDirectories(getDataFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * This will give you the logger of the module you are running. System.out does not work. So you have to use the logger
         */
        getModuleLogger().info("Onenable was triggered from example module");
        /**
         * getCloud returns the current CloudNet instance. This allows you to interact directly with the cloud. An instance is no longer used
         */
        getCloud().checkForUpdates();

        /**
         * getModuleJson returns all information from your Module Json that you can use
         */
        getModuleLogger().info(getModuleJson().getName());

        /**
         * Here with you can register your own command in the master by module
         */
        registerCommand(new ExampleModuleCommand(this,"exampleCommand", "module.commands.examplecommand","ec"));
        /**
         * Here with you can register listener like for a private server system
         */
        registerListener(new ExampleModuleListener(this));
    }

    @Override
    public void onDisable() {
        getModuleLogger().info("Ondisable was triggered from example module");
    }

    @Override
    public boolean migrate(final Semver oldVersion, final Semver newVersion) {
        return true;
    }

    @Override
    public boolean update(final String url) {
        return true;
    }
}
