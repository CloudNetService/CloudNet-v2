package de.dytanic.cloudnet.setup.responsetype;

import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.setup.SetupResponseType;

import java.util.regex.Pattern;

public class IntegerResponseType implements SetupResponseType<Integer> {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[+\\-]?\\d+");
    private static final IntegerResponseType INSTANCE = new IntegerResponseType();

    public static IntegerResponseType getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isValidInput(final String input) {
        return NUMBER_PATTERN.matcher(input).matches();
    }

    @Override
    public Integer getValue(final String input) {
        return Integer.parseInt(input);
    }

    @Override
    public String userFriendlyString() {
        return "integer";
    }

    @Override
    public void appendDocument(final Document document, final String name, final String input) {
        document.append(name, getValue(input));
    }
}
