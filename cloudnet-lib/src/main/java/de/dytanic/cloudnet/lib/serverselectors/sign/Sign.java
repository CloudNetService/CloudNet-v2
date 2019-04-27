package de.dytanic.cloudnet.lib.serverselectors.sign;

import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Tareko on 03.06.2017.
 */
@Getter
public class Sign {

    private UUID uniqueId;
    private String targetGroup;
    private Position position;

    @Setter
    private volatile ServerInfo serverInfo;

    public Sign(String targetGroup, Position signPosition)
    {
        this.uniqueId = UUID.randomUUID();
        this.targetGroup = targetGroup;
        this.position = signPosition;
    }
}