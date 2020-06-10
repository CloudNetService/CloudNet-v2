package eu.cloudnetservice.cloudnet.v2.master.module;

/**
 * This class allows a car to make updates
 */
public interface UpdateCloudModule {

    /**
     * This method is called when an update check is performed
     * @param url is read from the module file
     * @return returns true if the update check was successful
     */
    boolean update(String url);

}
