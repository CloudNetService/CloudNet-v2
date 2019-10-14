/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.serverselectors;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.adapter.NetworkHandlerAdapter;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.serverselectors.sign.*;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.*;


/**
 * Created by Tareko on 21.08.2017.
 */
public final class SignSelector implements Listener {

    private static SignSelector instance;
    private Map<UUID, Sign> signs;
    private volatile SignLayoutConfig signLayoutConfig;
    private Thread worker;
    private Map<String, ServerInfo> servers = NetworkUtils.newConcurrentHashMap();

    public SignSelector(Map<UUID, Sign> signs, SignLayoutConfig signLayoutConfig) {
        instance = this;
        this.signs = signs;
        this.signLayoutConfig = signLayoutConfig;

        Bukkit.getPluginManager().registerEvents(this, CloudServer.getInstance().getPlugin());
    }

    public static SignSelector getInstance() {
        return instance;
    }

    public Map<String, ServerInfo> getServers() {
        return servers;
    }

    public void setServers(Map<String, ServerInfo> servers) {
        this.servers = servers;
    }

    public Thread getWorker() {
        return worker;
    }

    public void setWorker(Thread worker) {
        this.worker = worker;
    }

    public Map<UUID, Sign> getSigns() {
        return signs;
    }

    public void setSigns(Map<UUID, Sign> signs) {
        this.signs = signs;
    }

    public SignLayoutConfig getSignLayoutConfig() {
        return signLayoutConfig;
    }

    public void setSignLayoutConfig(SignLayoutConfig signLayoutConfig) {
        this.signLayoutConfig = signLayoutConfig;
    }

    @Deprecated
    public void start() {
        CloudAPI.getInstance().getNetworkHandlerProvider().registerHandler(new NetworkHandlerAdapterImpl());
        worker = new ThreadImpl();
        worker.setDaemon(true);
        worker.start();

        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
            @Override
            public void run() {
                NetworkUtils.addAll(servers,
                                    MapWrapper.collectionCatcherHashMap(CloudAPI.getInstance().getServers(),
                                                                        new Catcher<String, ServerInfo>() {
                                                                            @Override
                                                                            public String doCatch(ServerInfo key) {
                                                                                return key.getServiceId().getServerId();
                                                                            }
                                                                        }));
            }
        });
    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent e) {
        if ((e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getClickedBlock() != null && e.getClickedBlock()
                                                                                                .getState() instanceof org.bukkit.block.Sign) {
            if (containsPosition(e.getClickedBlock().getLocation())) {
                Sign sign = getSignByPosition(e.getClickedBlock().getLocation());
                if (sign.getServerInfo() != null) {
                    String s = sign.getServerInfo().getServiceId().getServerId();
                    ByteArrayDataOutput output = ByteStreams.newDataOutput();
                    output.writeUTF("Connect");
                    output.writeUTF(s);
                    e.getPlayer().sendPluginMessage(CloudServer.getInstance().getPlugin(), "BungeeCord", output.toByteArray());
                }
            }
        }
    }

    public boolean containsPosition(Location location) {
        Position position = toPosition(location);
        for (Sign sign : signs.values()) {
            if (sign.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    public Sign getSignByPosition(Location location) {
        return CollectionWrapper.filter(signs.values(), new Acceptable<Sign>() {
            @Override
            public boolean isAccepted(Sign value) {
                return value.getPosition().equals(toPosition(location));
            }
        });
    }

    public Position toPosition(Location location) {
        return new Position(CloudAPI.getInstance().getGroup(),
                            location.getWorld().getName(),
                            location.getX(),
                            location.getY(),
                            location.getZ());
    }

    public boolean containsPosition(Position position) {
        for (Sign sign : signs.values()) {
            if (sign.getPosition().equals(position)) {
                return true;
            }
        }

        return false;
    }

    private Sign findFreeSign(String group) {
        return CollectionWrapper.filter(this.signs.values(), new Acceptable<Sign>() {
            @Override
            public boolean isAccepted(Sign value) {
                return value.getTargetGroup().equals(group) && value.getServerInfo() == null;
            }
        });
    }

    public Collection<String> freeServers(String group) {
        List<String> servers = new ArrayList<>();

        for (ServerInfo serverInfo : getServers(group)) {
            servers.add(serverInfo.getServiceId().getServerId());
        }

        for (Sign sign : signs.values()) {
            if (sign.getServerInfo() != null && servers.contains(sign.getServerInfo().getServiceId().getServerId())) {
                servers.remove(sign.getServerInfo().getServiceId().getServerId());
            }
        }

        List<String> x = new ArrayList<>();

        ServerInfo serverInfo;
        for (short i = 0; i < servers.size(); i++) {
            serverInfo = this.servers.get(servers.get(i));
            if (serverInfo != null) {
                if (!serverInfo.isOnline() || !serverInfo.getServerState().equals(ServerState.LOBBY) || serverInfo.getServerConfig()
                                                                                                                  .isHideServer() || serverInfo
                    .getMotd()
                    .contains("INGAME") || serverInfo.getMotd().contains("RUNNING") || serverInfo.getServerConfig().isHideServer()) {
                    x.add(serverInfo.getServiceId().getServerId());
                }
            } else {
                x.add(servers.get(i));
            }
        }

        for (String b : x) {
            servers.remove(b);
        }

        Collections.sort(servers);
        return servers;
    }

    private Collection<ServerInfo> getServers(String group) {
        return CollectionWrapper.filterMany(servers.values(), new Acceptable<ServerInfo>() {
            @Override
            public boolean isAccepted(ServerInfo value) {
                return value.getServiceId().getGroup().equals(group);
            }
        });
    }

    public Sign filter(ServerInfo serverInfo) {
        return CollectionWrapper.filter(signs.values(), new Acceptable<Sign>() {
            @Override
            public boolean isAccepted(Sign value) {
                return value.getServerInfo() != null && value.getServerInfo().getServiceId().getServerId().equals(serverInfo.getServiceId()
                                                                                                                            .getServerId());
            }
        });
    }

    public void sendUpdateSynchronized(Location location, String[] layout) {
        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) location.getBlock().getState();
        sign.setLine(0, layout[0]);
        sign.setLine(1, layout[1]);
        sign.setLine(2, layout[2]);
        sign.setLine(3, layout[3]);
        sign.update();
    }

    public Sign getSign(ServerInfo serverInfo) {
        return CollectionWrapper.filter(signs.values(), new Acceptable<Sign>() {
            @Override
            public boolean isAccepted(Sign value) {
                return value.getServerInfo() != null && value.getServerInfo().getServiceId().getServerId().equals(serverInfo.getServiceId()
                                                                                                                            .getServerId());
            }
        });
    }

    public boolean containsGroup(String group) {
        for (SignGroupLayouts signLayouts : signLayoutConfig.getGroupLayouts()) {
            if (signLayouts.getName().equalsIgnoreCase(group)) {
                return true;
            }
        }
        return false;
    }

    public void handleUpdate(Sign sign, ServerInfo serverInfo) {
        if (!exists(sign)) {
            return;
        }
        Location location = toLocation(sign.getPosition());
        SignLayout searchLayer = getSearchingLayout(((ThreadImpl) worker).animationTick);
        if (isMaintenance(sign.getTargetGroup())) {
            SignLayout _signLayout = getLayout(sign.getTargetGroup(), "maintenance");
            String[] layout = updateOfflineAndMaintenance(_signLayout.getSignLayout().clone(), sign);
            sign.setServerInfo(serverInfo);
            updateArray(layout, serverInfo);
            for (Player all : Bukkit.getOnlinePlayers()) {
                sendUpdate(all, location, layout);
            }
            sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
            changeBlock(location, _signLayout.getBlockName(), _signLayout.getBlockId(), _signLayout.getSubId());
            return;
        }

        if (serverInfo != null && serverInfo.isOnline() && !serverInfo.isIngame()) {
            if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo.getServerConfig()
                                                                                                                                .isHideServer()) {
                sign.setServerInfo(null);
                String[] layout = updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
                layout = updateOfflineAndMaintenance(layout, sign);
                for (Player all : Bukkit.getOnlinePlayers()) {
                    sendUpdate(all, location, layout);
                }
                sendUpdateSynchronizedTask(location, layout);
                return;
            }
            String[] layout;
            SignLayout signLayout;
            if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                signLayout = getLayout(sign.getTargetGroup(), "full");
                layout = signLayout.getSignLayout().clone();
            } else if (serverInfo.getOnlineCount() == 0) {
                signLayout = getLayout(sign.getTargetGroup(), "empty");
                layout = signLayout.getSignLayout().clone();
            } else {
                signLayout = getLayout(sign.getTargetGroup(), "online");
                layout = signLayout.getSignLayout().clone();
            }

            sign.setServerInfo(serverInfo);
            updateArray(layout, serverInfo);
            for (Player all : Bukkit.getOnlinePlayers()) {
                sendUpdate(all, location, layout);
            }
            sendUpdateSynchronizedTask(location, layout);
            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
        } else {
            sign.setServerInfo(null);
            String[] layout = updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
            for (Player all : Bukkit.getOnlinePlayers()) {
                sendUpdate(all, location, layout);
            }
            sendUpdateSynchronizedTask(location, layout);
        }
    }

    public boolean exists(Sign sign) {
        try {
            if (Bukkit.getWorld(sign.getPosition().getWorld()) != null) {
                Location location = toLocation(sign.getPosition());
                return location.getBlock().getState() instanceof org.bukkit.block.Sign;
            } else {
                return false;
            }
        } catch (Throwable ex) {
            return false;
        }
    }

    public Location toLocation(Position position) {
        return new Location(Bukkit.getWorld(position.getWorld()), position.getX(), position.getY(), position.getZ());
    }

    public SignLayout getSearchingLayout(int id) {
        for (SignLayout signLayout : signLayoutConfig.getSearchingAnimation().getSearchingLayouts()) {
            if (signLayout.getName().equals("loading" + id)) {
                return signLayout;
            }
        }
        return null;
    }

    public boolean isMaintenance(String group) {
        if (CloudAPI.getInstance().getServerGroupMap().containsKey(group)) {
            return CloudAPI.getInstance().getServerGroupMap().get(group).isMaintenance();
        } else {
            return true;
        }
    }

    public SignLayout getLayout(String group, String name) {
        SignGroupLayouts signGroupLayouts = getGroupLayout(group);
        if (signGroupLayouts == null) {
            signGroupLayouts = getGroupLayout("default");
        }
        return CollectionWrapper.filter(signGroupLayouts.getLayouts(), new Acceptable<SignLayout>() {
            @Override
            public boolean isAccepted(SignLayout value) {
                return value.getName().equals(name);
            }
        });
    }

    public String[] updateOfflineAndMaintenance(String[] value, Sign sign) {
        for (short i = 0; i < value.length; i++) {
            value[i] = ChatColor.translateAlternateColorCodes('&',
                                                              value[i].replace("%group%", sign.getTargetGroup())
                                                                      .replace("%from%", sign.getPosition().getGroup()));
        }
        return value;
    }

    public void updateArray(String[] value, ServerInfo serverInfo) {
        short i = 0;
        for (String x : value) {
            value[i] = ChatColor.translateAlternateColorCodes('&', x.replace("%server%",
                                                                             serverInfo.getServiceId()
                                                                                       .getServerId() + NetworkUtils.EMPTY_STRING)
                                                                    .replace("%id%",
                                                                             serverInfo.getServiceId().getId() + NetworkUtils.EMPTY_STRING)
                                                                    .replace("%host%", serverInfo.getHost())
                                                                    .replace("%port%",
                                                                             serverInfo.getPort() + NetworkUtils.EMPTY_STRING)
                                                                    .replace("%memory%", serverInfo.getMemory() + "MB")
                                                                    .replace("%online_players%",
                                                                             serverInfo.getOnlineCount() + NetworkUtils.EMPTY_STRING)
                                                                    .replace("%max_players%",
                                                                             serverInfo.getMaxPlayers() + NetworkUtils.EMPTY_STRING)
                                                                    .replace("%motd%",
                                                                             ChatColor.translateAlternateColorCodes('&',
                                                                                                                    serverInfo.getMotd()))
                                                                    .replace("%state%",
                                                                             serverInfo.getServerState().name() + NetworkUtils.EMPTY_STRING)
                                                                    .replace("%wrapper%",
                                                                             serverInfo.getServiceId()
                                                                                       .getWrapperId() + NetworkUtils.EMPTY_STRING)
                                                                    .replace("%extra%", serverInfo.getServerConfig().getExtra())
                                                                    .replace("%template%", serverInfo.getTemplate().getName())
                                                                    .replace("%group%", serverInfo.getServiceId().getGroup()));
            i++;
        }
    }

    public void sendUpdate(Player player, Location location, String[] layout) {
        if (player.getLocation().distance(location) < 32) {
            player.sendSignChange(location, layout);
        }
    }

    public void sendUpdateSynchronizedTask(Location location, String[] layout) {
        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
            @Override
            public void run() {
                org.bukkit.block.Sign sign = (org.bukkit.block.Sign) location.getBlock().getState();
                sign.setLine(0, layout[0]);
                sign.setLine(1, layout[1]);
                sign.setLine(2, layout[2]);
                sign.setLine(3, layout[3]);
                sign.update();
            }
        });
    }

    public void changeBlock(Location location, String blockName, int blockId, int subId) {
        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), () -> {
            Material material = ItemStackBuilder.getMaterialIgnoreVersion(blockName, blockId);
            BlockState signBlockState = location.getBlock().getState();

            if (material != null && subId != -1 && signBlockState instanceof org.bukkit.block.Sign) {
                MaterialData materialData = signBlockState.getData();

                if (materialData instanceof org.bukkit.material.Sign) { // this will return false in newer 1.14 spigot versions, even if it's a sign
                    org.bukkit.material.Sign materialSign = (org.bukkit.material.Sign) materialData;
                    if (materialSign.isWallSign()) {
                        Block backBlock = location.getBlock().getRelative(materialSign.getAttachedFace());
                        BlockState blockState = backBlock.getState();
                        blockState.setType(material);
                        blockState.setData(new MaterialData(material, (byte) subId));
                        blockState.update(true);
                    }
                }
            }
        });
    }

    public SignGroupLayouts getGroupLayout(String group) {
        return CollectionWrapper.filter(signLayoutConfig.getGroupLayouts(), new Acceptable<SignGroupLayouts>() {
            @Override
            public boolean isAccepted(SignGroupLayouts value) {
                return value.getName().equals(group);
            }
        });
    }

    private class ThreadImpl extends Thread {

        int animationTick = 1;
        private boolean valueTick = false;

        @Override
        public void run() {
            while (!isInterrupted()) {
                if (signLayoutConfig != null && signLayoutConfig.isKnockbackOnSmallDistance()) {
                    try {
                        for (Sign sign : signs.values()) {
                            if (Bukkit.getWorld(sign.getPosition().getWorld()) != null) {
                                Location location = SignSelector.this.toLocation(sign.getPosition());
                                for (Entity entity : location.getWorld().getNearbyEntities(location,
                                                                                           signLayoutConfig.getDistance(),
                                                                                           signLayoutConfig.getDistance(),
                                                                                           signLayoutConfig.getDistance())) {
                                    if (entity instanceof Player && !entity.hasPermission("cloudnet.signs.knockback.bypass")) {
                                        Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
                                            @Override
                                            public void run() {
                                                if (location.getBlock().getState() instanceof org.bukkit.block.Sign) {
                                                    try {
                                                        Location entityLocation = entity.getLocation();
                                                        entity.setVelocity(new Vector(entityLocation.getX() - location.getX(),
                                                                                      entityLocation.getY() - location.getY(),
                                                                                      entityLocation.getZ() - location.getZ()).normalize()
                                                                                                                              .multiply(
                                                                                                                                  signLayoutConfig
                                                                                                                                      .getStrength())
                                                                                                                              .setY(0.2D));
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {

                    }
                }

                SearchingAnimation searchingAnimation = signLayoutConfig.getSearchingAnimation();

                SignLayout searchLayer = getSearchingLayout(animationTick);
                Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        for (Sign sign : signs.values()) {
                            boolean exists = exists(sign);

                            if (!exists) {
                                sign.setServerInfo(null);
                                continue;
                            }

                            if (isMaintenance(sign.getTargetGroup())) {
                                SignLayout _signLayout = getLayout(sign.getTargetGroup(), "maintenance");
                                String[] layout = updateOfflineAndMaintenance(_signLayout.getSignLayout().clone(), sign);
                                sign.setServerInfo(null);
                                sendUpdateSynchronized(toLocation(sign.getPosition()), layout);
                                changeBlock(toLocation(sign.getPosition()),
                                            _signLayout.getBlockName(),
                                            _signLayout.getBlockId(),
                                            _signLayout.getSubId());
                                continue;
                            }

                            Location location = toLocation(sign.getPosition());
                            if (sign.getServerInfo() == null) {
                                List<String> servers = new ArrayList<>(freeServers(sign.getTargetGroup()));
                                if (servers.size() != 0) {
                                    String server = servers.get(NetworkUtils.RANDOM.nextInt(servers.size()));
                                    ServerInfo serverInfo = SignSelector.this.getServers().get(server);
                                    if (serverInfo != null && serverInfo.isOnline() && !serverInfo.isIngame()) {
                                        if (signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                            String[] layout = updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
                                            layout = updateOfflineAndMaintenance(layout, sign);
                                            sendUpdateSynchronized(location, layout);
                                            changeBlock(location,
                                                        searchLayer.getBlockName(),
                                                        searchLayer.getBlockId(),
                                                        searchLayer.getSubId());
                                            continue;
                                        }

                                        sign.setServerInfo(serverInfo);
                                        String[] layout;
                                        SignLayout signLayout;
                                        if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                            signLayout = getLayout(sign.getTargetGroup(), "full");
                                            layout = signLayout.getSignLayout().clone();
                                        } else if (serverInfo.getOnlineCount() == 0) {
                                            signLayout = getLayout(sign.getTargetGroup(), "empty");
                                            layout = signLayout.getSignLayout().clone();
                                        } else {
                                            signLayout = getLayout(sign.getTargetGroup(), "online");
                                            layout = signLayout.getSignLayout().clone();
                                        }
                                        updateArray(layout, serverInfo);
                                        sendUpdateSynchronized(location, layout);
                                        changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                                    } else {
                                        sign.setServerInfo(null);
                                        String[] layout = updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
                                        sendUpdateSynchronized(location, layout);
                                    }
                                } else {
                                    sign.setServerInfo(null);
                                    String[] layout = updateOfflineAndMaintenance(searchLayer.getSignLayout().clone(), sign);
                                    sendUpdateSynchronized(location, layout);
                                    changeBlock(location, searchLayer.getBlockName(), searchLayer.getBlockId(), searchLayer.getSubId());
                                }

                                continue;
                            }

                            if (valueTick) {
                                if (sign.getServerInfo() != null) {
                                    ServerInfo serverInfo = sign.getServerInfo();
                                    if (!isMaintenance(sign.getTargetGroup())) {
                                        if (serverInfo != null && serverInfo.isOnline() && !serverInfo.isIngame()) {
                                            if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                                                .getServerConfig()
                                                .isHideServer()) {
                                                sign.setServerInfo(null);
                                                String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                                  .getSignLayout()
                                                                                                  .clone(), sign);
                                                layout = updateOfflineAndMaintenance(layout, sign);
                                                sendUpdateSynchronized(toLocation(sign.getPosition()), layout);
                                                return;
                                            }
                                            String[] layout;
                                            SignLayout signLayout;
                                            if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                                signLayout = getLayout(sign.getTargetGroup(), "full");
                                                layout = signLayout.getSignLayout().clone();
                                            } else if (serverInfo.getOnlineCount() == 0) {
                                                signLayout = getLayout(sign.getTargetGroup(), "empty");
                                                layout = signLayout.getSignLayout().clone();
                                            } else {
                                                signLayout = getLayout(sign.getTargetGroup(), "online");
                                                layout = signLayout.getSignLayout().clone();
                                            }
                                            sign.setServerInfo(serverInfo);
                                            updateArray(layout, serverInfo);
                                            sendUpdateSynchronized(location, layout);
                                            changeBlock(location,
                                                        signLayout.getBlockName(),
                                                        signLayout.getBlockId(),
                                                        signLayout.getSubId());
                                        } else {
                                            sign.setServerInfo(null);
                                            String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                              .getSignLayout()
                                                                                              .clone(), sign);
                                            sendUpdateSynchronized(location, layout);
                                        }
                                    } else {
                                        sign.setServerInfo(null);
                                        SignLayout _signLayout = getLayout(sign.getTargetGroup(), "maintenance");
                                        String[] layout = updateOfflineAndMaintenance(_signLayout.getSignLayout().clone(), sign);
                                        sendUpdateSynchronized(location, layout);
                                        changeBlock(location, _signLayout.getBlockName(), _signLayout.getBlockId(), _signLayout.getSubId());
                                    }
                                }
                            }
                        }
                    }
                });

                if (searchingAnimation.getAnimations() <= animationTick) {
                    animationTick = 1;
                }

                animationTick++;
                valueTick = !valueTick;

                try {
                    Thread.sleep(1000 / searchingAnimation.getAnimationsPerSecond());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class NetworkHandlerAdapterImpl extends NetworkHandlerAdapter {

        @Override
        public void onServerAdd(ServerInfo serverInfo) {
            servers.put(serverInfo.getServiceId().getServerId(), serverInfo);
            Sign sign = filter(serverInfo);

            if (sign != null) {
                if (exists(sign)) {
                    sign.setServerInfo(serverInfo);
                    Location location = toLocation(sign.getPosition());
                    if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                        if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                            .getServerConfig()
                            .isHideServer()) {
                            sign.setServerInfo(null);
                            SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                            String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                            layout = updateOfflineAndMaintenance(layout, sign);
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                sendUpdate(all, location, layout);
                            }
                            sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                            return;
                        }
                        String[] layout;
                        SignLayout signLayout;
                        if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                            signLayout = getLayout(sign.getTargetGroup(), "full");
                            layout = signLayout.getSignLayout().clone();
                        } else if (serverInfo.getOnlineCount() == 0) {
                            signLayout = getLayout(sign.getTargetGroup(), "empty");
                            layout = signLayout.getSignLayout().clone();
                        } else {
                            signLayout = getLayout(sign.getTargetGroup(), "online");
                            layout = signLayout.getSignLayout().clone();
                        }
                        sign.setServerInfo(serverInfo);
                        updateArray(layout, serverInfo);
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            sendUpdate(all, location, layout);
                        }
                        sendUpdateSynchronizedTask(location, layout);
                        changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                    } else {
                        sign.setServerInfo(null);
                        String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick).getSignLayout()
                                                                                                                             .clone(),
                                                                      sign);
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            sendUpdate(all, location, layout);
                        }
                        sendUpdateSynchronizedTask(location, layout);
                    }

                } else {
                    sign.setServerInfo(null);

                    Sign next = findFreeSign(serverInfo.getServiceId().getGroup());
                    Location location = toLocation(next.getPosition());
                    if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                        if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                            .getServerConfig()
                            .isHideServer()) {
                            sign.setServerInfo(null);
                            SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                            String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                sendUpdate(all, location, layout);
                            }
                            sendUpdateSynchronizedTask(toLocation(next.getPosition()), layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                            return;
                        }
                        String[] layout;
                        SignLayout signLayout;
                        if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                            signLayout = getLayout(sign.getTargetGroup(), "full");
                            layout = signLayout.getSignLayout().clone();
                        } else if (serverInfo.getOnlineCount() == 0) {
                            signLayout = getLayout(sign.getTargetGroup(), "empty");
                            layout = signLayout.getSignLayout().clone();
                        } else {
                            signLayout = getLayout(sign.getTargetGroup(), "online");
                            layout = signLayout.getSignLayout().clone();
                        }
                        sign.setServerInfo(serverInfo);
                        updateArray(layout, serverInfo);
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            sendUpdate(all, location, layout);
                        }
                        sendUpdateSynchronizedTask(location, layout);
                        changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                    } else {
                        sign.setServerInfo(null);
                        SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                        String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            sendUpdate(all, location, layout);
                        }
                        sendUpdateSynchronizedTask(location, layout);
                        changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                    }
                }
            } else {
                Sign newSign = findFreeSign(serverInfo.getServiceId().getGroup());
                if (newSign != null) {
                    if (exists(newSign)) {
                        Location location = toLocation(newSign.getPosition());
                        if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                            if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                                .getServerConfig()
                                .isHideServer()) {
                                sign.setServerInfo(null);
                                SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                                String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                                for (Player all : Bukkit.getOnlinePlayers()) {
                                    sendUpdate(all, location, layout);
                                }
                                sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
                                changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                                return;
                            }

                            SignLayout signLayout;
                            String[] layout;
                            if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                signLayout = getLayout(sign.getTargetGroup(), "full");
                                layout = signLayout.getSignLayout().clone();
                            } else if (serverInfo.getOnlineCount() == 0) {
                                signLayout = getLayout(sign.getTargetGroup(), "empty");
                                layout = signLayout.getSignLayout().clone();
                            } else {
                                signLayout = getLayout(sign.getTargetGroup(), "online");
                                layout = signLayout.getSignLayout().clone();
                            }
                            sign.setServerInfo(serverInfo);
                            updateArray(layout, serverInfo);
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                sendUpdate(all, location, layout);
                            }
                            sendUpdateSynchronizedTask(location, layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                        } else {
                            newSign.setServerInfo(null);
                            SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                            String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                            for (Player all : Bukkit.getOnlinePlayers()) {
                                sendUpdate(all, location, layout);
                            }
                            sendUpdateSynchronizedTask(location, layout);
                            changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                        }
                    }
                }
            }
        }

        @Override
        public void onServerInfoUpdate(ServerInfo serverInfo) {
            servers.put(serverInfo.getServiceId().getServerId(), serverInfo);
            Sign sign = filter(serverInfo);

            if (sign != null) {
                if (CloudServer.getInstance().getPlugin() != null && CloudServer.getInstance().getPlugin().isEnabled()) {
                    Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {

                        @Override
                        public void run() {
                            if (exists(sign)) {
                                sign.setServerInfo(serverInfo);
                                Location location = toLocation(sign.getPosition());
                                if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                                    if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                                        .getServerConfig()
                                        .isHideServer()) {
                                        sign.setServerInfo(null);
                                        SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                                        String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                                        sendUpdateSynchronized(toLocation(sign.getPosition()), layout);
                                        return;
                                    }
                                    SignLayout signLayout;
                                    String[] layout;
                                    if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                        signLayout = getLayout(sign.getTargetGroup(), "full");
                                        layout = signLayout.getSignLayout().clone();
                                    } else if (serverInfo.getOnlineCount() == 0) {
                                        signLayout = getLayout(sign.getTargetGroup(), "empty");
                                        layout = signLayout.getSignLayout().clone();
                                    } else {
                                        signLayout = getLayout(sign.getTargetGroup(), "online");
                                        layout = signLayout.getSignLayout().clone();
                                    }
                                    sign.setServerInfo(serverInfo);
                                    updateArray(layout, serverInfo);
                                    sendUpdateSynchronized(location, layout);
                                    changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                                } else {
                                    sign.setServerInfo(null);
                                    SignLayout signLayout = getSearchingLayout(((ThreadImpl) worker).animationTick);
                                    String[] layout = updateOfflineAndMaintenance(signLayout.getSignLayout().clone(), sign);
                                    sendUpdateSynchronized(location, layout);
                                    changeBlock(location, signLayout.getBlockName(), signLayout.getBlockId(), signLayout.getSubId());
                                }

                            } else {
                                sign.setServerInfo(null);

                                Sign next = findFreeSign(serverInfo.getServiceId().getGroup());
                                Location location = toLocation(next.getPosition());
                                if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                                    if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                                        .getServerConfig()
                                        .isHideServer()) {
                                        sign.setServerInfo(null);
                                        String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                          .getSignLayout()
                                                                                          .clone(), sign);
                                        sendUpdateSynchronized(toLocation(next.getPosition()), layout);
                                        return;
                                    }
                                    String[] layout;
                                    if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                        layout = getLayout(sign.getTargetGroup(), "full").getSignLayout().clone();
                                    } else if (serverInfo.getOnlineCount() == 0) {
                                        layout = getLayout(sign.getTargetGroup(), "empty").getSignLayout().clone();
                                    } else {
                                        layout = getLayout(sign.getTargetGroup(), "online").getSignLayout().clone();
                                    }
                                    sign.setServerInfo(serverInfo);
                                    updateArray(layout, serverInfo);
                                    sendUpdateSynchronized(location, layout);
                                } else {
                                    sign.setServerInfo(null);
                                    String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick).getSignLayout()
                                                                                                                                         .clone(),
                                                                                  sign);
                                    sendUpdateSynchronized(location, layout);
                                }
                            }
                        }
                    });
                } else {
                    Bukkit.getScheduler().runTask(CloudServer.getInstance().getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            Sign newSign = findFreeSign(serverInfo.getServiceId().getGroup());
                            if (newSign != null) {
                                if (exists(newSign)) {
                                    Location location = toLocation(newSign.getPosition());
                                    if (serverInfo.isOnline() && !serverInfo.isIngame()) {
                                        if ((signLayoutConfig.isFullServerHide() && serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) || serverInfo
                                            .getServerConfig()
                                            .isHideServer()) {
                                            sign.setServerInfo(null);
                                            String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                              .getSignLayout()
                                                                                              .clone(), sign);
                                            sendUpdateSynchronized(toLocation(sign.getPosition()), layout);
                                            return;
                                        }
                                        String[] layout;
                                        if (serverInfo.getOnlineCount() >= serverInfo.getMaxPlayers()) {
                                            layout = getLayout(sign.getTargetGroup(), "full").getSignLayout().clone();
                                        } else if (serverInfo.getOnlineCount() == 0) {
                                            layout = getLayout(sign.getTargetGroup(), "empty").getSignLayout().clone();
                                        } else {
                                            layout = getLayout(sign.getTargetGroup(), "online").getSignLayout().clone();
                                        }
                                        sign.setServerInfo(serverInfo);
                                        updateArray(layout, serverInfo);
                                        sendUpdateSynchronized(location, layout);
                                    } else {
                                        sign.setServerInfo(null);
                                        String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick)
                                                                                          .getSignLayout()
                                                                                          .clone(), sign);
                                        sendUpdateSynchronized(location, layout);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onServerRemove(ServerInfo serverInfo) {
            servers.remove(serverInfo.getServiceId().getServerId(), serverInfo);

            Sign sign = filter(serverInfo);
            if (sign != null) {
                sign.setServerInfo(null);
                if (!exists(sign)) {
                    return;
                }
                String[] layout = updateOfflineAndMaintenance(getSearchingLayout(((ThreadImpl) worker).animationTick).getSignLayout()
                                                                                                                     .clone(), sign);
                sendUpdateSynchronizedTask(toLocation(sign.getPosition()), layout);
            }
        }
    }
}
