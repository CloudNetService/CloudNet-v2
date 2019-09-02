package de.dytanic.cloudnet.lib.server.priority;

/**
 * Created by Tareko on 18.07.2017.
 */
public class PriorityService {

    private int stopTimeInSeconds;

    private PriorityConfig global;

    private PriorityConfig group;

    public PriorityService(int stopTimeInSeconds, PriorityConfig global, PriorityConfig group) {
        this.stopTimeInSeconds = stopTimeInSeconds;
        this.global = global;
        this.group = group;
    }

    public int getStopTimeInSeconds() {
        return stopTimeInSeconds;
    }

    public PriorityConfig getGlobal() {
        return global;
    }

    public PriorityConfig getGroup() {
        return group;
    }
}