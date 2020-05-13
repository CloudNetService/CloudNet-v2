package eu.cloudnetservice.v2.modules.exception;

import eu.cloudnetservice.v2.modules.Module;

/**
 * Runtime exception that should be thrown when loading a {@link Module}.
 */
public class ModuleLoadException extends RuntimeException {

    /**
     * Constructs a new runtime exception with a given message.
     *
     * @param message the message that details the exception
     */
    public ModuleLoadException(String message) {
        super(message);
    }
}
