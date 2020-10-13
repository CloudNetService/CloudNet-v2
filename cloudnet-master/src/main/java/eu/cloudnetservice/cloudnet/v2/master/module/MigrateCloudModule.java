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

package eu.cloudnetservice.cloudnet.v2.master.module;

import com.vdurmont.semver4j.Semver;

/**
 * This class can be used when updating a module from an older version to a newer version
 */
public interface MigrateCloudModule {

    /**
     * The method is executed as soon as an update is executed and you want to migrate something from version A to B. Like a configuration file or database
     * @param oldVersion is the old version of the module
     * @param newVersion is the new version of the module
     * @return If true is returned the migration was successful
     */
    boolean migrate(Semver oldVersion, Semver newVersion);
}
