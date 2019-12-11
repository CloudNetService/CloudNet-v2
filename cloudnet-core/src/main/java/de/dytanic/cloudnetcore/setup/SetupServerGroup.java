/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.setup;

import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.setup.Setup;
import de.dytanic.cloudnet.setup.SetupRequest;
import de.dytanic.cloudnet.setup.responsetype.IntegerResponseType;
import de.dytanic.cloudnet.setup.responsetype.StringResponseType;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Tareko on 21.10.2017.
 */
public class SetupServerGroup {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
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
