package eu.cloudnetservice.cloudnet.v2.master.module;

import com.vdurmont.semver4j.Semver;

public interface MigrateCloudModule {

    boolean migrate(Semver oldVersion, Semver newVersion);
}
