package eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.result;

import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.util.UUID;

/**
 * Created by Tareko on 26.07.2017.
 */
public class Result {

    private final UUID uniqueId;

    private final Document result;

    public Result(UUID uniqueId, Document result) {
        this.uniqueId = uniqueId;
        this.result = result;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Document getResult() {
        return result;
    }
}