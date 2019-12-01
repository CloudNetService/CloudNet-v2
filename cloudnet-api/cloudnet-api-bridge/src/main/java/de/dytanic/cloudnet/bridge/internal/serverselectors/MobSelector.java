/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.serverselectors;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.adapter.NetworkHandlerAdapter;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitMobInitEvent;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitMobUpdateEvent;
import de.dytanic.cloudnet.bridge.internal.listener.v18_112.ArmorStandListener;
import de.dytanic.cloudnet.bridge.internal.serverselectors.listeners.MobListener;
import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.dytanic.cloudnet.bridge.internal.util.ReflectionUtil;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobConfig;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobItemLayout;
import de.dytanic.cloudnet.lib.serverselectors.mob.MobPosition;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 25.08.2017.
 */
public final class MobSelector {

    private static MobSelector instance;

    private Map<UUID, Mob> mobs;

    private MobConfig mobConfig;

    private Map<String, ServerInfo> servers = new ConcurrentHashMap<>();

    public MobSelector(MobConfig mobConfig) {
        instance = this;
        this.mobConfig = mobConfig;
    }

    public static MobSelector getInstance() {
        return instance;
    }

    public Mob spawnMob(final MobConfig mobConfig, final UUID uuid, final ServerMob serverMob) {
        Location location = toLocation(serverMob.getPosition());

        if (!location.getChunk().isLoaded()) {
            location.getChunk().load();
        }

        Entity entity = location.getWorld().spawnEntity(
            location,
            EntityType.valueOf(serverMob.getType())
        );

        if (!(entity instanceof LivingEntity)) {
            return null;
        }
        entity.setFireTicks(0);

        final LivingEntity livingEntity = (LivingEntity) entity;
        ArmorStand armorStand = ReflectionUtil.armorStandCreation(
            location,
            livingEntity,
            serverMob
        );

        if (armorStand == null) {
            return null;
        }

        updateCustom(serverMob, armorStand);

        if (armorStand.getPassenger() == null && serverMob.getItemId() != null) {
            Material material = ItemStackBuilder.getMaterialIgnoreVersion(serverMob.getItemName(), serverMob.getItemId());
            if (material != null) {
                Item item = location.getWorld().dropItem(armorStand.getLocation(), new ItemStack(material));
                item.setTicksLived(Integer.MAX_VALUE);
                item.setPickupDelay(Integer.MAX_VALUE);
                armorStand.setPassenger(item);
            }
        }

        if (entity instanceof Villager) {
            ((Villager) entity).setProfession(Villager.Profession.FARMER);
        }

        unstableEntity(entity);
        entity.setCustomNameVisible(true);
        entity.setCustomName(ChatColor.translateAlternateColorCodes('&', serverMob.getDisplay()));


        Mob mob = new Mob(
            uuid,
            serverMob,
            entity,
            createInventory(mobConfig, serverMob),
            Collections.emptyMap(),
            armorStand
        );

        Bukkit.getPluginManager().callEvent(
            new BukkitMobInitEvent(mob)
        );
        return mob;
    }

    public void updateCustom(ServerMob serverMob, ArmorStand armorStand) {
        OnlineCount onlineCount = getOnlineCount(serverMob.getTargetGroup());
        if (armorStand != null) {

            armorStand.setCustomName(
                ChatColor.translateAlternateColorCodes('&', serverMob.getDisplayMessage() + NetworkUtils.EMPTY_STRING)
                         .replace("%max_players%", onlineCount.getMaxPlayers() + NetworkUtils.EMPTY_STRING)
                         .replace("%group%", serverMob.getTargetGroup())
                         .replace("%group_online%", onlineCount.getOnlineCount() + NetworkUtils.EMPTY_STRING));
        }
    }

    public OnlineCount getOnlineCount(String group) {
        int onlineCount = 0;
        int maxPlayers = 0;
        for (ServerInfo serverInfo : this.servers.values()) {
            if (serverInfo.getServiceId().getGroup().equalsIgnoreCase(group)) {
                onlineCount += serverInfo.getOnlineCount();
                maxPlayers += serverInfo.getMaxPlayers();
            }
        }
        return new OnlineCount(onlineCount, maxPlayers);
    }

    public MobConfig getMobConfig() {
        return mobConfig;
    }

    public void setMobConfig(MobConfig mobConfig) {
        this.mobConfig = mobConfig;
    }

    public Map<String, ServerInfo> getServers() {
        return servers;
    }

    public Map<UUID, Mob> getMobs() {
        return mobs;
    }

    public Inventory createInventory(MobConfig mobConfig, ServerMob mob) {
        Inventory inventory = Bukkit.createInventory(null,
                                                     mobConfig.getInventorySize(),
                                                     ChatColor.translateAlternateColorCodes('&',
                                                                                            mob.getDisplay() + NetworkUtils.SPACE_STRING));

        for (Map.Entry<Integer, MobItemLayout> mobItem : mobConfig.getDefaultItemInventory().entrySet()) {
            inventory.setItem(mobItem.getKey() - 1, transform(mobItem.getValue()));
        }
        return inventory;
    }

    public void init() {
        CloudAPI.getInstance().getNetworkHandlerProvider().registerHandler(new NetworkHandlerAdapterImplx());

        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), () -> {
            CloudAPI.getInstance().getServers().forEach(
                serverInfo -> this.servers.put(serverInfo.getServiceId().getServerId(), serverInfo)
            );
            Bukkit.getScheduler().runTaskAsynchronously(CloudServer.getInstance().getPlugin(), () -> {
                for (ServerInfo serverInfo : this.servers.values()) {
                    handleUpdate(serverInfo);
                }
            });
        });

        Bukkit.getPluginManager().registerEvents(new ArmorStandListener(), CloudServer.getInstance().getPlugin());
        Bukkit.getPluginManager().registerEvents(new MobListener(this), CloudServer.getInstance().getPlugin());
    }

    public void setMobs(Map<UUID, Mob> mobs) {
        this.mobs = mobs;
    }

    private ItemStack transform(MobItemLayout mobItemLayout, ServerInfo serverInfo) {
        Material material = ItemStackBuilder.getMaterialIgnoreVersion(mobItemLayout.getItemName(), mobItemLayout.getItemId());
        if (material == null) {
            return null;
        } else {
            return ItemStackBuilder.builder(material, 1, mobItemLayout.getSubId()).lore(
                mobItemLayout.getLore().stream()
                             .map(lore -> initPatterns(ChatColor.translateAlternateColorCodes('&', lore), serverInfo))
                             .collect(Collectors.toList())
            ).displayName(initPatterns(ChatColor.translateAlternateColorCodes('&', mobItemLayout.getDisplay()), serverInfo)).build();
        }
    }

    public void handleUpdate(ServerInfo serverInfo) {
        if (serverInfo.getServiceId().getGroup() == null) {
            return;
        }

        for (Mob mob : this.mobs.values()) {
            if (mob.getMob().getTargetGroup().equals(serverInfo.getServiceId().getGroup())) {
                mob.getEntity().setTicksLived(Integer.MAX_VALUE);
                updateCustom(mob.getMob(), mob.getDisplayMessage());
                Bukkit.getPluginManager().callEvent(new BukkitMobUpdateEvent(mob.getMob()));

                mob.getServerPosition().clear();
                Collection<ServerInfo> serverInfos = getServersOfGroup(serverInfo.getServiceId().getGroup());

                int index = 0;
                for (ServerInfo server : serverInfos) {
                    if (server.isOnline() && server.getServerState().equals(ServerState.LOBBY) &&
                        !server.getServerConfig().isHideServer() &&
                        !server.getServerConfig().getProperties().contains(NetworkUtils.DEV_PROPERTY)) {
                        while (mobConfig.getDefaultItemInventory().containsKey(index + 1)) {
                            ++index;
                        }

                        if ((mobConfig.getInventorySize() - 1) <= index) {
                            break;
                        }

                        final int value = index;
                        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), () -> {
                            mob.getInventory().setItem(value, transform(mobConfig.getItemLayout(), server));
                            mob.getServerPosition().put(value, server.getServiceId().getServerId());
                        });
                        ++index;
                    }
                }

                while (index < mob.getInventory().getSize()) {
                    if (!mobConfig.getDefaultItemInventory().containsKey(index + 1)) {
                        mob.getInventory().setItem(index, new ItemStack(Material.AIR));
                    }
                    ++index;
                }
            }
        }
    }

    private String initPatterns(String x, ServerInfo serverInfo) {
        return x.replace("%server%", serverInfo.getServiceId().getServerId())
                .replace("%id%",
                         serverInfo.getServiceId()
                                   .getId() + NetworkUtils.EMPTY_STRING)
                .replace("%host%", serverInfo.getHost())
                .replace("%port%", serverInfo.getPort() + NetworkUtils.EMPTY_STRING)
                .replace("%memory%", serverInfo.getMemory() + "MB")
                .replace("%online_players%", serverInfo.getOnlineCount() + NetworkUtils.EMPTY_STRING)
                .replace("%max_players%", serverInfo.getMaxPlayers() + NetworkUtils.EMPTY_STRING)
                .replace("%motd%", ChatColor.translateAlternateColorCodes('&', serverInfo.getMotd()))
                .replace("%state%", serverInfo.getServerState().name() + NetworkUtils.EMPTY_STRING)
                .replace("%wrapper%", serverInfo.getServiceId().getWrapperId() + NetworkUtils.EMPTY_STRING)
                .replace("%extra%", serverInfo.getServerConfig().getExtra())
                .replace("%template%", serverInfo.getTemplate().getName())
                .replace("%group%", serverInfo.getServiceId().getGroup());
    }

    private List<ServerInfo> getServersOfGroup(String group) {
        return servers.values().stream()
                      .filter(serverInfo -> serverInfo.getServiceId().getGroup().equals(group))
                      .collect(Collectors.toList());
    }

    public static Location toLocation(MobPosition position) {
        return new Location(Bukkit.getWorld(position.getWorld()),
                            position.getX(),
                            position.getY(),
                            position.getZ(),
                            position.getYaw(),
                            position.getPitch());
    }

    public static MobPosition toPosition(String group, Location location) {
        return new MobPosition(group,
                               location.getWorld().getName(),
                               location.getX(),
                               location.getY(),
                               location.getZ(),
                               location.getYaw(),
                               location.getPitch());
    }

    public List<ServerInfo> getServers(String group) {
        return getServersOfGroup(group);
    }

    private ItemStack transform(MobItemLayout mobItemLayout) {
        Material material = ItemStackBuilder.getMaterialIgnoreVersion(mobItemLayout.getItemName(), mobItemLayout.getItemId());
        if (material == null) {
            return null;
        } else {
            return ItemStackBuilder.builder(material, 1, mobItemLayout.getSubId()).lore(
                mobItemLayout.getLore().stream()
                             .map(lore -> ChatColor.translateAlternateColorCodes('&', lore))
                             .collect(Collectors.toList())
            ).displayName(ChatColor.translateAlternateColorCodes('&', mobItemLayout.getDisplay())).build();
        }
    }

    @Deprecated
    public void shutdown() {
        for (Mob mobImpl : this.mobs.values()) {
            if (mobImpl.getDisplayMessage() != null) {
                Entity entity = mobImpl.getDisplayMessage();
                if (entity.getPassenger() != null) {
                    entity.getPassenger().remove();
                }
                mobImpl.getDisplayMessage().remove();
            }
            mobImpl.getEntity().remove();
        }

        mobs.clear();
    }

    @Deprecated
    public void unstableEntity(Entity entity) {
        try {
            Class<?> nbt = ReflectionUtil.reflectNMSClazz(".NBTTagCompound");
            Class<?> entityClazz = ReflectionUtil.reflectNMSClazz(".Entity");
            Object object = nbt.newInstance();

            Object nmsEntity = entity.getClass().getMethod("getHandle", new Class[] {}).invoke(entity);
            try {
                entityClazz.getMethod("e", nbt).invoke(nmsEntity, object);
            } catch (Exception ex) {
                entityClazz.getMethod("save", nbt).invoke(nmsEntity, object);
            }

            object.getClass().getMethod("setInt", String.class, int.class).invoke(object, "NoAI", 1);
            object.getClass().getMethod("setInt", String.class, int.class).invoke(object, "Silent", 1);
            entityClazz.getMethod("f", nbt).invoke(nmsEntity, object);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            System.out.println("[CLOUD] Disabling NoAI and Silent support for " + entity.getEntityId());
            ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
        }
    }

    public Collection<Inventory> getInventories() {
        return this.mobs.values().stream().map(Mob::getInventory).collect(Collectors.toList());
    }

    public Mob findByInventory(Inventory inventory) {
        return this.mobs.values().stream()
                        .filter(mob -> mob.getInventory().equals(inventory))
                        .findFirst()
                        .orElse(null);
    }

    private class NetworkHandlerAdapterImplx extends NetworkHandlerAdapter {

        @Override
        public void onServerAdd(ServerInfo serverInfo) {
            servers.put(serverInfo.getServiceId().getServerId(), serverInfo);
            handleUpdate(serverInfo);
        }

        @Override
        public void onServerInfoUpdate(ServerInfo serverInfo) {
            servers.put(serverInfo.getServiceId().getServerId(), serverInfo);
            handleUpdate(serverInfo);
        }

        @Override
        public void onServerRemove(ServerInfo serverInfo) {
            servers.remove(serverInfo.getServiceId().getServerId());
            handleUpdate(serverInfo);
        }
    }

}
