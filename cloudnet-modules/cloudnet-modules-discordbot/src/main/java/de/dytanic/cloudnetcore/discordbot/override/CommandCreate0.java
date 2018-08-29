package de.dytanic.cloudnetcore.discordbot.override;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.map.WrappedMap;
import de.dytanic.cloudnet.lib.server.*;
import de.dytanic.cloudnet.lib.server.advanced.AdvancedServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.server.version.ProxyVersion;
import de.dytanic.cloudnet.lib.user.BasicUser;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.discordbot.DiscordBot;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.WrapperMeta;
import de.dytanic.cloudnetcore.util.defaults.BasicProxyConfig;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CommandCreate0 extends Command
{
    private final TextChannel textChannel = DiscordBot.getTextChannel();
    private final String[] MODE = {"static", "static_lobby", "lobby", "dynamic"};
    private final String[] MODE_PROXY = {"static", "dynamic"};
    private final String[] TYPE = {"bukkit", "cauldron", "glowstone"};
    private final String[] TEMPLATE = {"local", "master"};

    public CommandCreate0()
    {
        super("create0", "cloudnet.command.create0");
    }

    @Override
    public void onExecuteCommand(CommandSender sender, String[] args)
    {
        if (args.length > 2 && args[0].equalsIgnoreCase("dispatchCommand"))
        {
            StringBuilder builder = new StringBuilder();
            for (short i = 2; i < args.length; i++)
            {
                builder.append(args[i]);
            }

            CloudNet.getInstance().getDbHandlers().getCommandDispatcherDatabase().appendCommand(
                    args[1], builder.substring(0, (builder.substring(0)
                            .endsWith(" ") ? builder.length() - 1 : builder.length())));
            textChannel.sendMessage("A dispatcher was created \"" + args[1] + "\": \"" + builder.substring(0) + "\"").queue();
            return;
        }

        switch (args.length)
        {
            /**
             * servergroup
             * name
             * ram
             * always_online
             * percent_new_server
             * group_mode (STATIC, STATIC_LOBBY, LOBBY, DYNAMIC
             * servergroup_type (BUKKIT, CAULDRON, GLOWSTONE)
             * template (LOCAL, MASTER)
             * 100 player online group
             * 100 player online global
             * Wrapper_name
             */
            case 11:
            {
                if (args[0].equalsIgnoreCase("servergroup"))
                {
                    if (NetworkUtils.checkIsNumber(args[3]) && NetworkUtils.checkIsNumber(args[4])
                            && Arrays.stream(this.MODE).parallel().anyMatch(args[5].toLowerCase()::equals) &&
                            Arrays.stream(this.TYPE).parallel().anyMatch(args[6].toLowerCase()::equals) &&
                            Arrays.stream(this.TEMPLATE).parallel().anyMatch(args[7].toLowerCase()::equals) &&
                            NetworkUtils.checkIsNumber(args[8]) && NetworkUtils.checkIsNumber(args[9]) && CloudNet.getInstance().getWrappers().containsKey(args[10])) {
                        List<String> wrappers = (List<String>) CollectionWrapper.toCollection(args[10], ",");
                        if (wrappers.size() == 0) return;
                        for (short i = 0; i < wrappers.size(); i++)
                        {
                            if (!CloudNet.getInstance().getWrappers().containsKey(wrappers.get(i)))
                            {
                                wrappers.remove(wrappers.get(i));
                            }
                        }
                        if (wrappers.size() == 0) return;

                        ServerGroup serverGroup = new ServerGroup(
                                args[1],
                                wrappers,
                                args[5].equals(ServerGroupMode.LOBBY),
                                Integer.parseInt(args[2]),
                                Integer.parseInt(args[2]),
                                0,
                                true,
                                Integer.parseInt(args[3]),
                                Integer.parseInt(args[9]),
                                Integer.parseInt(args[8]),
                                180,
                                100,
                                100,
                                Integer.parseInt(args[4]),
                                ServerGroupType.valueOf(args[6].toUpperCase()),
                                ServerGroupMode.valueOf(args[5].toUpperCase()),
                                Arrays.asList(new Template(
                                        "default",
                                        TemplateResource.valueOf(args[7].toUpperCase()),
                                        null,
                                        new String[0],
                                        new ArrayList<>()
                                )),
                                new AdvancedServerConfig(false, false, false, !ServerGroupMode.valueOf(args[5].toUpperCase()).equals(ServerGroupMode.STATIC)));
                        CloudNet.getInstance().getConfig().createGroup(serverGroup);
                        CloudNet.getInstance().getServerGroups().put(serverGroup.getName(), serverGroup);
                        CloudNet.getInstance().setupGroup(serverGroup);
                        for (Wrapper wrapper : CloudNet.getInstance().toWrapperInstances(wrappers))
                        {
                            wrapper.updateWrapper();
                        }
                        textChannel.sendMessage("The Servergroup (" + serverGroup.getName() + ") was created successfully!").queue();
                    } else
                    {
                        textChannel.sendMessage("Hmmm, there was an Error while parsing he given data! Are you sure that you gave the right information?").queue();
                    }
                    return;
                }
                break;
            }
            /**
             * proxygroup
             * name
             * ram
             * start_port
             * always_online
             * mode (Static, Dynamic)
             * template
             * wrapper
             */
            case 8:
            {
                if (args[0].equalsIgnoreCase("proxygroup"))
                {
                    if (NetworkUtils.checkIsNumber(args[2]) && NetworkUtils.checkIsNumber(args[3]) && NetworkUtils.checkIsNumber(args[4])
                            && Arrays.stream(this.MODE_PROXY).parallel().anyMatch(args[5].toLowerCase()::equals)
                            && Arrays.stream(this.TEMPLATE).parallel().anyMatch(args[6].toLowerCase()::equals) &&
                            CloudNet.getInstance().getWrappers().containsKey(args[7]))
                    {
                        List<String> wrappers = (List<String>) CollectionWrapper.toCollection(args[7], ",");
                        if (wrappers.size() == 0) return;
                        for (short i = 0; i < wrappers.size(); i++)
                        {
                            if (!CloudNet.getInstance().getWrappers().containsKey(wrappers.get(i)))
                            {
                                wrappers.remove(wrappers.get(i));
                            }
                        }
                        if (wrappers.size() == 0) return;

                        ProxyGroup proxyGroup = new ProxyGroup(args[1], wrappers, new Template(
                                "default",
                                TemplateResource.valueOf(args[6].toUpperCase()),
                                null,
                                new String[0],
                                new ArrayList<>()
                        ), ProxyVersion.BUNGEECORD, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[2]), new BasicProxyConfig(),
                                ProxyGroupMode.valueOf(args[5].toUpperCase()), new WrappedMap());

                        CloudNet.getInstance().getConfig().createGroup(proxyGroup);
                        CloudNet.getInstance().getProxyGroups().put(proxyGroup.getName(), proxyGroup);
                        CloudNet.getInstance().setupProxy(proxyGroup);
                        for (Wrapper wrapper : CloudNet.getInstance().toWrapperInstances(wrappers))
                        {
                            wrapper.updateWrapper();
                        }
                        textChannel.sendMessage("The Proxygroup (" + proxyGroup.getName() + ") was created successfully!").queue();
                    } else
                    {
                        textChannel.sendMessage("Hmmm, there was an Error while parsing he given data! Are you sure that you gave the right information?").queue();
                    }
                    return;
                }
                break;
            }
            case 3:
            {
                if ((args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p")) && NetworkUtils.checkIsNumber(args[2]))
                {
                    if (CloudNet.getInstance().getProxyGroups().containsKey(args[1]))
                    {
                        for (short i = 0; i < Integer.parseInt(args[2]); i++)
                        {
                            CloudNet.getInstance().startProxy(CloudNet.getInstance().getProxyGroups().get(args[1]));
                            NetworkUtils.sleepUninterruptedly(2000L);
                        }
                        textChannel.sendMessage("Trying to startup a proxy server...").queue();
                    } else
                    {
                        textChannel.sendMessage("The proxy group doesn't exists").queue();
                    }
                    return;
                }
                if ((args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s")) && NetworkUtils.checkIsNumber(args[2]))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1]))
                    {
                        for (short i = 0; i < Integer.parseInt(args[2]); i++)
                        {
                            CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(args[1]));
                            NetworkUtils.sleepUninterruptedly(2000L);
                        }
                        textChannel.sendMessage("Trying to startup a game server...").queue();
                    } else
                    {
                        textChannel.sendMessage("The server group doesn't exists").queue();
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("user"))
                {
                    if (!CloudNet.getInstance().getUsers().contains(args[1]))
                    {
                        User user = new BasicUser(args[1], args[2], Arrays.asList());
                        CloudNet.getInstance().getUsers().add(user);
                        CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
                        textChannel.sendMessage("The user was created!").queue();
                    } else
                    {
                        textChannel.sendMessage("The user already exists!").queue();
                    }
                    return;
                }
                break;
            }
            case 2:
                {
                if (args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p"))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1]))
                    {
                        CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(args[1]));
                        textChannel.sendMessage("Trying to startup a game server...").queue();
                    } else
                    {
                        textChannel.sendMessage("The server group doesn't exists").queue();
                    }
                    return;
                }
                    if (args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s"))
                    {
                        if (CloudNet.getInstance().getServerGroups().containsKey(args[1]))
                        {
                            CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(args[1]));
                            textChannel.sendMessage("Trying to startup a game server...").queue();
                        } else
                        {
                            textChannel.sendMessage("The server group doesn't exists").queue();
                        }
                        return;
                    }
                break;
            }
            /**
             * wrapper
             * name
             * ip
             * user
             */
            case 4:
            {
                if (args[0].equalsIgnoreCase("wrapper"))
                {
                    if (!args[2].equalsIgnoreCase("127.0.0.1") && !CloudNet.getInstance().getWrappers().containsKey(args[1]))
                    {
                        WrapperMeta wrapperMeta = new WrapperMeta(args[1], args[2], args[3]);
                        CloudNet.getInstance().getConfig().createWrapper(wrapperMeta);
                        textChannel.sendMessage("The Wrapper (" + wrapperMeta.getId() + ") was created successfully!").queue();
                    } else
                    {
                        textChannel.sendMessage("Hmmm, there was an Error while parsing the given data! Are you sure that you gave the right information?").queue();
                    }
                    return;
                }
                if ((args[0].equalsIgnoreCase("proxy") || args[0].equalsIgnoreCase("-p")) && NetworkUtils.checkIsNumber(args[2]))
                {
                    if (CloudNet.getInstance().getProxyGroups().containsKey(args[1]) && CloudNet.getInstance().getWrappers().containsKey(args[3]))
                    {
                        for (short i = 0; i < Integer.parseInt(args[2]); i++)
                        {
                            CloudNet.getInstance().startProxy(CloudNet.getInstance().getWrappers().get(args[3]), CloudNet.getInstance().getProxyGroups().get(args[1]));
                            NetworkUtils.sleepUninterruptedly(2000L);
                        }
                        textChannel.sendMessage("Trying to startup a proxy server...").queue();
                    } else
                    {
                        textChannel.sendMessage("The proxy group or wrapper doesn't exists").queue();
                    }
                    return;
                }
                if ((args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("-s")) && NetworkUtils.checkIsNumber(args[2]))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[1]) && CloudNet.getInstance().getWrappers().containsKey(args[3]))
                    {
                        for (short i = 0; i < Integer.parseInt(args[2]); i++)
                        {
                            CloudNet.getInstance().startGameServer(CloudNet.getInstance().getWrappers().get(args[3]), CloudNet.getInstance().getServerGroups().get(args[1]));
                            NetworkUtils.sleepUninterruptedly(2000L);
                        }
                        textChannel.sendMessage("Trying to startup a game server...").queue();
                    } else
                    {
                        textChannel.sendMessage("The server group or wrapper doesn't exists").queue();
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("cloudserver") || args[0].equalsIgnoreCase("cs"))
                {
                    if (NetworkUtils.checkIsNumber(args[2]))
                    {
                        CloudNet.getInstance().startCloudServer(args[1], Integer.parseInt(args[2]), args[3].equalsIgnoreCase("true"));
                        textChannel.sendMessage("Trying to startup a cloud server...").queue();
                    } else
                    {
                        textChannel.sendMessage("Invalid argument!").queue();
                    }
                    return;
                }
                if (args[0].equalsIgnoreCase("TEMPLATE"))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[2]))
                    {
                        if (args[3].equalsIgnoreCase("LOCAL"))
                        {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(args[2]);
                            serverGroup.getTemplates().add(new Template(args[1], TemplateResource.LOCAL, null, new String[]{}, Arrays.asList()));
                            CloudNet.getInstance().getConfig().createGroup(serverGroup);

                            NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(), CloudNet.getInstance().getConfig().getServerGroups(), new Acceptable<ServerGroup>() {
                                @Override
                                public boolean isAccepted(ServerGroup value)
                                {
                                    return true;
                                }
                            });

                            NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(), CloudNet.getInstance().getConfig().getProxyGroups(), new Acceptable<ProxyGroup>() {
                                @Override
                                public boolean isAccepted(ProxyGroup value)
                                {
                                    return true;
                                }
                            });
                            CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
                                @Override
                                public void accept(Wrapper wrapper)
                                {
                                    wrapper.updateWrapper();
                                }
                            });
                            textChannel.sendMessage("The template was created and all wrappers were updated!").queue();
                        }
                        if (args[3].equalsIgnoreCase("MASTER"))
                        {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(args[2]);
                            serverGroup.getTemplates().add(new Template(args[1], TemplateResource.MASTER, null, new String[]{}, Arrays.asList()));
                            CloudNet.getInstance().getConfig().createGroup(serverGroup);

                            NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(), CloudNet.getInstance().getConfig().getServerGroups(), new Acceptable<ServerGroup>() {
                                @Override
                                public boolean isAccepted(ServerGroup value)
                                {
                                    return true;
                                }
                            });

                            NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(), CloudNet.getInstance().getConfig().getProxyGroups(), new Acceptable<ProxyGroup>() {
                                @Override
                                public boolean isAccepted(ProxyGroup value)
                                {
                                    return true;
                                }
                            });
                            CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
                                @Override
                                public void accept(Wrapper wrapper)
                                {
                                    wrapper.updateWrapper();
                                }
                            });
                            textChannel.sendMessage("The template was created and all wrappers were updated!").queue();
                        }
                    } else
                    {
                        textChannel.sendMessage("The server group doesn't exist").queue();
                    }
                }
                break;
            }
            case 5:
            {
                if (args[0].equalsIgnoreCase("TEMPLATE"))
                {
                    if (CloudNet.getInstance().getServerGroups().containsKey(args[2]))
                    {
                        if (args[3].equalsIgnoreCase("URL"))
                        {
                            ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(args[2]);
                            serverGroup.getTemplates().add(new Template(args[1], TemplateResource.URL, args[4], new String[]{("-Dtest=true")}, Arrays.asList()));
                            CloudNet.getInstance().getConfig().createGroup(serverGroup);

                            NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(), CloudNet.getInstance().getConfig().getServerGroups(), new Acceptable<ServerGroup>() {
                                @Override
                                public boolean isAccepted(ServerGroup value)
                                {
                                    return true;
                                }
                            });

                            NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(), CloudNet.getInstance().getConfig().getProxyGroups(), new Acceptable<ProxyGroup>() {
                                @Override
                                public boolean isAccepted(ProxyGroup value)
                                {
                                    return true;
                                }
                            });
                            CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
                                @Override
                                public void accept(Wrapper wrapper)
                                {
                                    wrapper.updateWrapper();
                                }
                            });
                            textChannel.sendMessage("The template was created and all wrappers were updated!").queue();
                        }
                    } else
                    {
                        textChannel.sendMessage("The server group doesn't exists").queue();
                    }
                }
            }
            default:
            {
                textChannel.sendMessage(
                        "create PROXY <proxyGroup> <count> | Creates a proxy server of a proxy group. <count> is not mandatory\n" +
                        "create PROXY <proxyGroup> <count> <wrapper> | Creates a proxy server of a proxy group. <count> is not mandatory\n" +
                        "create SERVER <serverGroup> <count> | Creates a game server of a server group. <count> is not mandatory\n" +
                        "create SERVER <serverGroup> <count> <wrapper> | Creates a game server of a server group. <count> is not mandatory\n" +
                        "create CLOUDSERVER <name> <memory> <priorityStop>\n" +
                        "create USER <name> <password> | Creates a new user with specified name and password\n" +
                        "create PROXYGROUP <name> <ram> <start_port> <always_online> <group_mode> <template_backend> <wrapper> | Creates a completely new proxy group for BungeeCord with its own configurations, etc.\n" +
                        "create SERVERGROUP <name> <ram> <always_online> <percent_new_server> <group_mode> <server_type> <template_backend> <100_player_online_group> <100_players_online_global> <wrapper_name> | Creates a completely new server group for Minecraft servers with its own configurations, etc.\n" +
                        "create DISPATCHCOMMAND <main-command> <command> | Creates a simple command alias\n" +
                        "create WRAPPER <ip> <name> <user> | Creates and whitelists a new wrapper. The wrapper can also have the same IP of a previous wrapper\n" +
                        "create TEMPLATE <name> <group> LOCAL | Creates a new locale (Wrapper locales) template for a server group\n" +
                        "create TEMPLATE <name> <group> MASTER | Creates a new master backend (Master locales) template for a server group\n" +
                        "create TEMPLATE <name> <group> URL <url> | Creates a new template of a server group via url\n"
                ).queue();
            }
        }
    }
}
