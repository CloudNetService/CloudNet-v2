package eu.cloudnetservice.cloudnet.v2.master.module.model;

import com.google.gson.reflect.TypeToken;
import com.vdurmont.semver4j.Semver;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.module.exception.InvalidDescriptionException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class CloudModuleDescriptionFile {

    private String main;
    private Semver version;
    private String name;
    private String groupId;
    private String updateUrl;

    private String description;
    private String website;

    private Set<CloudModuleDependency> dependencies;
    private Set<CloudModuleAuthor> authors;

    private Set<CloudModulePlugin> plugins;

    //transient allows to use this constant only on runtime and was not saved into a config
    private transient Path file;

    public CloudModuleDescriptionFile(InputStream stream) {
        loadJson(stream);
    }

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
        this.version = new Semver(version);
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

    private void loadJson(InputStream stream) {
        CloudModuleDescriptionFile thisClazz = Document.GSON.fromJson(new InputStreamReader(stream), TypeToken.get(CloudModuleDescriptionFile.class).getType());
        this.main = thisClazz.main;
        this.version = thisClazz.version;
        this.name = thisClazz.name;
        this.groupId = thisClazz.groupId;
        this.updateUrl = thisClazz.updateUrl;
        this.description = thisClazz.description;
        this.website = thisClazz.website;
        this.dependencies = thisClazz.dependencies;
        this.authors = thisClazz.authors;
        this.plugins = thisClazz.plugins;
    }

    public Path getFile() {
        return file;
    }

    public Set<CloudModulePlugin> getPlugins() {
        return plugins;
    }

    public String getMain() {
        return main;
    }

    public Semver getVersion() {
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

    public Set<CloudModuleDependency> getDependencies() {
        return dependencies;
    }

    public Set<CloudModuleAuthor> getAuthors() {
        return authors;
    }

    public String getAuthorsAsString() {
        return getAuthors().stream().map(cloudModuleAuthor -> String.format("%s(%s)", cloudModuleAuthor.getName(),cloudModuleAuthor.getRole())).collect(Collectors.joining(","));
    }

    public void setFile(Path file) {
        if (file == null) {
            throw new InvalidDescriptionException("The given path can not be null for the description file");
        }
        this.file = file;
    }
}
