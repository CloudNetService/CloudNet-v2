package de.dytanic.cloudnet.setup.models;

public class PaperMCProjectBuilds {

    private final String latest;
    private final String[] all;

    public PaperMCProjectBuilds(String latest, String[] all) {
        this.latest = latest;
        this.all = all;
    }

    public String getLatest() {
        return latest;
    }

    public String[] getAll() {
        return all;
    }
}
