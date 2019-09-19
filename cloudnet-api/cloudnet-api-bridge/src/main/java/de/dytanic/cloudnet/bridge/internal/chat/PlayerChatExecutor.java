package de.dytanic.cloudnet.bridge.internal.chat;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.api.chat.BaseComponent;

public interface PlayerChatExecutor {

    default void sendMessage(CloudPlayer cloudPlayer, BaseComponent... baseComponents) {
        for (final BaseComponent baseComponent : baseComponents) {
            sendMessage(cloudPlayer, baseComponent);
        }
    }

    default void sendMessage(CloudPlayer cloudPlayer, BaseComponent baseComponent) {
        if (cloudPlayer != null) {
            final Document messageValue = new Document("baseComponent", baseComponent).append("uniqueId", cloudPlayer.getUniqueId()).append(
                "name",
                cloudPlayer.getName());
            CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal", "sendMessage_basecomponent", messageValue);
        }
    }

}
