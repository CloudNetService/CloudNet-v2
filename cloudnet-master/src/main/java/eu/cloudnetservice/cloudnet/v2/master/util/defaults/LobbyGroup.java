/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.master.util.defaults;

import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroupMode;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroupType;
import eu.cloudnetservice.cloudnet.v2.lib.server.advanced.AdvancedServerConfig;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.TemplateResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class LobbyGroup extends ServerGroup {

    public static final String[] EMPTY_STRING_ARRAY = {};

    public LobbyGroup() {
        super("Lobby",
              Arrays.asList("Wrapper-1"),
              true,
              356,
              512,
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
              new Template("globaltemplate", TemplateResource.LOCAL, null, EMPTY_STRING_ARRAY, Collections.emptyList()),
              Collections.singletonList(new Template("default", TemplateResource.LOCAL, null, EMPTY_STRING_ARRAY, new ArrayList<>())),
              new AdvancedServerConfig(true, true, true, true));
    }
}
