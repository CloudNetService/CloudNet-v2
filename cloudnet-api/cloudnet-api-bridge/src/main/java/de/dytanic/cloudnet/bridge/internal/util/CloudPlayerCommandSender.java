package de.dytanic.cloudnet.bridge.internal.util;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.event.PermissionCheckEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Tareko on 10.01.2018.
 */
public class CloudPlayerCommandSender implements CommandSender {

    private final CloudPlayer cloudPlayer;

    public CloudPlayerCommandSender(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    @Override
    public String getName() {
        return cloudPlayer.getName();
    }

    @Override
    public void sendMessage(String s) {
        cloudPlayer.getPlayerExecutor().sendMessage(cloudPlayer, s);
    }

    @Override
    public void sendMessages(String... strings) {
        for (String m : strings) {
            sendMessage(m);
        }
    }

    @Override
    public void sendMessage(BaseComponent... baseComponents) {
        for (BaseComponent m : baseComponents) {
            sendMessage(m);
        }
    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {
        sendMessage(baseComponent.toLegacyText());
    }

    @Override
    public Collection<String> getGroups() {
        return new ArrayList<>();
    }

    @Override
    public void addGroups(String... strings) {

    }

    @Override
    public void removeGroups(String... strings) {

    }

    @Override
    public boolean hasPermission(String s) {
        return new PermissionCheckEvent(this, s, false).hasPermission();
    }

    @Override
    public void setPermission(String s, boolean b) {

    }

    @Override
    public Collection<String> getPermissions() {
        return new ArrayDeque<>();
    }
}
