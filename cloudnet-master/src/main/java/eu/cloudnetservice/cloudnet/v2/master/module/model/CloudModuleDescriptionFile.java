package eu.cloudnetservice.cloudnet.v2.master.module.model;

import java.nio.file.Path;
import java.util.HashSet;

public final class CloudModuleDescriptionFile {

    private final String main;
    private final String version;
    private final String name;
    private final String groupId;
    private final String updateUrl;

    private final String description;
    private final String website;

    private final HashSet<CloudModuleDependency> dependencies;
    private final HashSet<CloudModuleAuthor> authors;

    private final HashSet<CloudModulePlugin> plugins;

    private transient Path file;

    public CloudModuleDescriptionFile(String main,
                                      String version,
                                      String name,
                                      String groupId,
                                      String updateUrl,
                                      String description,
                                      String website,
                                      HashSet<CloudModuleDependency> dependencies,
                                      HashSet<CloudModuleAuthor> authors,
                                      HashSet<CloudModulePlugin> plugins, Path file) {
        this.main = main;
        this.version = version;
        this.name = name;
        this.groupId = groupId;
        this.updateUrl = updateUrl;
        this.description = description;
        this.website = website;
        this.dependencies = dependencies;
        this.authors = authors;
        this.plugins = plugins;
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    public HashSet<CloudModulePlugin> getPlugins() {
        return plugins;
    }

    public String getMain() {
        return main;
    }

    public String getVersion() {
        return version;
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

    public HashSet<CloudModuleDependency> getDependencies() {
        return dependencies;
    }

    public HashSet<CloudModuleAuthor> getAuthors() {
        return authors;
    }
}
