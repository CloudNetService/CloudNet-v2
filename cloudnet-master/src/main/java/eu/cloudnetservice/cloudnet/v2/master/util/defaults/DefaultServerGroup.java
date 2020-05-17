package eu.cloudnetservice.cloudnet.v2.master.util.defaults;

import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroupMode;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroupType;
import eu.cloudnetservice.cloudnet.v2.lib.server.advanced.AdvancedServerConfig;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.TemplateResource;

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
