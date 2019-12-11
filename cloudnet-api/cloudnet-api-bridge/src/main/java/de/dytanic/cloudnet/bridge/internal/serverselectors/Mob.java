package de.dytanic.cloudnet.bridge.internal.serverselectors;

import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;

//MobImpl
public class Mob {

    private UUID uniqueId;

    private ServerMob mob;

    private Entity entity;

    private Inventory inventory;

    private Map<Integer, String> serverPosition;

    private ArmorStand displayMessage;

    public Mob(UUID uniqueId,
               ServerMob mob,
               Entity entity,
               Inventory inventory,
               Map<Integer, String> serverPosition,
               ArmorStand displayMessage) {
        this.uniqueId = uniqueId;
        this.mob = mob;
        this.entity = entity;
        this.inventory = inventory;
        this.serverPosition = serverPosition;
        this.displayMessage = displayMessage;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Map<Integer, String> getServerPosition() {
        return serverPosition;
    }

    public void setServerPosition(Map<Integer, String> serverPosition) {
        this.serverPosition = serverPosition;
    }

    public ArmorStand getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(ArmorStand displayMessage) {
        this.displayMessage = displayMessage;
    }

    public ServerMob getMob() {
        return mob;
    }

    public void setMob(ServerMob mob) {
        this.mob = mob;
    }
}
