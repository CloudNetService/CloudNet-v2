package eu.cloudnetservice.cloudnet.v2.master.setup;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by Tareko on 21.10.2017.
 */
public class SetupServerGroup extends Setup{

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final Pattern WRAPPER_SPLITTER = Pattern.compile("\\s?+,\\s?+");
    private final String name;
    public SetupServerGroup(CommandSender commandSender, String name) {
        super(CloudNet.getInstance().getConsoleManager());
        this.name = name;

        this.setupCancel(() -> {
            System.out.println("Setup was cancelled");
            CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
            CloudNet.getInstance().getConsoleRegistry().unregisterInput(SetupWrapper.class);
        }).setupComplete(data -> {
                               if (data == null) return;
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
                               CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
                               CloudNet.getInstance().getConsoleRegistry().unregisterInput(SetupServerGroup.class);
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

}
