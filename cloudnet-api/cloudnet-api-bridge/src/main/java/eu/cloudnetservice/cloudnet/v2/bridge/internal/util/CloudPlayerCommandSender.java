/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.bridge.internal.util;

import eu.cloudnetservice.cloudnet.v2.api.player.PlayerExecutorBridge;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.event.PermissionCheckEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("deprecation")
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
    public void sendMessage(String message) {
        PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, message);
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
