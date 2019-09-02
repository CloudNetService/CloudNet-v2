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
import java.util.Collection;

public class DefaultServerGroup extends ServerGroup {

    public DefaultServerGroup(String name,
                              Collection<String> wrapper,
                              int memory,
                              int startup,
                              int percentForNewServerAutomatically,
                              ServerGroupType serverType,
                              ServerGroupMode groupMode,
                              AdvancedServerConfig advancedServerConfig) {
        super(name,
              wrapper,
              false,
              memory,
              memory,
              0,
              true,
              startup,
              0,
              1,
              180,
              100,
              100,
              percentForNewServerAutomatically,
              serverType,
              groupMode,
              Arrays.asList(new Template("default", TemplateResource.LOCAL, null, new String[] {}, new ArrayList<>())),
              advancedServerConfig);
    }
}
