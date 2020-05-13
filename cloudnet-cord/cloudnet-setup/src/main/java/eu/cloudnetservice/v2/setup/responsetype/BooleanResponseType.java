package eu.cloudnetservice.v2.setup.responsetype;

import eu.cloudnetservice.v2.lib.utility.document.Document;
import eu.cloudnetservice.v2.setup.SetupResponseType;

import java.util.regex.Pattern;

public class BooleanResponseType implements SetupResponseType<Boolean> {
    private static final Pattern FALSE_PATTERN = Pattern.compile("(false|n(o)?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TRUE_PATTERN = Pattern.compile("(true|y(es)?)", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isValidInput(final String input) {
        return TRUE_PATTERN.matcher(input).matches() || FALSE_PATTERN.matcher(input).matches();
    }

    @Override
    public Boolean getValue(final String input) {
        return TRUE_PATTERN.matcher(input).matches();
    }

    @Override
    public String userFriendlyString() {
        return "boolean (y/yes or n/no)";
    }

    @Override
    public void appendDocument(final Document document, final String name, final String input) {
        document.append(name, getValue(input));
    }
}
