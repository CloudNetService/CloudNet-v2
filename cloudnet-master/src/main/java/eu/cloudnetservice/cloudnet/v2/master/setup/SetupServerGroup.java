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

package eu.cloudnetservice.cloudnet.v2.master.setup;

import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroupMode;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroupType;
import eu.cloudnetservice.cloudnet.v2.lib.server.advanced.AdvancedServerConfig;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.TemplateResource;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import eu.cloudnetservice.cloudnet.v2.setup.Setup;
import eu.cloudnetservice.cloudnet.v2.setup.SetupRequest;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.IntegerResponseType;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.StringResponseType;

import java.util.*;
import java.util.regex.Pattern;

public class SetupServerGroup {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final Pattern WRAPPER_SPLITTER = Pattern.compile("\\s?+,\\s?+");
    private final String name;
    private final Setup setup;

    public SetupServerGroup(CommandSender commandSender, String name) {
        this.name = name;

        setup = new Setup().setupCancel(() -> System.out.println("Setup cancelled!"))
                           .setupComplete(data -> {
                               // Make sure there is at least one valid wrapper
                               List<String> wrappers = new ArrayList<>(Arrays.asList(WRAPPER_SPLITTER.split(data.getString("wrapper"))));
                               if (wrappers.size() == 0) {
                                   return;
                               }
                               Set<String> cloudWrappers = CloudNet.getInstance().getWrappers().keySet();
                               wrappers.removeIf(wrapper -> !cloudWrappers.contains(wrapper));
                               if (wrappers.size() == 0) {
                                   return;
                               }

                               ServerGroupMode serverGroupMode = ServerGroupMode.valueOf(data.getString("mode").toUpperCase());

                               ServerGroupType serverGroupType = ServerGroupType.valueOf(data.getString("type").toUpperCase());

                               ServerGroup serverGroup = new ServerGroup(name,
                                                                         wrappers,
                                                                         serverGroupMode.equals(ServerGroupMode.LOBBY),
                                                                         data.getInt("memory"),
                                                                         0,
                                                                         true,
                                                                         data.getInt("startup"),
                                                                         data.getInt("onlineGlobal"),
                                                                         data.getInt("onlineGroup"),
                                                                         180,
                                                                         100,
                                                                         100,
                                                                         data.getInt("percent"),
                                                                         serverGroupType,
                                                                         serverGroupMode,
                                                                         new Template("globaltemplate",
                                                                                      TemplateResource.valueOf(data.getString("template")),
                                                                                      null,
                                                                                      EMPTY_STRING_ARRAY,
                                                                                      Collections.emptyList()),
                                                                         Collections.singletonList(
                                                                             new Template("default",
                                                                                          TemplateResource.valueOf(data.getString("template")),
                                                                                          null,
                                                                                          EMPTY_STRING_ARRAY,
                                                                                          Collections.emptyList())),
                                                                         new AdvancedServerConfig(false,
                                                                                                  false,
                                                                                                  false,
                                                                                                  !serverGroupMode.equals(ServerGroupMode.STATIC)));
                               CloudNet.getInstance().getConfig().createGroup(serverGroup);
                               CloudNet.getInstance().getServerGroups().put(serverGroup.getName(), serverGroup);
                               CloudNet.getInstance().setupGroup(serverGroup);
                               CloudNet.getInstance().toWrapperInstances(wrappers).forEach(Wrapper::updateWrapper);
                               commandSender.sendMessage(String.format("The server group %s is now created!", serverGroup.getName()));
                           })
                           .request(new SetupRequest("memory",
                                                     "How much memory should each server of this server group have?",
                                                     "The specified amount of memory is invalid",
                                                     IntegerResponseType.getInstance(),
                                                     key -> Integer.parseInt(key) >= 64))
                           .request(new SetupRequest("startup",
                                                     "How many servers should always be online?",
                                                     "The specified amount of servers is invalid",
                                                     IntegerResponseType.getInstance(),
                                                     key -> Integer.parseInt(key) >= 0))
                           .request(new SetupRequest("percent",
                                                     "How full does the server have to be to start a new server? (in percent)",
                                                     "The specified percentage is invalid",
                                                     IntegerResponseType.getInstance(),
                                                     key -> Integer.parseInt(key) >= 0 && Integer.parseInt(key) <= 100))
                           .request(new SetupRequest("mode",
                                                     "Which server group mode should be used for this server group? [STATIC, STATIC_LOBBY, LOBBY, DYNAMIC]",
                                                     "The specified server group mode is invalid",
                                                     StringResponseType.getInstance(),
                                                     key -> key.equalsIgnoreCase("STATIC") ||
                                                         key.equalsIgnoreCase("STATIC_LOBBY") ||
                                                         key.equalsIgnoreCase("LOBBY") ||
                                                         key.equalsIgnoreCase("DYNAMIC")))
                           .request(new SetupRequest("type",
                                                     "Which server group type should be used? [BUKKIT, GLOWSTONE]",
                                                     "The specified group type is invalid",
                                                     StringResponseType.getInstance(),
                                                     key -> key.equals("BUKKIT") || key.equals("GLOWSTONE")))
                           .request(new SetupRequest("template",
                                                     "What backend should be used for the group's default template? [\"LOCAL\" for a wrapper local backend | \"MASTER\" for the master backend]",
                                                     "The specified backend is invalid",
                                                     StringResponseType.getInstance(),
                                                     key -> key.equals("MASTER") || key.equals("LOCAL")))
                           .request(new SetupRequest("onlineGroup",
                                                     "How many servers should be online, if 100 players are online in the group?",
                                                     "The specified amount is invalid",
                                                     IntegerResponseType.getInstance(),
                                                     key -> Integer.parseInt(key) > 0))
                           .request(new SetupRequest("onlineGlobal",
                                                     "How many servers should be online, if 100 global players are online?",
                                                     "The specified amount is invalid",
                                                     IntegerResponseType.getInstance(),
                                                     key -> Integer.parseInt(key) > 0))
                           .request(
                               new SetupRequest("wrapper",
                                                "Which wrappers should be used for this group? (comma-separated list)",
                                                "The specified list of wrappers is invalid",
                                                StringResponseType.getInstance(),
                                                key -> {
                                                    // Make sure there is at least one valid wrapper
                                                    List<String> wrappers = new ArrayList<>(Arrays.asList(WRAPPER_SPLITTER.split(key)));

                                                    if (wrappers.size() == 0) {
                                                        return false;
                                                    }
                                                    Set<String> cloudWrappers = CloudNet.getInstance().getWrappers().keySet();
                                                    wrappers.removeIf(wrapper -> !cloudWrappers.contains(wrapper));
                                                    return wrappers.size() != 0;
                                                }));
    }

    public String getName() {
        return name;
    }

    public void startSetup() {
        setup.start(CloudNet.getLogger().getReader());
    }
}
