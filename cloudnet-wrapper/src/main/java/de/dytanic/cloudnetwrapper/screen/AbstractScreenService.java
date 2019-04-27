package de.dytanic.cloudnetwrapper.screen;

import de.dytanic.cloudnet.lib.server.screen.ScreenInfo;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutSendScreenLine;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Getter;

public abstract class AbstractScreenService implements Screenable {

    @Getter
    protected final Queue<String> cachedLogMessages = new ConcurrentLinkedQueue<>();

    @Getter
    protected volatile boolean screenSystemEnabled;

    public void addCachedItem(String text)
    {
        if (text == null) return;

        while (cachedLogMessages.size() >= 64)
            cachedLogMessages.poll();

        cachedLogMessages.offer(text);

        if (this.screenSystemEnabled) this.sendScreenLine0(text);
    }

    public void enableScreenSystem()
    {
        for (String text : this.cachedLogMessages)
            this.sendScreenLine0(text);

        this.screenSystemEnabled = true;
    }

    public void disableScreenSystem()
    {
        this.screenSystemEnabled = false;
    }

    private void sendScreenLine0(String text)
    {
        CloudNetWrapper.getInstance().getNetworkConnection().sendPacket(new PacketOutSendScreenLine(Collections.singletonList(new ScreenInfo(getServiceId(), text))));
    }
}