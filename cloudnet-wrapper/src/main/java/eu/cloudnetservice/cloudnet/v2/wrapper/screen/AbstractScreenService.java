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

package eu.cloudnetservice.cloudnet.v2.wrapper.screen;

import eu.cloudnetservice.cloudnet.v2.lib.server.screen.ScreenInfo;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out.PacketOutSendScreenLine;

import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractScreenService implements Screenable {

    protected final Queue<String> cachedLogMessages = new ConcurrentLinkedQueue<>();

    protected volatile boolean screenSystemEnabled;

    public void addCachedItem(String text) {
        if (text == null) {
            return;
        }

        while (cachedLogMessages.size() >= 64) {
            cachedLogMessages.poll();
        }

        cachedLogMessages.offer(text);

        if (this.screenSystemEnabled) {
            this.sendScreenLine0(text);
        }
    }

    private void sendScreenLine0(String text) {
        CloudNetWrapper.getInstance()
                       .getNetworkConnection()
                       .sendPacket(new PacketOutSendScreenLine(Collections.singletonList(new ScreenInfo(getServiceId(), text))));
    }

    public void enableScreenSystem() {
        for (String text : this.cachedLogMessages) {
            this.sendScreenLine0(text);
        }

        this.screenSystemEnabled = true;
    }

    public void disableScreenSystem() {
        this.screenSystemEnabled = false;
    }

    public Queue<String> getCachedLogMessages() {
        return cachedLogMessages;
    }

    public boolean isScreenSystemEnabled() {
        return screenSystemEnabled;
    }
}
