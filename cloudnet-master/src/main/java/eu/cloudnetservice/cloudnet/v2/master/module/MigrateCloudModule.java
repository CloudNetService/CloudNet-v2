package eu.cloudnetservice.cloudnet.v2.master.module;

public interface MigrateCloudModule {

    boolean migrate(String oldVersion, String newVersion);
}
