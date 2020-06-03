package eu.cloudnetservice.cloudnet.v2.master.module.exception;

import java.io.IOException;

public final class ModuleDescriptionFileNotFoundException extends IOException {

    public ModuleDescriptionFileNotFoundException() {
        super();
    }

    public ModuleDescriptionFileNotFoundException(String message) {
        super(message);
    }

    public ModuleDescriptionFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleDescriptionFileNotFoundException(Throwable cause) {
        super(cause);
    }
}
