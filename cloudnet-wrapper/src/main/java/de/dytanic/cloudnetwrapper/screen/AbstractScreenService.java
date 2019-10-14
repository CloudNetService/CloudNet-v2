package de.dytanic.cloudnetwrapper.screen;

import de.dytanic.cloudnet.lib.server.screen.ScreenInfo;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutSendScreenLine;

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
