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

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.map.WrappedMap;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroupMode;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.TemplateResource;
import eu.cloudnetservice.cloudnet.v2.lib.server.version.ProxyVersion;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import eu.cloudnetservice.cloudnet.v2.master.util.defaults.BasicProxyConfig;
import eu.cloudnetservice.cloudnet.v2.setup.Setup;
import eu.cloudnetservice.cloudnet.v2.setup.SetupRequest;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.IntegerResponseType;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.StringResponseType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Tareko on 23.10.2017.
 */
public class SetupProxyGroup extends Setup {

    private static final Pattern WRAPPER_SPLITTER = Pattern.compile("\\s?+,\\s?+");
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private final String name;

    public SetupProxyGroup(CommandSender commandSender, String name) {
        super(CloudNet.getInstance().getConsoleManager());
        this.name = name;

        this.setupCancel(() -> {
            System.out.println("Setup was cancelled");
            CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
            CloudNet.getInstance().getConsoleRegistry().unregisterInput(SetupProxyGroup.class);
        }).setupComplete(data -> {
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

                               ProxyGroupMode proxyGroupMode;

                               try {
                                   proxyGroupMode = ProxyGroupMode.valueOf(data.getString("mode").toUpperCase());
                               } catch (IllegalArgumentException exception) {
                                   commandSender.sendMessage("Proxy group mode unknown, defaulting to DYNAMIC");
                                   proxyGroupMode = ProxyGroupMode.DYNAMIC;
                               }

                               ProxyGroup proxyGroup = new ProxyGroup(name,
                                                                      wrappers,
                                                                      new Template("default",
                                                                                   TemplateResource.valueOf(data.getString("template")),
                                                                                   null,
                                                                                   EMPTY_STRING_ARRAY,
                                                                                   new ArrayList<>()),
                                                                      ProxyVersion.BUNGEECORD,
                                                                      data.getInt("startport"),
                                                                      data.getInt("startup"),
                                                                      data.getInt("memory"),
                                                                      new BasicProxyConfig(),
                                                                      proxyGroupMode,
                                                                      new WrappedMap());

                               CloudNet.getInstance().getConfig().createGroup(proxyGroup);
                               CloudNet.getInstance().getProxyGroups().put(proxyGroup.getName(), proxyGroup);
                               commandSender.sendMessage("The proxy group " + proxyGroup.getName() + " was created!");
                               CloudNet.getInstance().setupProxy(proxyGroup);
                               CloudNet.getInstance().toWrapperInstances(wrappers).forEach(Wrapper::updateWrapper);
                               CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
                               CloudNet.getInstance().getConsoleRegistry().unregisterInput(SetupProxyGroup.class);
                           }).request(new SetupRequest("memory",
                                                       "How much memory should each proxy of this proxy group have (in mb)?",
                                                       "Specified amount of memory is invalid",
                                                       IntegerResponseType.getInstance(),
                                                       key -> Integer.parseInt(key) > 64, null
        ))
                           .request(new SetupRequest("startport",
                                                     "What should be the starting port of the proxy group?",
                                                     "Specified starting port is invalid",
                                                     IntegerResponseType.getInstance(),
                                                     key -> Integer.parseInt(key) > 128 &&
                                                         Integer.parseInt(key) < 65536, null
                           ))
                           .request(new SetupRequest("startup",
                                                     "How many proxy instances should always be online?",
                                                     "Please enter a positive number",
                                                     IntegerResponseType.getInstance(),
                                                     key -> Integer.parseInt(key) >= 0, null
                           ))
                           .request(new SetupRequest("mode",
                                                     "Should the group be STATIC or DYNAMIC?",
                                                     "The specified proxy group mode is invalid",
                                                     StringResponseType.getInstance(),
                                                     key -> key.equalsIgnoreCase("STATIC") ||
                                                         key.equalsIgnoreCase("DYNAMIC"), null
                           ))
                           .request(new SetupRequest("template",
                                                     "What should be the backend of the group's default template? [\"LOCAL\" for a wrapper local backend | \"MASTER\" for the master backend]",
                                                     "The specified backend is invalid",
                                                     StringResponseType.getInstance(),
                                                     key -> key.equals("MASTER") || key.equals("LOCAL"),
                                                     null
                           ))
                           .request(new SetupRequest("wrapper",
                                                     "Which wrappers should be used for this group?",
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
                                                     }, null
                           ));
    }

    public String getName() {
        return name;
    }

}
