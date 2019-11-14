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
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketInMobSelector extends PacketInHandlerDefault {

    private static final Type UUID_SERVERMOB_MAP_TYPE = TypeToken.getParameterized(Map.class, UUID.class, ServerMob.class).getType();
    private static final Type MOBCONFIG_TYPE = TypeToken.get(MobConfig.class).getType();

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        Map<UUID, ServerMob> mobMap = data.getObject("mobs", UUID_SERVERMOB_MAP_TYPE);
        MobConfig mobConfig = data.getObject("mobConfig", MOBCONFIG_TYPE);


        Map<UUID, ServerMob> filteredMobs = MapWrapper.filter(mobMap, new Acceptable<ServerMob>() {
            @Override
            public boolean isAccepted(ServerMob value) {
                return value.getPosition().getGroup().equalsIgnoreCase(CloudAPI.getInstance().getGroup());
            }
        });

        if (MobSelector.getInstance() != null) {
            Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), () -> {
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
                        MobSelector.toLocation(key.getPosition()).getChunk().load();
                        LivingEntity entity = (LivingEntity) MobSelector
                            .toLocation(key.getPosition())
                            .getWorld()
                            .spawnEntity(MobSelector.toLocation(key.getPosition()),
                                         EntityType.valueOf(key.getType()));
                        entity.setFireTicks(0);
                        Entity armorStand = ReflectionUtil.armorStandCreation(MobSelector.toLocation(key.getPosition()),
                                                                              entity,
                                                                              key);

                        if (armorStand != null) {
                            MobSelector.getInstance().updateCustom(key, armorStand);
                            Entity armor = armorStand;
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
                            MobSelector.toLocation(key.getPosition()).getChunk().load();
                            LivingEntity entity = (LivingEntity) MobSelector
                                .toLocation(key.getPosition())
                                .getWorld()
                                .spawnEntity(MobSelector.toLocation(key.getPosition()),
                                             EntityType.valueOf(key.getType()));
                            Entity armorStand = ReflectionUtil.armorStandCreation(MobSelector.toLocation(key.getPosition()),
                                                                                  entity,
                                                                                  key);

                            if (armorStand != null) {
                                MobSelector.getInstance().updateCustom(key, armorStand);
                                Entity armor = armorStand;
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
