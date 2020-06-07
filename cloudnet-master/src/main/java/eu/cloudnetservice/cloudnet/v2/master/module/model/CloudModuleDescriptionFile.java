package eu.cloudnetservice.cloudnet.v2.master.module.model;

import com.google.gson.reflect.TypeToken;
import com.vdurmont.semver4j.Semver;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.module.exception.InvalidDescriptionException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public final class CloudModuleDescriptionFile {

    private String main;
    private String version;
    private String name;
    private String groupId;
    private String updateUrl;

    private String description;
    private String website;

    private Set<CloudModuleDependency> dependencies;
    private Set<CloudModuleAuthor> authors;

    private Set<CloudModuleDeployFile> fileDeployment;

    //transient allows to use this constant only on runtime and was not saved into a config
    private transient Path file;
    private transient Semver semver;

    public static transient TypeToken<CloudModuleDescriptionFile> CLOUD_MODULE_DESCRIPTION_FILE = TypeToken.get(CloudModuleDescriptionFile.class);

    public CloudModuleDescriptionFile(InputStream stream, Path file) {
        loadJson(stream, file);
    }

    public CloudModuleDescriptionFile(String main,
                                      String version,
                                      String name,
                                      String groupId,
                                      String updateUrl,
                                      String description,
                                      String website,
                                      Set<CloudModuleDependency> dependencies,
                                      Set<CloudModuleAuthor> authors,
                                      Set<CloudModuleDeployFile> fileDeployment, Path file) {
        this.main = main;
        this.version = version;
        this.name = name;
        this.groupId = groupId;
        this.updateUrl = updateUrl;
        this.description = description;
        this.website = website;
        this.dependencies = dependencies;
        this.authors = authors;
        this.fileDeployment = fileDeployment;
        this.file = file;
        this.semver = new Semver(version, Semver.SemverType.NPM);
    }

    private void loadJson(InputStream stream, Path file) {
        CloudModuleDescriptionFile thisClazz = Document.GSON.fromJson(new InputStreamReader(stream), CLOUD_MODULE_DESCRIPTION_FILE.getType());
        this.main = thisClazz.main;
        this.version = thisClazz.version;
        this.name = thisClazz.name;
        this.groupId = thisClazz.groupId;
        this.updateUrl = thisClazz.updateUrl;
        this.description = thisClazz.description;
        this.website = thisClazz.website;
        this.dependencies = thisClazz.dependencies;
        this.authors = thisClazz.authors;
        this.fileDeployment = thisClazz.fileDeployment;
        this.semver = new Semver(version, Semver.SemverType.NPM);
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    public Set<CloudModuleDeployFile> getFileDeployment() {
        return fileDeployment;
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
