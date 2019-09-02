package de.dytanic.cloudnet.bridge.vault;

import de.dytanic.cloudnet.bridge.BukkitBootstrap;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

/**
 * Created by Tareko on 30.11.2017.
 */
public final class VaultInvoker {

    private VaultInvoker() {
    }

    public static void invoke() {
        ServicesManager servicesManager = BukkitBootstrap.getPlugin(BukkitBootstrap.class).getServer().getServicesManager();

        Permission permission = new VaultPermissionImpl();

        servicesManager.register(Permission.class, permission, BukkitBootstrap.getPlugin(BukkitBootstrap.class), ServicePriority.Highest);
        servicesManager.register(Chat.class,
                                 new VaultChatImpl(permission),
                                 BukkitBootstrap.getPlugin(BukkitBootstrap.class),
                                 ServicePriority.Highest);
    }

}
