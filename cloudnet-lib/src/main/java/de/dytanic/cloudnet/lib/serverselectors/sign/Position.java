/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.serverselectors.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 26.05.2017.
 */
@AllArgsConstructor
@Getter
public class Position {

    private String group;
    private String world;
    private double x;
    private double y;
    private double z;

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Position)) return false;
        Position signPosition = (Position) obj;

        if (signPosition.x == x
                && signPosition.y == y &&
                signPosition.z == z && signPosition.world.equals(world) &&
                signPosition.group.equals(group)) {
            return true;
        }

        return false;
    }
}
