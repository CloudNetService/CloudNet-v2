/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util.defaults;

import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;

import java.util.ArrayList;
import java.util.Arrays;

public final class LobbyGroup extends ServerGroup {
    public LobbyGroup() {
        super("Lobby",
              Arrays.asList("Wrapper-1"),
              true,
              356,
              512,
              0,
              false,
              0,
              1,
              0,
              300,
              100,
              100,
              50,
              ServerGroupType.BUKKIT,
              ServerGroupMode.LOBBY,
              Arrays.asList(new Template("default", TemplateResource.LOCAL, null, new String[] {}, new ArrayList<>())),
              new AdvancedServerConfig(true, true, true, true));
    }
}
