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

package eu.cloudnetservice.cloudnet.v2.wrapper;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import jline.console.ConsoleReader;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudNetWrapperConfig {

    private static final InetAddressValidator IP_VALIDATOR = new InetAddressValidator();

    private final Path configPath = Paths.get("config.yml");

    private Configuration configuration;

    private InetAddress internalIP;
    private String wrapperId;
    private InetAddress proxyConfigHost;

    private boolean savingRecords, autoUpdate, maintenanceCopy;

    private InetAddress cloudNetHost;
    private int cloudNetPort;
    private int startPort;
    private int processQueueSize;
    private int maxMemory;
    private int webPort;

    private double percentOfCPUForANewServer;
    private double percentOfCPUForANewProxy;

    public CloudNetWrapperConfig(ConsoleReader reader) throws Exception {

        for (final Path path : new Path[] {
            Paths.get("local", "servers"),
            Paths.get("local", "templates"),
            Paths.get("local", "cache", "web_templates"),
            Paths.get("local", "cache", "web_plugins"),
            Paths.get("local", "records"),
            Paths.get("local", "global", "plugins"),
            Paths.get("local", "plugins"),
            Paths.get("local", "proxy_versions"),
            Paths.get("temp")
        }) {
            Files.createDirectories(path);
        }

        if (!Files.exists(configPath)) {
            Files.createFile(configPath);

            String hostName = NetworkUtils.getHostName();
            if (System.getProperty("hostAddress") != null) {
                hostName = System.getProperty("hostAddress");
            }


            String input;
            System.out.println("Please provide the host IP address for services on this wrapper.");
            System.out.println("Using an IPv6 address might prevent some players from joining!");
            System.out.println("Use 0.0.0.0 to listen on all network interfaces.");
            System.out.println("Leave empty for 0.0.0.0.");
            while ((input = reader.readLine()) != null) {
                if (input.isEmpty()) {
                    hostName = "0.0.0.0";
                    break;
                }
                // Validate the address and try to get a network interface, that is configured with the given IP address.
                // If the address was not found, try again.
                if (IP_VALIDATOR.isValid(input) && NetworkInterface.getByInetAddress(InetAddress.getByName(input)) != null) {
                    hostName = input;
                    break;
                } else {
                    System.out.println("Please provide an IP address that is configured on one of your network adapters.");
                }
            }


            String wrapperId;
            if (System.getProperty("wrapper-id") != null) {
                wrapperId = System.getProperty("wrapper-id");
            } else {
                System.out.println("Please provide the name of this wrapper (example: Wrapper-1)");
                wrapperId = reader.readLine().replace(NetworkUtils.SPACE_STRING, NetworkUtils.EMPTY_STRING);

                if (wrapperId.isEmpty()) {
                    wrapperId = "Wrapper-1";
                }
            }

            String cloudNetHost = NetworkUtils.getHostName();

            if (System.getProperty("cloudnet-host") != null) {
                cloudNetHost = System.getProperty("cloudnet-host");
            }

            System.out.println("Please provide the IP address of the CloudNet-Master.");
            System.out.println("Leave empty for loopback.");
            while ((input = reader.readLine()) != null) {
                if (input.isEmpty()) {
                    cloudNetHost = InetAddress.getLoopbackAddress().getHostAddress();
                    break;
                }
                if (IP_VALIDATOR.isValid(input)) {
                    cloudNetHost = input;
                    break;
                } else {
                    System.out.println("Please provide a proper IP address.");
                }
            }

            long memory = ((NetworkUtils.systemMemory() / 1048576) - 2048);
            if (memory < 1024) {
                System.out.println("WARNING: YOU CAN'T USE THE CLOUD NETWORK SOFTWARE WITH SUCH A SMALL MEMORY SIZE!");
            }

            Configuration configuration = new Configuration();
            configuration.set("connection.cloudnet-host", cloudNetHost);
            configuration.set("connection.cloudnet-port", 1410);
            configuration.set("connection.cloudnet-web", 1420);
            configuration.set("general.wrapperId", wrapperId);
            configuration.set("general.internalIp", hostName);
            configuration.set("general.proxy-config-host", hostName);
            configuration.set("general.max-memory", memory);
            configuration.set("general.startPort", 41570);
            configuration.set("general.auto-update", false);
            configuration.set("general.saving-records", false);
            configuration.set("general.maintenance-copyFileToDirectory", false);
            configuration.set("general.processQueueSize", (Runtime.getRuntime().availableProcessors() / 2));
            configuration.set("general.percentOfCPUForANewServer", 100D);
            configuration.set("general.percentOfCPUForANewProxy", 100D);

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(configPath),
                                                                                StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
            }
        }
        load();
    }

    public CloudNetWrapperConfig load() {
        try (InputStream inputStream = Files.newInputStream(configPath); InputStreamReader inputStreamReader = new InputStreamReader(
            inputStream,
            StandardCharsets.UTF_8)) {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStreamReader);

            this.internalIP = InetAddress.getByName(configuration.getString("general.internalIp"));
            this.savingRecords = configuration.getBoolean("general.saving-records");
            this.wrapperId = configuration.getString("general.wrapperId");
            this.startPort = configuration.getInt("general.startPort");
            this.processQueueSize = configuration.getInt("general.processQueueSize");
            this.maxMemory = configuration.getInt("general.max-memory");
            this.autoUpdate = configuration.getBoolean("general.auto-update");
            this.maintenanceCopy = configuration.getBoolean("maintenance-copyFileToDirectory");
            this.percentOfCPUForANewProxy = configuration.getDouble("general.percentOfCPUForANewProxy");
            this.percentOfCPUForANewServer = configuration.getDouble("general.percentOfCPUForANewServer");

            this.cloudNetHost = InetAddress.getByName(configuration.getString("connection.cloudnet-host"));
            this.cloudNetPort = configuration.getInt("connection.cloudnet-port");
            this.webPort = configuration.getInt("connection.cloudnet-web");

            if (!configuration.getSection("general").contains("proxy-config-host")) {
                configuration.set("general.proxy-config-host", this.internalIP.getHostAddress());
                save();
            }

            //generatated configurations

            this.proxyConfigHost = InetAddress.getByName(configuration.getString("general.proxy-config-host"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void save() {
        try (OutputStream outputStream = Files.newOutputStream(configPath); OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
            outputStream)) {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public int getMaxMemory() {
        return maxMemory;
    }

    public int getCloudNetPort() {
        return cloudNetPort;
    }

    public int getProcessQueueSize() {
        return processQueueSize;
    }

    public int getStartPort() {
        return startPort;
    }

    public Path getConfigPath() {
        return configPath;
    }

    public InetAddress getCloudNetHost() {
        return this.cloudNetHost;
    }

    public InetAddress getInternalIP() {
        return internalIP;
    }

    public InetAddress getProxyConfigHost() {
        return proxyConfigHost;
    }

    public String getWrapperId() {
        return wrapperId;
    }

    public double getPercentOfCPUForANewProxy() {
        return percentOfCPUForANewProxy;
    }

    public double getPercentOfCPUForANewServer() {
        return percentOfCPUForANewServer;
    }

    public int getWebPort() {
        return webPort;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public boolean isMaintenanceCopy() {
        return maintenanceCopy;
    }

    public boolean isSavingRecords() {
        return savingRecords;
    }

}
