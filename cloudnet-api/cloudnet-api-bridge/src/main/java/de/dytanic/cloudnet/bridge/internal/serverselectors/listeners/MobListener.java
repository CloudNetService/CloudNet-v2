package de.dytanic.cloudnet.bridge.internal.serverselectors.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.internal.serverselectors.Mob;
import de.dytanic.cloudnet.bridge.internal.serverselectors.MobSelector;
import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.serverselectors.mob.ServerMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MobListener implements Listener {

    private final MobSelector mobSelector;

    public MobListener(MobSelector mobSelector) {
        this.mobSelector = mobSelector;
    }

    @EventHandler
    public void handleRightClick(PlayerInteractEntityEvent e) {
        Mob mobImpl = mobSelector.getMobs().get(e.getRightClicked().getUniqueId());
        if (mobImpl != null) {
            e.setCancelled(true);
            if (!CloudAPI.getInstance().getServerGroupData(mobImpl.getMob().getTargetGroup()).isMaintenance()) {
                if (mobImpl.getMob().getAutoJoin() != null && mobImpl.getMob().getAutoJoin()) {
                    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
                    byteArrayDataOutput.writeUTF("Connect");

                    List<ServerInfo> serverInfos = mobSelector.getServers(mobImpl.getMob().getTargetGroup());

                    for (ServerInfo serverInfo : serverInfos) {
                        if (serverInfo.getOnlineCount() < serverInfo.getMaxPlayers() &&
                            serverInfo.getServerState().equals(ServerState.LOBBY)) {
                            byteArrayDataOutput.writeUTF(serverInfo.getServiceId().getServerId());
                            e.getPlayer().sendPluginMessage(
                                CloudServer.getInstance().getPlugin(),
                                "BungeeCord",
                                byteArrayDataOutput.toByteArray());
                            return;
                        }
                    }
                } else {
                    e.getPlayer().openInventory(mobImpl.getInventory());
                }
            } else {
                e.getPlayer().sendMessage(
                    ChatColor.translateAlternateColorCodes(
                        '&',
                        CloudAPI.getInstance()
                                .getCloudNetwork()
                                .getMessages()
                                .getString("mob-selector-maintenance-message")));
            }
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent e) {
        Mob mob = mobSelector.getMobs().get(e.getEntity().getUniqueId());
        if (mob != null) {
            e.getEntity().setFireTicks(0);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        final Player player = (Player) e.getWhoClicked();

        if (mobSelector.getInventories().contains(e.getInventory()) && e.getCurrentItem() != null && e.getSlot() == e.getRawSlot()) {
            e.setCancelled(true);
            if (ItemStackBuilder.getMaterialIgnoreVersion(mobSelector.getMobConfig().getItemLayout().getItemName(),
                                                          mobSelector.getMobConfig().getItemLayout().getItemId()) == e.getCurrentItem()
                                                                                                                      .getType()) {
                Mob mob = mobSelector.findByInventory(e.getInventory());
                if (mob.getServerPosition().containsKey(e.getSlot())) {
                    if (CloudAPI.getInstance().getServerId().equalsIgnoreCase(mob.getServerPosition().get(e.getSlot()))) {
                        return;
                    }
                    ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
                    byteArrayDataOutput.writeUTF("Connect");
                    byteArrayDataOutput.writeUTF(mob.getServerPosition().get(e.getSlot()));
                    player.sendPluginMessage(
                        CloudServer.getInstance().getPlugin(),
                        "BungeeCord",
                        byteArrayDataOutput.toByteArray());
                }
            }
        }
    }

    @EventHandler
    public void onSave(WorldSaveEvent e) {
        // Create a simple copy of the original mobs
        Map<UUID, ServerMob> mobMap = new HashMap<>();
        mobSelector.getMobs().forEach((uuid, serverMob) -> mobMap.put(uuid, serverMob.getMob()));

        mobSelector.shutdown();


        Bukkit.getScheduler().runTaskLater(CloudServer.getInstance().getPlugin(), () -> {
            Map<UUID, Mob> mobImplementationMap = new HashMap<>();

            mobMap.forEach((uuid, serverMob) -> {
                Mob mob = mobSelector.spawnMob(mobSelector.getMobConfig(), uuid, serverMob);
                if (mob == null) {
                    return;
                }

                mobImplementationMap.put(uuid, mob);
            });

            mobSelector.setMobs(mobImplementationMap);
            Bukkit.getScheduler().runTaskAsynchronously(CloudServer.getInstance().getPlugin(), () -> {
                for (ServerInfo serverInfo : mobSelector.getServers().values()) {
                    mobSelector.handleUpdate(serverInfo);
                }
            });
        }, 40);
    }
}
