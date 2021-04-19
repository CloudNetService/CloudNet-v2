package eu.cloudnetservice.cloudnet.v2.master.command;

import eu.cloudnetservice.cloudnet.v2.command.Command;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.command.TabCompletable;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import eu.cloudnetservice.cloudnet.v2.master.network.components.WrapperMeta;
import eu.cloudnetservice.cloudnet.v2.master.setup.SetupCreateWrapper;
import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CommandWrapper extends Command implements TabCompletable {

    /**
     * Constructs a new command with a name, a needed permission and variable aliases.
     */
    public CommandWrapper() {
        super("wrapper", "cloudnet.command.wrapper", "w");
    }

    @Override
    public void onExecuteCommand(final CommandSender sender, ParsedLine parsedLine) {
        if (parsedLine.words().size() <= 1) {
            printHelp(sender);
            return;
        }
        if (parsedLine.words().size() >= 2) {
            if (parsedLine.words().get(1).equalsIgnoreCase("create")) {
                CloudNet.getInstance().getConsoleRegistry().registerInput(new SetupCreateWrapper(sender));
                CloudNet.getInstance().getConsoleManager().changeConsoleInput(SetupCreateWrapper.class);
                return;
            }
            if (parsedLine.words().get(1).equalsIgnoreCase("list")) {
                sender.sendMessage("Registered Wrappers:");
                CloudNet.getInstance().getConfig().getWrappers().stream().map(WrapperMeta::getId).forEach(wrapperName -> {
                    if (CloudNet.getInstance().getWrappers().containsKey(wrapperName)
                        && CloudNet.getInstance().getWrappers().get(wrapperName).getChannel() != null ) {
                        Wrapper wrapper = CloudNet.getInstance().getWrappers().get(wrapperName);
                        sender.sendMessage(String.format("- §a%s§7(Servers: %d, Proxies: %d, Memory: %d/%dM, CPU: %.2f%%)",
                                                         wrapperName,
                                                         wrapper.getServers().size(),
                                                         wrapper.getProxies().size(),
                                                         wrapper.getUsedMemory(),
                                                         wrapper.getMaxMemory(),
                                                         wrapper.getCpuUsage()
                                                         ));
                    } else {
                        sender.sendMessage("- §e" + wrapperName);
                    }

                });
                return;
            }
            if (parsedLine.words().get(1).equalsIgnoreCase("delete") && parsedLine.words().size() == 3) {
                String wrapperId = parsedLine.words().get(2);
                Optional<WrapperMeta> wrapperMeta = CloudNet.getInstance()
                                                      .getConfig()
                                                      .getWrappers()
                                                      .stream()
                                                      .filter(meta -> meta.getId().equalsIgnoreCase(wrapperId))
                                                      .findFirst();
                if (wrapperMeta.isPresent()) {
                    CloudNet.getInstance().getConfig().deleteWrapper(wrapperMeta.get());
                    sender.sendMessage("§aWrapper " + wrapperId  + " successfully deleted from the system!");
                } else {
                    sender.sendMessage("§cThis Wrapper-Id(" + wrapperId + ") is not registered in the system!");
                }
            } else {
                printHelp(sender);
            }
        }
    }

    private void printHelp(CommandSender sender) {
        sender.sendMessage("wrapper create - Start a wrapper setup to generate a config.yml");
        sender.sendMessage("wrapper list - List all wrappers");
        sender.sendMessage("wrapper delete <Wrapper-ID> - Delete a wrapper based on the ID from System");
        return;
    }

    @Override
    public List<Candidate> onTab(ParsedLine parsedLine) {
        List<Candidate> strings = new ArrayList<>();
        if (parsedLine.words().size() == 1) {
            if (parsedLine.words().get(0).equalsIgnoreCase("wrapper")) {
                strings.add(new Candidate("create", "create", null, "Create a new wrapper configuration", null, null, true));
                strings.add(new Candidate("list", "list", null, "List all wrapper configurations", null, null, true));
                strings.add(new Candidate("delete", "delete", null, "Delete a wrapper from system", null, null, true));
            }
        }
        if (parsedLine.words().size() == 2) {
            if (parsedLine.words().get(0).equalsIgnoreCase("wrapper")) {
                if (parsedLine.words().get(1).equalsIgnoreCase("delete")) {
                    CloudNet.getInstance().getConfig().getWrappers().forEach(wrapperMeta ->
                    strings.add(new Candidate(wrapperMeta.getId(), wrapperMeta.getId(), "wrapper", "Wrappername", null, null, true)));
                }
            }
        }
        return strings;
    }
}
