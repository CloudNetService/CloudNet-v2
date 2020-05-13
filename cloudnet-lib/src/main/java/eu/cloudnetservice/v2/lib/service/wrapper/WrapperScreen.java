package eu.cloudnetservice.v2.lib.service.wrapper;

/**
 * Created by Tareko on 23.09.2017.
 */
public class WrapperScreen {

    private final String wrapperId;

    private final String consoleLine;

    public WrapperScreen(String wrapperId, String consoleLine) {
        this.wrapperId = wrapperId;
        this.consoleLine = consoleLine;
    }

    public String getWrapperId() {
        return wrapperId;
    }

    public String getConsoleLine() {
        return consoleLine;
    }
}