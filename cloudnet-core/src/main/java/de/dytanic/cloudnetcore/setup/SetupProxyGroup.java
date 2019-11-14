/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.setup;

import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyGroupMode;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;
import de.dytanic.cloudnet.setup.Setup;
import de.dytanic.cloudnet.setup.SetupRequest;
import de.dytanic.cloudnet.setup.SetupResponseType;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.util.defaults.BasicProxyConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Tareko on 23.10.2017.
 */
public class SetupProxyGroup {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private String name;

    public SetupProxyGroup(CommandSender commandSender, String name) {
        this.name = name;

        Setup setup = new Setup().setupCancel(() -> commandSender.sendMessage("Setup cancelled!"))
                                 .setupComplete(data -> {

                                     // Make sure there is at least one valid wrapper
                                     List<String> wrappers = new ArrayList<>(Arrays.asList(data.getString("wrapper").split(",")));
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
                                     for (Wrapper wrapper : CloudNet.getInstance().toWrapperInstances(wrappers)) {
                                         wrapper.updateWrapper();
            }
        }).request(new SetupRequest("memory",
                                    "How many MB of RAM should the proxy group have?",
                                    "Specified memory is invalid",
                                    SetupResponseType.NUMBER,
                                    key -> NetworkUtils.checkIsNumber(key) &&
                                        Integer.parseInt(key) > 64))
                                 .request(new SetupRequest("startport",
                                                           "What's the starting port of the proxygroup?",
                                                           "Specified starting port is invalid",
                                                           SetupResponseType.NUMBER,
                                                           key -> NetworkUtils.checkIsNumber(key) &&
                                                               Integer.parseInt(key) > 128 &&
                                                               Integer.parseInt(key) < 65536))
                                 .request(new SetupRequest("startup",
                                                           "How many proxys should always be online?",
                                                           "Specified startup count is invalid",
                                                           SetupResponseType.NUMBER,
                                                           null))
                                 .request(new SetupRequest("mode",
                                                           "Should the group be STATIC or DYNAMIC?",
                                                           "Group mode is invalid",
                                                           SetupResponseType.STRING,
                                                           key -> key
                                                               .equalsIgnoreCase("STATIC") ||
                                                               key.equalsIgnoreCase("DYNAMIC")))
                                 .request(
            new SetupRequest("template",
                             "What is the backend of the group default template? [\"LOCAL\" for the wrapper local | \"MASTER\" for the master backend]",
                             "String is invalid",
                             SetupResponseType.STRING,
                             key -> key.equals("MASTER") || key.equals("LOCAL")))
                                 .request(new SetupRequest("wrapper",
                                                           "Which wrappers should be used for this group?",
                                                           "String is invalid",
                                                           SetupResponseType.STRING,
                                                           key -> {
                                                               // Make sure there is at least one valid wrapper
                                                               List<String> wrappers = new ArrayList<>(Arrays.asList(key.split(",")));
                                                               if (wrappers.size() == 0) {
                                                                   return false;
                                                               }
                                                               for (short i = 0; i < wrappers.size(); i++) {
                                                                   if (!CloudNet.getInstance()
                                                                                .getWrappers()
                                                                                .containsKey(wrappers.get(i))) {
                                                                       wrappers.remove(wrappers.get(i));
                                                                   }
                                                               }
                                                               return wrappers.size() != 0;
                                                          }));
        setup.start(CloudNet.getLogger().getReader());
    }

    public String getName() {
        return name;
    }
}
