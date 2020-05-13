package eu.cloudnetservice.v2.help;

/**
 * Class to store a brief and a detailed usage description.
 */
public class ServiceDescription {

    /**
     * Brief usage description.
     */
    private final String usage;

    /**
     * Detailed description.
     */
    private final String description;

    public ServiceDescription(String usage, String description) {
        this.usage = usage;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }
}
