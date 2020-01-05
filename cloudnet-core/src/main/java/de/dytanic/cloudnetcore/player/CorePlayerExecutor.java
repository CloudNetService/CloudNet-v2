package de.dytanic.cloudnetcore.player;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.PlayerExecutor;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.Objects;

/**
 * CloudNet core implementation for the player executor.
 * This class is usable only with master plugins and allows developers to interact with players regardless
 * of their connected proxy.
 * <p>
 * Use the {@link #INSTANCE} to use this class.
 */
public final class CorePlayerExecutor extends PlayerExecutor {

    public static final PlayerExecutor INSTANCE = new CorePlayerExecutor();

    private CorePlayerExecutor() {
    }

    @Override
    public void sendPlayer(CloudPlayer cloudPlayer, String server) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(server, "the provided server is null");
        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "sendPlayer",
                                                                    new Document("uniqueId", cloudPlayer.getUniqueId())
                                                                        .append("name", cloudPlayer.getName())
                                                                        .append("server", server));
    }

    @Override
    public void kickPlayer(CloudPlayer cloudPlayer, String reason) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(reason, "the provided reason is null");
        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "kickPlayer",
                                                                    new Document("uniqueId", cloudPlayer.getUniqueId())
                                                                        .append("name", cloudPlayer.getName())
                                                                        .append("reason", reason));
    }

    @Override
    public void sendMessage(CloudPlayer cloudPlayer, String message) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(message, "the provided message is null");
        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "sendMessage",
                                                                    new Document("message", message)
                                                                        .append("name", cloudPlayer.getName())
                                                                        .append("uniqueId", cloudPlayer.getUniqueId()));
    }

    @Override
    public void sendActionbar(final CloudPlayer cloudPlayer, final String message) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(message, "the provided message is null");
        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "sendActionbar",
                                                                    new Document("message", message)
                                                                        .append("uniqueId", cloudPlayer.getUniqueId()));
    }

    @Override
    public void sendTitle(final CloudPlayer cloudPlayer,
                          final String title,
                          final String subTitle,
                          final int fadeIn,
                          final int stay,
                          final int fadeOut) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(title, "the provided title is null");
        Objects.requireNonNull(subTitle, "the provided subtitle is null");

        CloudNet.getInstance().getNetworkManager().sendProxyMessage("cloudnet_internal", "sendTitle",
                                                                    new Document("uniqueId", cloudPlayer.getUniqueId())
                                                                        .append("title", title)
                                                                        .append("subTitle", subTitle)
                                                                        .append("stay", stay)
                                                                        .append("fadeIn", fadeIn)
                                                                        .append("fadeOut", fadeOut));

    }
}
