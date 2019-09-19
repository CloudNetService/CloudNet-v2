package de.dytanic.cloudnet.bridge.internal.chat;

import com.google.gson.GsonBuilder;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import net.md_5.bungee.chat.TranslatableComponentSerializer;

public final class DocumentRegistry {

    private DocumentRegistry() {
    }

    public static void fire() {
        Document.GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping()
                                         //
                                         .registerTypeAdapter(BaseComponent.class, new ComponentSerializer()).registerTypeAdapter(
                TextComponent.class,
                new TextComponentSerializer()).registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer())
                                         //
                                         .create();
    }

}
