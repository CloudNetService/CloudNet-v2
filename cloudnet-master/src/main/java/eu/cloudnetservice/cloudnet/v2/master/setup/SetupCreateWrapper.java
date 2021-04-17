package eu.cloudnetservice.cloudnet.v2.master.setup;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.command.CommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.lib.utility.Constants;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.WrapperMeta;
import eu.cloudnetservice.cloudnet.v2.setup.Setup;
import eu.cloudnetservice.cloudnet.v2.setup.SetupRequest;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.IntegerResponseType;
import eu.cloudnetservice.cloudnet.v2.setup.responsetype.StringResponseType;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.jline.reader.Candidate;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SetupCreateWrapper extends Setup {


    public SetupCreateWrapper(CommandSender sender) {
        super(CloudNet.getInstance().getConsoleManager());
        this.setupCancel(() -> {
            System.out.println("Setup was cancelled");
            CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
        })
            .setupComplete(data -> {
                String host = data.getString("listingIp");
                Integer memory = data.getInt("memory");
                Integer queue = data.getInt("queue");
                String wrapperId = data.getString("wrapperId");
                String user = data.getString("user");
                try {
                    InetAddressValidator validator = new InetAddressValidator();
                    if (!validator.isValid(host)) {
                        throw new UnknownHostException("No valid InetAddress found!");
                    }
                    InetAddress hostInet = InetAddress.getByName(host);
                    WrapperMeta wrapperMeta = new WrapperMeta(wrapperId, hostInet, user);
                    CloudNet.getInstance().getConfig().createWrapper(wrapperMeta);
                    sender.sendMessage(String.format("Wrapper [%s] was registered on CloudNet",
                                                     wrapperMeta.getId()));
                    Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getClass().getClassLoader().getResourceAsStream(
                        "files/example-wrapper-config.yml"));
                    configuration.set("connection.cloudnet-host",
                                      CloudNet.getInstance()
                                              .getConfig()
                                              .getAddresses()
                                              .toArray(new ConnectableAddress[0])[0].getHostName().getHostAddress());
                    configuration.set("general.wrapperId", wrapperId);
                    configuration.set("general.internalIp", host);
                    configuration.set("general.proxy-config-host", host);
                    configuration.set("general.max-memory", memory);
                    configuration.set("general.processQueueSize", queue);
                    try {
                        Files.createDirectories(Paths.get("local", "wrapper", wrapperId));
                        Files.copy(Paths.get("WRAPPER_KEY.cnd"), Paths.get("local", "wrapper", wrapperId, "WRAPPER_KEY.cnd"));
                    } catch (IOException e) {
                        throw new RuntimeException("Wrapper Key path " + Paths.get("local", "wrapper", wrapperId, "WRAPPER_KEY.cnd")
                                                                              .toAbsolutePath() + " could not be created", e);
                    }
                    try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("local",
                                                                                                                        "wrapper",
                                                                                                                        wrapperId,
                                                                                                                        "config.yml")),
                                                                                        StandardCharsets.UTF_8)) {

                        ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
                    } catch (IOException e) {
                        throw new RuntimeException("Wrapper config path " + Paths.get("local", "wrapper", wrapperId, "config.yml")
                                                                                 .toAbsolutePath() + " could not be saved", e);
                    }
                    sender.sendMessage(String.format("Wrapper [%s] config is generated under -> %s",
                                                     wrapperMeta.getId(), "local/wrapper/" + wrapperId));

                    sender.sendMessage("WRAPPER_KEY.cnd is also copied to local/wrapper/" + wrapperId);
                    CloudNet.getInstance().getConsoleManager().changeConsoleInput(CommandManager.class);
                } catch (UnknownHostException e) {
                    CloudNet.getLogger().log(Level.SEVERE, "Error create a new wrapper", e);
                }

            })

            .request(new SetupRequest("wrapperId",
                                      "What is the ID of this wrapper?",
                                      "",
                                      StringResponseType.getInstance(),
                                      key -> true, null))
            .request(new SetupRequest("listingIp",
                                      "What is the listing address of the wrapper for proxies and servers?",
                                      "The specified IP address is invalid!",
                                      StringResponseType.getInstance(),
                                      key -> Constants.IPV4_PATTERN.matcher(key).matches()
                                          || Constants.IPV6_STD_PATTERN.matcher(key).matches()
                                          || Constants.IPV6_HEX_COMPRESSED_PATTERN.matcher(key).matches(),
                                      () -> {
                                          InetAddress localhost = null;
                                          try {
                                              localhost = InetAddress.getLocalHost();
                                          } catch (UnknownHostException e) {
                                              CloudNet.getLogger().log(Level.SEVERE, "Error create a new wrapper", e);
                                          }
                                          if (localhost != null) {
                                              try {
                                                  InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
                                                  List<Candidate> candidates = new ArrayList<>();
                                                  for (final InetAddress ip : allMyIps) {
                                                      candidates.add(new Candidate(ip.getHostAddress()));
                                                  }
                                                  return candidates;
                                              } catch (UnknownHostException e) {
                                                  CloudNet.getLogger().log(Level.SEVERE, "Error create a new wrapper", e);
                                              }
                                          }

                                          return new ArrayList<>();
            }
            ))
            .request(new SetupRequest("user",
                                      "What is the user of the wrapper?",
                                      "The specified user does not exist!",
                                      StringResponseType.getInstance(),
                                      key -> CloudNet.getInstance().getUser(key) != null,
                                      () -> CloudNet.getInstance().getUsers().stream().map(u -> new Candidate(u.getName())).collect(Collectors.toList())))
            .request(new SetupRequest("memory",
                                      "How many memory is allowed to use for the wrapper services?",
                                      "No negative numbers or zero allowed",
                                      IntegerResponseType.getInstance(),
                                      key -> Integer.parseInt(key) > 0, () -> {
                List<Candidate> candidates = new ArrayList<>();
                int last = 1024;
                for (int i = 0; i < 8; i++) {
                    candidates.add(new Candidate(String.valueOf(last)));
                    last = last * 2;
                }
                return candidates;
            }))
            .request(new SetupRequest("queue",
                                      "How large should the server queue be?",
                                      "No negative numbers or zero allowed",
                                      IntegerResponseType.getInstance(),
                                      key -> Integer.parseInt(key) > 0,
                                      () -> {
                                          List<Candidate> candidates = new ArrayList<>();
                                          for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
                                              candidates.add(new Candidate(String.valueOf(i+1)));
                                          }
                                          return candidates;
                                      }));
    }
}
