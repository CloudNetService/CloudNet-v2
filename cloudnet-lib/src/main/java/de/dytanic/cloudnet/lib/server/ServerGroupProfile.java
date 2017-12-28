package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.server.template.Template;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 03.07.2017.
 */
@Getter
@AllArgsConstructor
public class ServerGroupProfile
                    implements Nameable{

    private String name;

    private int maxPlayerCount;

    private Template config;

}