/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.serverselectors.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitMobInitEvent;
import de.dytanic.cloudnet.bridge.internal.serverselectors.MobSelector;
import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.dytanic.cloudnet.bridge.internal.util.ReflectionUtil;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketInMobSelector extends PacketInHandlerDefault {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        Map<UUID, ServerMob> mobMap = data.getObject("mobs", new TypeToken<Map<UUID, ServerMob>>() {}.getType());
        MobConfig mobConfig = data.getObject("mobConfig", new TypeToken<MobConfig>() {}.getType());

        Map<UUID, ServerMob> filteredMobs = MapWrapper.filter(mobMap, new Acceptable<ServerMob>() {
            @Override
            public boolean isAccepted(ServerMob value) {
                return value.getPosition().getGroup().equalsIgnoreCase(CloudAPI.getInstance().getGroup());
            }
        });

        if (MobSelector.getInstance() != null) {
            Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
                @Override
                public void run() {
                    MobSelector.getInstance().shutdown();
                    MobSelector.getInstance().setMobConfig(mobConfig);
                    MobSelector.getInstance().setMobs(new HashMap<>());
                    MobSelector.getInstance().setMobs(MapWrapper.transform(filteredMobs, new Catcher<UUID, UUID>() {
                        @Override
                        public UUID doCatch(UUID key) {
                            return key;
                        }
                    }, new Catcher<MobSelector.MobImpl, ServerMob>() {

                        @Override
                        public MobSelector.MobImpl doCatch(ServerMob key) {
                            MobSelector.getInstance().toLocation(key.getPosition()).getChunk().load();
                            Entity entity = MobSelector.getInstance()
                                                       .toLocation(key.getPosition())
                                                       .getWorld()
                                                       .spawnEntity(MobSelector.getInstance().toLocation(key.getPosition()),
                                                                    EntityType.valueOf(key.getType()));
                            entity.setFireTicks(0);
                            Object armorStand = ReflectionUtil.armorstandCreation(MobSelector.getInstance().toLocation(key.getPosition()),
                                                                                  entity,
                                                                                  key);

                            if (armorStand != null) {
                                MobSelector.getInstance().updateCustom(key, armorStand);
                                Entity armor = (Entity) armorStand;
                                if (armor.getPassenger() == null && key.getItemId() != null) {

                                    Material material = ItemStackBuilder.getMaterialIgnoreVersion(key.getItemName(), key.getItemId());
                                    if (material != null) {
                                        Item item = Bukkit.getWorld(key.getPosition().getWorld()).dropItem(armor.getLocation(),
                                                                                                           new ItemStack(material));
                                        item.setTicksLived(Integer.MAX_VALUE);
                                        item.setPickupDelay(Integer.MAX_VALUE);
                                        armor.setPassenger(item);
                                    }
                                }
                            }

                            if (entity instanceof Villager) {
                                ((Villager) entity).setProfession(Villager.Profession.FARMER);
                            }

                            MobSelector.getInstance().unstableEntity(entity);
                            entity.setCustomNameVisible(true);
                            entity.setCustomName(ChatColor.translateAlternateColorCodes('&', key.getDisplay()));
                            MobSelector.MobImpl mob = new MobSelector.MobImpl(key.getUniqueId(),
                                                                              key,
                                                                              entity,
                                                                              MobSelector.getInstance().create(mobConfig, key),
                                                                              new HashMap<>(),
                                                                              armorStand);
                            Bukkit.getPluginManager().callEvent(new BukkitMobInitEvent(mob));
                            return mob;
                        }
                    }));
                    Bukkit.getScheduler().runTaskAsynchronously(CloudServer.getInstance().getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            for (ServerInfo serverInfo : MobSelector.getInstance().getServers().values()) {
                                MobSelector.getInstance().handleUpdate(serverInfo);
                            }
                        }
                    });
                }
            });

        } else {
            MobSelector mobSelector = new MobSelector(mobConfig);
            MobSelector.getInstance().setMobs(new HashMap<>());
            Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
                @Override
                public void run() {
                    MobSelector.getInstance().setMobs(MapWrapper.transform(filteredMobs, new Catcher<UUID, UUID>() {
                        @Override
                        public UUID doCatch(UUID key) {
                            return key;
                        }
                    }, new Catcher<MobSelector.MobImpl, ServerMob>() {
                        @Override
                        public MobSelector.MobImpl doCatch(ServerMob key) {
                            MobSelector.getInstance().toLocation(key.getPosition()).getChunk().load();
                            Entity entity = MobSelector.getInstance()
                                                       .toLocation(key.getPosition())
                                                       .getWorld()
                                                       .spawnEntity(MobSelector.getInstance().toLocation(key.getPosition()),
                                                                    EntityType.valueOf(key.getType()));
                            Object armorStand = ReflectionUtil.armorstandCreation(MobSelector.getInstance().toLocation(key.getPosition()),
                                                                                  entity,
                                                                                  key);

                            if (armorStand != null) {
                                MobSelector.getInstance().updateCustom(key, armorStand);
                                Entity armor = (Entity) armorStand;
                                if (armor.getPassenger() == null && key.getItemId() != null) {
                                    Material material = ItemStackBuilder.getMaterialIgnoreVersion(key.getItemName(), key.getItemId());
                                    if (material != null) {
                                        Item item = Bukkit.getWorld(key.getPosition().getWorld()).dropItem(armor.getLocation(),
                                                                                                           new ItemStack(material));
                                        item.setTicksLived(Integer.MAX_VALUE);
                                        item.setPickupDelay(Integer.MAX_VALUE);
                                        armor.setPassenger(item);
                                    }
                                }
                            }

                            if (entity instanceof Villager) {
                                ((Villager) entity).setProfession(Villager.Profession.FARMER);
                            }

                            MobSelector.getInstance().unstableEntity(entity);
                            entity.setCustomNameVisible(true);
                            entity.setCustomName(ChatColor.translateAlternateColorCodes('&', key.getDisplay() + NetworkUtils.EMPTY_STRING));

                            MobSelector.MobImpl mob = new MobSelector.MobImpl(key.getUniqueId(),
                                                                              key,
                                                                              entity,
                                                                              MobSelector.getInstance().create(mobConfig, key),
                                                                              new HashMap<>(),
                                                                              armorStand);
                            Bukkit.getPluginManager().callEvent(new BukkitMobInitEvent(mob));
                            return mob;
                        }
                    }));
                }
            });
            mobSelector.init();
        }
    }
}
