package eu.cloudnetservice.v2.lib.proxylayout;

public class Motd {

    private final String firstLine;

    private final String secondLine;

    public Motd(String firstLine, String secondLine) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }
}