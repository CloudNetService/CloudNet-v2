package eu.cloudnetservice.v2.api.player;

import eu.cloudnetservice.v2.api.CloudAPI;
import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.v2.lib.player.PlayerExecutor;
import eu.cloudnetservice.v2.lib.utility.document.Document;

import java.util.Objects;

/**
 * Created by Tareko on 27.08.2017.
 */
public class PlayerExecutorBridge extends PlayerExecutor {

    public static final PlayerExecutorBridge INSTANCE = new PlayerExecutorBridge(CloudAPI.getInstance());

    private static final String CHANNEL_NAME = "cloudnet_internal";

    private final CloudAPI cloudAPI;

    /**
     * Constructs a new executor for cloud player interactions.
     *
     * @deprecated use the {@link #INSTANCE} instead.
     */
    @Deprecated
    public PlayerExecutorBridge() {
        this(CloudAPI.getInstance());
        CloudAPI.getInstance().getLogger().warning("A plugin instantiated the PlayerExecutorBridge!");
        CloudAPI.getInstance().getLogger().warning("This is not necessary, as the constant instance replaced that.");
        CloudAPI.getInstance().getLogger().warning("Please use that instead of your own instance! ");
    }

    private PlayerExecutorBridge(final CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void sendPlayer(CloudPlayer cloudPlayer, String server) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(server, "the provided server is null");

        this.cloudAPI.sendCustomSubProxyMessage(CHANNEL_NAME, "sendPlayer",
                                                new Document("uniqueId", cloudPlayer.getUniqueId())
                                                    .append("name", cloudPlayer.getName())
                                                    .append("server", server));
    }

    @Override
    public void kickPlayer(CloudPlayer cloudPlayer, String reason) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(reason, "the provided reason is null");

        this.cloudAPI.sendCustomSubProxyMessage(CHANNEL_NAME, "kickPlayer",
                                                new Document("uniqueId", cloudPlayer.getUniqueId())
                                                    .append("name", cloudPlayer.getName())
                                                    .append("reason", reason));
    }

    @Override
    public void sendMessage(CloudPlayer cloudPlayer, String message) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(message, "the provided message is null");

        this.cloudAPI.sendCustomSubProxyMessage(CHANNEL_NAME, "sendMessage",
                                                new Document("message", message)
                                                    .append("name", cloudPlayer.getName())
                                                    .append("uniqueId", cloudPlayer.getUniqueId()));
    }

    @Override
    public void sendActionbar(CloudPlayer cloudPlayer, String message) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(message, "the provided message is null");

        this.cloudAPI.sendCustomSubProxyMessage(CHANNEL_NAME, "sendActionbar",
                                                new Document("message", message)
                                                    .append("uniqueId", cloudPlayer.getUniqueId()));
    }

    @Override
    public void sendTitle(CloudPlayer cloudPlayer, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Objects.requireNonNull(cloudPlayer, "the provided player is null");
        Objects.requireNonNull(title, "the provided title is null");
        Objects.requireNonNull(subTitle, "the provided subtitle is null");
        this.cloudAPI.sendCustomSubProxyMessage(CHANNEL_NAME, "sendTitle",
                                                new Document("uniqueId", cloudPlayer.getUniqueId())
                                                    .append("title", title)
                                                    .append("subTitle", subTitle)
                                                    .append("stay", stay)
                                                    .append("fadeIn", fadeIn)
                                                    .append("fadeOut", fadeOut));
    }
}
