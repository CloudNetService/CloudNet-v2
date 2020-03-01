package de.dytanic.cloudnet;

import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.event.EventListener;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.api.CoreModule;
import de.dytanic.cloudnetcore.api.event.server.ServerAddEvent;
import de.dytanic.cloudnetcore.process.CoreServerProcessBuilder;

/**
 * Created by Tareko on 15.10.2017.
 */
public class CloudCoreExample extends CoreModule { //extend the CoreModule class to implement the Module system

    @Override
    public void onLoad() {
        if (!getUtilFile().exists()) {
            saveUtils(new Document("myutil configuration", "hello world!"));
        }
        if (!getConfigFile().exists()) {
            getConfig().set("myconfiguration", "foo bar");
            saveConfig();
        }
    }

    @Override
    public void onBootstrap() {
        loadConfig();
        System.out.println(getConfig().getString("myconfiguration"));
        getCloud().getCommandManager().registerCommand(new CommandTest());//Register the command test
        getCloud().getEventManager().registerListener(this, new EventListenerExample()); //Register the event listener
    }

    @Override
    public void onShutdown() {

    }

    private static class EventListenerExample implements EventListener<ServerAddEvent> {

        @Override
        public void onCall(ServerAddEvent event) {
            System.out.println(event.getMinecraftServer().getServiceId());
        }
    }

    private static class CommandTest extends Command { //Creates a command with the specified constructor

        public CommandTest() {
            super("test", "cloudnet.command.test", "te");
        }

        @Override
        public void onExecuteCommand(CommandSender sender, String[] args) {
            CoreServerProcessBuilder.create("Lobby").startServer();
        }
    }
}
