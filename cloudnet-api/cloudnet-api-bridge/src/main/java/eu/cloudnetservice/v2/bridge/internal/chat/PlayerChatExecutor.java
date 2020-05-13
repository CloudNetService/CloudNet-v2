package eu.cloudnetservice.v2.bridge.internal.chat;

import eu.cloudnetservice.v2.api.CloudAPI;
import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.v2.lib.utility.document.Document;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Executor for chat interaction with cloud players.
 * Exclusively uses {@link BaseComponent} instances.
 */
public final class PlayerChatExecutor {

    /**
     * Sends all given base components to the given player.
     * The base components are send individually; the array returned by
     * {@link net.md_5.bungee.api.chat.TextComponent#fromLegacyText(String)} should be wrapped in another component to ensure
     * that the message will be properly received by the player.
     *
     * @param cloudPlayer    the player to send the messages to.
     * @param baseComponents the base components to send.
     *
     * @see #sendMessage(CloudPlayer, BaseComponent)
     */
    public static void sendMessage(CloudPlayer cloudPlayer, BaseComponent... baseComponents) {
        for (final BaseComponent baseComponent : baseComponents) {
            sendMessage(cloudPlayer, baseComponent);
        }
    }

    /**
     * Sends a single base component to a cloud player.
     *
     * @param cloudPlayer   the player to send the message to.
     * @param baseComponent the message to send.
     */
    public static void sendMessage(CloudPlayer cloudPlayer, BaseComponent baseComponent) {
        if (cloudPlayer != null) {
            final Document messageValue = new Document("baseComponent", baseComponent)
                .append("uniqueId", cloudPlayer.getUniqueId())
                .append("name", cloudPlayer.getName());
            CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal", "sendMessage_basecomponent", messageValue);
        }
    }

}
