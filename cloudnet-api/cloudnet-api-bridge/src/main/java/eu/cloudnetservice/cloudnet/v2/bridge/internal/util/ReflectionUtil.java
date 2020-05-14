package eu.cloudnetservice.cloudnet.v2.bridge.internal.util;

import eu.cloudnetservice.cloudnet.v2.lib.serverselectors.mob.ServerMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wither;

/**
 * Created by Tareko on 17.08.2017.
 */
public final class ReflectionUtil {

    private ReflectionUtil() {
    }

    public static Class<?> reflectCraftClazz(String suffix) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            return Class.forName("org.bukkit.craftbukkit." + version + suffix);
        } catch (Exception ex) {
            try {
                return Class.forName("org.bukkit.craftbukkit" + suffix);
            } catch (ClassNotFoundException e) {
            }
        }
        return null;
    }

    public static Class<?> forName(String path) {
        try {
            return Class.forName(path);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> reflectNMSClazz(String suffix) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            return Class.forName("net.minecraft.server." + version + suffix);
        } catch (ClassNotFoundException ex) {
            try {
                return Class.forName("net.minecraft.server" + suffix);
            } catch (ClassNotFoundException e) {
            }
        }
        return null;
    }

    public static ArmorStand armorStandCreation(Location location, LivingEntity entity, ServerMob serverMob) {
        try {
            ArmorStand armorStand = (ArmorStand) entity.getWorld().spawnEntity(
                entity.getLocation().clone().add(0,
                                                 entity.getEyeHeight() - (entity instanceof Wither ? 0.15 : 0.3),
                                                 0),
                EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setCustomNameVisible(true);
            armorStand.setGravity(false);
            armorStand.setBasePlate(false);
            armorStand.setSmall(true);
            armorStand.setCanPickupItems(false);
            return armorStand;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
