package eu.cloudnetservice.v2.help;

import eu.cloudnetservice.v2.lib.map.Maps;

/**
 * Class to organize help information and print it in a pretty way
 */
public final class HelpService {

    private final Maps.ArrayMap<String, ServiceDescription> descriptions = new Maps.ArrayMap<>();

    /**
     * Print the help directly to {@link System#out}
     */
    public void describe() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Help service of the Cloud:").append(Character.LINE_SEPARATOR);
        descriptions.forEach((key, value) -> {
            stringBuilder.append(key).append(':').append(Character.LINE_SEPARATOR);
            for (ServiceDescription description : value) {
                stringBuilder.append("Usage: ")
                             .append(description.getUsage())
                             .append(Character.LINE_SEPARATOR)
                             .append("Description: ")
                             .append(description.getDescription())
                             .append(Character.LINE_SEPARATOR)
                             .append(Character.LINE_SEPARATOR);
            }
        });
        return stringBuilder.toString();
    }

    public Maps.ArrayMap<String, ServiceDescription> getDescriptions() {
        return descriptions;
    }
}
