package eu.cloudnetservice.cloudnet.v2.master.module.exception;

import java.io.IOException;

/**
 * This error is thrown if no module json is found
 */
public final class ModuleDescriptionFileNotFoundException extends IOException {

    public ModuleDescriptionFileNotFoundException(String message) {
        super(message);
    }

}
