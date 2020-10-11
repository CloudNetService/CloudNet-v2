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

package eu.cloudnetservice.cloudnet.v2.master.module.model;

import com.google.gson.reflect.TypeToken;
import com.vdurmont.semver4j.Semver;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class contains all information to activate the module. Module dependencies, update path, minimum CloudNet version, group id, name, version and authors
 */
public final class CloudModuleDescriptionFile {


    private String main;
    private String version;
    private String name;
    private String groupId;
    private String updateUrl;

    private String description;
    private String website;
    private String requiredCloudNetVersion;

    private Set<CloudModuleDependency> dependencies;
    private Set<CloudModuleAuthor> authors;

    //transient allows us to use this variable only for runtime and can be ignored for serialization
    private transient Path file;
    private transient Semver semver;

    public static transient Type CLOUD_MODULE_DESCRIPTION_FILE = TypeToken.get(CloudModuleDescriptionFile.class).getType();

    public CloudModuleDescriptionFile(@NotNull InputStream stream,@NotNull Path file) {
        loadJson(stream, file);
    }

    private void loadJson(@NotNull InputStream stream,@NotNull Path file) {
        CloudModuleDescriptionFile thisClazz = Document.GSON.fromJson(new InputStreamReader(stream),
                                                                      CLOUD_MODULE_DESCRIPTION_FILE);
        this.main = thisClazz.main;
        this.version = thisClazz.version;
        this.name = thisClazz.name;
        this.groupId = thisClazz.groupId;
        this.updateUrl = thisClazz.updateUrl;
        this.description = thisClazz.description;
        this.website = thisClazz.website;
        this.requiredCloudNetVersion = thisClazz.requiredCloudNetVersion;
        this.dependencies = thisClazz.dependencies;
        this.authors = thisClazz.authors;
        this.semver = new Semver(version, Semver.SemverType.NPM);
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    public String getMain() {
        return main;
    }

    public String getVersion() {
        return version;
    }

    public Semver getSemVersion() {
        return this.semver;
    }

    public String getName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public Set<CloudModuleDependency> getDependencies() {
        return dependencies;
    }

    public Set<CloudModuleAuthor> getAuthors() {
        return authors;
    }

    public String getRequiredCloudNetVersion() {
        return requiredCloudNetVersion;
    }

    /**
     * Collects all authors to a string
     * @return Returns all authors as string
     */
    public String getAuthorsAsString() {
        return getAuthors().stream().map(cloudModuleAuthor -> String.format("%s(%s)",
                                                                            cloudModuleAuthor.getName(),
                                                                            cloudModuleAuthor.getRole())).collect(Collectors.joining(","));
    }

    /**
     * @param file sets the running modules jar
     */
    public void setFile(@NotNull Path file) {
        this.file = file;
    }
}
