package eu.cloudnetservice.cloudnet.v2.bridge.internal.chat;

import com.google.gson.GsonBuilder;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.KeybindComponentSerializer;
import net.md_5.bungee.chat.TextComponentSerializer;
import net.md_5.bungee.chat.TranslatableComponentSerializer;

public final class DocumentRegistry {

    private DocumentRegistry() {
    }

    public static void fire() {
        Document.GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            //
            .registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
            .registerTypeAdapter(TextComponent.class, new TextComponentSerializer())
            .registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer())
            .registerTypeAdapter(KeybindComponent.class, new KeybindComponentSerializer())
            //
            .create();
    }

}
