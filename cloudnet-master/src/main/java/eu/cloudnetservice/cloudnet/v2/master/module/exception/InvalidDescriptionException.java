package eu.cloudnetservice.cloudnet.v2.master.module.exception;

public final class InvalidDescriptionException extends RuntimeException {

    public InvalidDescriptionException() { }

    public InvalidDescriptionException(final String message) {
        super(message);
    }
}
