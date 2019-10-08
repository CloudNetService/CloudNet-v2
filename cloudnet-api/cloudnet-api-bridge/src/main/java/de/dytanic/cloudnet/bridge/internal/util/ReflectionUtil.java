/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.util;

import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
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
            String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
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
            String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            return Class.forName("net.minecraft.server." + version + suffix);
        } catch (Exception ex) {
            try {
                return Class.forName("net.minecraft.server" + suffix);
            } catch (ClassNotFoundException e) {
            }
        }
        return null;
    }

    public static Object armorstandCreation(Location location, Entity entity, ServerMob serverMob) {
        try {
            Object armorstand = entity.getWorld().spawnEntity(entity.getLocation()
                                                                    .clone()
                                                                    .add(0,
                                                                         ((LivingEntity) entity).getEyeHeight() - (entity instanceof Wither ? 0.15 : 0.3),
                                                                         0), EntityType.valueOf("ARMOR_STAND"));

            armorstand.getClass().getMethod("setVisible", boolean.class).invoke(armorstand, false);
            armorstand.getClass().getMethod("setCustomNameVisible", boolean.class).invoke(armorstand, true);
            armorstand.getClass().getMethod("setGravity", boolean.class).invoke(armorstand, false);
            armorstand.getClass().getMethod("setBasePlate", boolean.class).invoke(armorstand, false);
            armorstand.getClass().getMethod("setSmall", boolean.class).invoke(armorstand, true);
            armorstand.getClass().getMethod("setCanPickupItems", boolean.class).invoke(armorstand, false);
            return armorstand;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
