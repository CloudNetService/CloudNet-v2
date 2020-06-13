package eu.cloudnetservice.cloudnet.v2.setup.responsetype;

import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.setup.SetupResponseType;

import java.util.regex.Pattern;

public class StringResponseType implements SetupResponseType<String> {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final StringResponseType INSTANCE = new StringResponseType();

    public static StringResponseType getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValidInput(final String input) {
        return !input.isEmpty() && !WHITESPACE.matcher(input).matches();
    }

    @Override
    public String getValue(final String input) {
        return input;
    }

    @Override
    public String userFriendlyString() {
        return "string";
    }

    @Override
    public void appendDocument(final Document document, final String name, final String input) {
        document.append(name, getValue(input));
    }
}
