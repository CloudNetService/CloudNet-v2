package eu.cloudnetservice.v2.lib.serverselectors.sign;

import java.util.Objects;

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
    public int hashCode() {
        int result;
        long temp;
        result = group != null ? group.hashCode() : 0;
        result = 31 * result + (world != null ? world.hashCode() : 0);
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }

        final Position position = (Position) o;

        if (Double.compare(position.x, x) != 0) {
            return false;
        }
        if (Double.compare(position.y, y) != 0) {
            return false;
        }
        if (Double.compare(position.z, z) != 0) {
            return false;
        }
        if (!Objects.equals(group, position.group)) {
            return false;
        }
        return Objects.equals(world, position.world);
    }
}
