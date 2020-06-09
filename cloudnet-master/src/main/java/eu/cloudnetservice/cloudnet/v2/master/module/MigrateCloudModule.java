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
