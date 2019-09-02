/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.serverselectors.sign;

/**
 * Created by Tareko on 26.05.2017.
 */
public class Position {

    private String group;
    private String world;
    private double x;
    private double y;
    private double z;

    public Position(String group, String world, double x, double y, double z) {
        this.group = group;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public String getGroup() {
        return group;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Position)) {
            return false;
        }
        Position signPosition = (Position) obj;

        if (signPosition.x == x && signPosition.y == y && signPosition.z == z && signPosition.world.equals(world) && signPosition.group.equals(
            group)) {
            return true;
        }

        return false;
    }
}
