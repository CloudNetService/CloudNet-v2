package eu.cloudnetservice.v2.master.network.wrapper;

import eu.cloudnetservice.v2.master.network.components.WrapperMeta;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Tareko on 23.09.2017.
 */
public class WrapperSession implements Serializable {

    private final UUID uniqueId;

    private final WrapperMeta wrapperMeta;

    private final long connected;

    public WrapperSession(UUID uniqueId, WrapperMeta wrapperMeta, long connected) {
        this.uniqueId = uniqueId;
        this.wrapperMeta = wrapperMeta;
        this.connected = connected;
    }

    public long getConnected() {
        return connected;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public WrapperMeta getWrapperMeta() {
        return wrapperMeta;
    }
}
