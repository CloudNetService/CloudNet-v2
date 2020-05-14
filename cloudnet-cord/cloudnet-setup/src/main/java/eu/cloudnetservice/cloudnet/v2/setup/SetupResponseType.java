package eu.cloudnetservice.cloudnet.v2.setup;

import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Interface for setup response types.
 * Classes which implement this interface validate and parse the given
 * user input as a string into their respective value type.
 *
 * @param <T> The value type of the response.
 */
public interface SetupResponseType<T> {

    /**
     * Tests the given input for validity of the implemented value type.
     * Implementations should thoroughly check the input and determine, whether the input
     * is really valid and can be parsed using {@link #getValue(String)}.
     * @param input the user provided input.
     * @return whether or not the input is valid and can be parsed.
     */
    boolean isValidInput(String input);

    /**
     * Parses or otherwise converts the user provided input to the value type of the implementation.
     *
     * @param input the user provided input.
     * @return the parsed or converted value in the generic type of the implementation.
     */
    T getValue(String input);

    /**
     * Returns a user-friendly textual representation of the value type, such as
     * the user is able to understand the constraints of their input.
     * @return a user-friendly textual representation of the generic type.
     */
    String userFriendlyString();

    /**
     * Appends the value returned by {@link #getValue(String)} to the given document.
     *
     * @param document the document to append the value to.
     * @param name the name of the key that the value will be associated with.
     * @param input the user provided input that is converted to the value type.
     */
    default void appendDocument(Document document, String name, String input) {
        throw new UnsupportedOperationException("This response type does not support appendDocument");
    }
}
