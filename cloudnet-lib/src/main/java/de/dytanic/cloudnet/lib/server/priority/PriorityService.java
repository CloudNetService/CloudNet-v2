package de.dytanic.cloudnet.lib.server.priority;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 18.07.2017.
 */
@Getter
@AllArgsConstructor
public class PriorityService {

    private int stopTimeInSeconds;

    private PriorityConfig global;

    private PriorityConfig group;

}