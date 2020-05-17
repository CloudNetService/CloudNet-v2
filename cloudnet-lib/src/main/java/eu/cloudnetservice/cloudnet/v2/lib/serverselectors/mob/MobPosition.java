package eu.cloudnetservice.cloudnet.v2.lib.serverselectors.mob;

/**
 * Created by Tareko on 02.09.2017.
 */
public class MobPosition {

    private final String group;

    private final String world;

    private final double x;

    private final double y;

    private final double z;

    private final float yaw;

    private final float pitch;

    public MobPosition(String group, String world, double x, double y, double z, float yaw, float pitch) {
        this.group = group;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String getGroup() {
        return group;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public String getWorld() {
        return world;
    }
}