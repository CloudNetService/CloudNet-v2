package eu.cloudnetservice.cloudnet.v2.master.module.exception;

public final class ModuleNotFoundException extends RuntimeException {

    public ModuleNotFoundException() {
        super();
    }

    public ModuleNotFoundException(final String message) {
        super(message);
    }

    public ModuleNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ModuleNotFoundException(final Throwable cause) {
        super(cause);
    }
}
