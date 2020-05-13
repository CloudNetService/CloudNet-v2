package eu.cloudnetservice.v2.master.util.defaults;

import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;

import java.util.Collection;
import java.util.Collections;

public class DefaultServerGroup extends ServerGroup {

    public static final String[] EMPTY_STRING_ARRAY = new String[] {};

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
              new Template("globaltemplate", TemplateResource.LOCAL, null, EMPTY_STRING_ARRAY, Collections.emptyList()),
              Collections.singletonList(new Template("default", TemplateResource.LOCAL, null, EMPTY_STRING_ARRAY, Collections.emptyList())),
              advancedServerConfig);
    }
}
