package eu.cloudnetservice.v2.logging.handler;

/**
 * Interface for classes that handle console messages of the cloud.
 */
public interface ICloudLoggerHandler {

    /**
     * Handle console messages
     *
     * @param input the string that should be handled
     */
    void handleConsole(String input);

}
