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

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudNetWrapperConfig {

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

    public CloudNetWrapperConfig() throws Exception {

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
