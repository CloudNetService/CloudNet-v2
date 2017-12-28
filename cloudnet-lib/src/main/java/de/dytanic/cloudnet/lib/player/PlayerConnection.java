package de.dytanic.cloudnet.lib.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Tareko on 27.07.2017.
 */
@Getter
@AllArgsConstructor
public class PlayerConnection {

    private UUID uniqueId;

    private String name;

    private int version;

    private String host;

    private int port;

    private boolean onlineMode;

    private boolean legacy;

}