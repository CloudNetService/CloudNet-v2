/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.setup;

import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyGroupMode;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;
import de.dytanic.cloudnet.setup.Setup;
import de.dytanic.cloudnet.setup.SetupRequest;
import de.dytanic.cloudnet.setup.responsetype.IntegerResponseType;
import de.dytanic.cloudnet.setup.responsetype.StringResponseType;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.util.defaults.BasicProxyConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Tareko on 23.10.2017.
 */
public class SetupProxyGroup {

    private static final Pattern WRAPPER_SPLITTER = Pattern.compile("\\s?+,\\s?+");
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private String name;
    private final Setup setup;

    public SetupProxyGroup(CommandSender commandSender, String name) {
        this.name = name;

        setup = new Setup().setupCancel(() -> commandSender.sendMessage("Setup cancelled!"))
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
                           }).request(new SetupRequest("memory",
                                                       "How much memory should each proxy of this proxy group have (in mb)?",
                                                       "Specified amount of memory is invalid",
                                                       IntegerResponseType.getInstance(),
                                                       key -> Integer.parseInt(key) > 64))
                           .request(new SetupRequest("startport",
                                                     "What should be the starting port of the proxy group?",
                                                     "Specified starting port is invalid",
                                                     IntegerResponseType.getInstance(),
                                                     key -> Integer.parseInt(key) > 128 &&
                                                         Integer.parseInt(key) < 65536))
                           .request(new SetupRequest("startup",
                                                     "How many proxy instances should always be online?",
                                                     "Please enter a positive number",
                                                     IntegerResponseType.getInstance(),
                                                     key -> Integer.parseInt(key) >= 0))
                           .request(new SetupRequest("mode",
                                                     "Should the group be STATIC or DYNAMIC?",
                                                     "The specified proxy group mode is invalid",
                                                     StringResponseType.getInstance(),
                                                     key -> key.equalsIgnoreCase("STATIC") ||
                                                         key.equalsIgnoreCase("DYNAMIC")))
                           .request(new SetupRequest("template",
                                                     "What should be the backend of the group's default template? [\"LOCAL\" for a wrapper local backend | \"MASTER\" for the master backend]",
                                                     "The specified backend is invalid",
                                                     StringResponseType.getInstance(),
                                                     key -> key.equals("MASTER") || key.equals("LOCAL")))
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
                                                     }));
    }

    public String getName() {
        return name;
    }

    public void startSetup() {
        setup.start(CloudNet.getLogger().getReader());
    }
}
