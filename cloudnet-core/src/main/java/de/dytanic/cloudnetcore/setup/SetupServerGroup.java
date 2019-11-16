/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.setup;

import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.setup.Setup;
import de.dytanic.cloudnet.setup.SetupRequest;
import de.dytanic.cloudnet.setup.SetupResponseType;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Tareko on 21.10.2017.
 */
public class SetupServerGroup {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final Pattern WRAPPER_SPLITTER = Pattern.compile("\\s?+,\\s?+");
    private String name;
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
                                                                         Collections.singletonList(
                                                                             new Template("default",
                                                                                          TemplateResource.valueOf(data.getString("template")),
                                                                                          null,
                                                                                          EMPTY_STRING_ARRAY,
                                                                                          new ArrayList<>())),
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
                                                     "How many MB RAM should the server group have?",
                                                     "Specified Memory is invalid",
                                                     SetupResponseType.NUMBER,
                                                     key -> NetworkUtils.checkIsNumber(key) && Integer.parseInt(key) > 64))
                           .request(new SetupRequest("startup",
                                                     "How many servers should always be online?",
                                                     "Specified startup count is invalid",
                                                     SetupResponseType.NUMBER,
                                                     key -> NetworkUtils.checkIsNumber(key) && Integer.parseInt(key) > 0))
                           .request(new SetupRequest("percent",
                                                     "How full does the server have to be until a new server is started? (In Percent)?",
                                                     "Specified percent count is invalid",
                                                     SetupResponseType.NUMBER,
                                                     key -> NetworkUtils.checkIsNumber(key) && Integer.parseInt(key) <= 100))
                           .request(new SetupRequest("mode",
                                                     "Which server group mode should be used? [STATIC, STATIC_LOBBY, LOBBY, DYNAMIC]",
                                                     "Specified server group mode is invalid",
                                                     SetupResponseType.STRING,
                                                     key -> key.equalsIgnoreCase("STATIC") ||
                                                         key.equalsIgnoreCase("STATIC_LOBBY") ||
                                                         key.equalsIgnoreCase("LOBBY") ||
                                                         key.equalsIgnoreCase("DYNAMIC")))
                           .request(new SetupRequest("type",
                                                     "Which server group type should be used? [BUKKIT, GLOWSTONE]",
                                                     "Specified group type is invalid",
                                                     SetupResponseType.STRING,
                                                     key -> key.equals("BUKKIT") ||
                                                         key.equals("GLOWSTONE")))
                           .request(new SetupRequest("template",
                                                     "What is the backend of the group default template? [\"LOCAL\" for the wrapper local | \"MASTER\" for the master backend]",
                                                     "Specified string is invalid",
                                                     SetupResponseType.STRING,
                                                     key -> key.equals("MASTER") || key.equals("LOCAL")))
                           .request(new SetupRequest("onlineGroup",
                                                     "How many servers should be online if 100 players are online in the group?",
                                                     "Specified string is invalid",
                                                     SetupResponseType.NUMBER,
                                                     key -> NetworkUtils.checkIsNumber(key) && Integer.parseInt(key) > 0))
                           .request(new SetupRequest("onlineGlobal",
                                                     "How many servers should be online if 100 global players are online?",
                                                     "Specified string is invalid",
                                                     SetupResponseType.NUMBER,
                                                     key -> NetworkUtils.checkIsNumber(key) && Integer.parseInt(key) > 0))

                           .request(
                               new SetupRequest("wrapper",
                                                "Which wrappers should be used for this group? (comma-separated list)",
                                                "Specified string is invalid",
                                                SetupResponseType.STRING,
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
