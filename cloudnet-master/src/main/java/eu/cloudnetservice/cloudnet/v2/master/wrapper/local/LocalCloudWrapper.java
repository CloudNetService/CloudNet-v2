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

package eu.cloudnetservice.cloudnet.v2.master.wrapper.local;

import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.user.BasicUser;
import eu.cloudnetservice.cloudnet.v2.lib.user.User;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import eu.cloudnetservice.cloudnet.v2.master.network.components.WrapperMeta;
import eu.cloudnetservice.cloudnet.v2.setup.spigot.SetupSpigotVersion;
import eu.cloudnetservice.cloudnet.v2.web.client.WebClient;
import joptsimple.OptionSet;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class LocalCloudWrapper implements Consumer<OptionSet>, Closeable {

    private static final String WRAPPER_URL = "https://ci.cloudnetservice.eu/job/CloudNetService/job/CloudNet/job/master/lastSuccessfulBuild/artifact/cloudnet-wrapper/target/CloudNet-Wrapper.jar";

    private Process process;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private boolean shutdown = false;
    private boolean enabled;
    private boolean showConsoleOutput = !Boolean.getBoolean("cloudnet.localwrapper.disableConsole");
    private LocalWrapperConfig config;

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isShowConsoleOutput() {
        return showConsoleOutput;
    }

    public void setShowConsoleOutput(boolean showConsoleOutput) {
        this.showConsoleOutput = showConsoleOutput;
    }

    public Wrapper getWrapper() {
        String wrapperId = this.loadWrapperConfiguration().getString("general.wrapperId");
        return CloudNet.getInstance().getWrappers().get(wrapperId);
    }

    public Configuration loadWrapperConfiguration() {
        if (this.config == null || this.config.isOutdated()) {
            try (InputStream inputStream = Files.newInputStream(Paths.get("wrapper/config.yml"))) {
                this.config = new LocalWrapperConfig(ConfigurationProvider.getProvider(YamlConfiguration.class).load(inputStream));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return this.config != null ? this.config.getConfiguration() : null;
    }

    public void installUpdate(WebClient webClient) {
        Path path = Paths.get("wrapper/CloudNet-Wrapper.jar");
        if (Files.exists(path)) {
            boolean runningBeforeUpdate = false;
            if (this.process != null && this.process.isAlive()) {
                this.shutdown = true;
                try {
                    this.stop();
                    runningBeforeUpdate = true;
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            webClient.updateLocalCloudWrapper(path);
            if (runningBeforeUpdate) {
                try {
                    this.startProcess();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    public void accept(OptionSet obj) {
        if (obj.has("installWrapper")) {
            try {
                if (!Files.exists(Paths.get("wrapper"))) {
                    Files.createDirectories(Paths.get("wrapper"));
                }

                this.setupWrapperJar();
                this.setupConfig();
                this.setupWrapperKey();
                this.setupSpigot(obj);

            } catch (IOException exception) {
                exception.printStackTrace();
            }

            this.startup();
            this.enabled = true;
        }
    }

    // -------------------- SETUP --------------------

    private void setupWrapperJar() {
        Path path = Paths.get("wrapper/CloudNet-Wrapper.jar");
        if (!Files.exists(path)) {
            try {
                System.out.println("Downloading wrapper...");
                URLConnection urlConnection = new URL(WRAPPER_URL).openConnection();
                urlConnection.setRequestProperty("User-Agent",
                                                 NetworkUtils.USER_AGENT);
                urlConnection.connect();
                Files.copy(urlConnection.getInputStream(), path);
                System.out.println("Download completed!");
            } catch (Exception exception) {
                System.err.println("Error on setting up wrapper: " + exception.getMessage());
                return;
            }
        }
    }

    private void setupConfig() {
        Path path = Paths.get("wrapper/config.yml");
        if (!Files.exists(path)) {
            ConnectableAddress address = CloudNet.getInstance().getConfig().getAddresses().iterator().next();
            User user = CloudNet.getInstance().getUser("Wrapper-1");
            if (user == null) {
                String password = NetworkUtils.randomString(32);
                System.out.println("PASSWORD FOR USER \"Wrapper-1\": " + password);
                user = new BasicUser("Wrapper-1", password, Collections.singletonList("*"));
                CloudNet.getInstance().getUsers().add(user);
                CloudNet.getInstance().getConfig().save(CloudNet.getInstance().getUsers());
            }

            final User finalUser = user;
            WrapperMeta wrapperMeta = CloudNet.getInstance()
                                              .getConfig()
                                              .getWrappers()
                                              .stream()
                                              .filter(meta -> meta.getId()
                                                                  .equals("Wrapper-1"))
                                              .findFirst()
                                              .orElseGet(() -> {
                                                  WrapperMeta newMeta = new WrapperMeta("Wrapper-1",
                                                                                        address.getHostName(),
                                                                                        finalUser.getName());
                                                  CloudNet.getInstance().getConfig().createWrapper(newMeta);
                                                  return newMeta;
                                              });

            long memory = ((NetworkUtils.systemMemory() / 1048576) - 2048);
            if (memory < 1024) {
                System.out.println("WARNING: YOU CAN'T USE THE CLOUD NETWORK SOFTWARE WITH SUCH A SMALL MEMORY SIZE!");
            }

            Configuration configuration = new Configuration();
            configuration.set("connection.cloudnet-host", address.getHostName());
            configuration.set("connection.cloudnet-port", address.getPort());
            configuration.set("connection.cloudnet-web", CloudNet.getInstance().getConfig().getWebServerConfig().getPort());
            configuration.set("general.wrapperId", wrapperMeta.getId());
            configuration.set("general.internalIp", wrapperMeta.getHostName());
            configuration.set("general.proxy-config-host", wrapperMeta.getHostName());
            configuration.set("general.max-memory", memory);
            configuration.set("general.startPort", 41570);
            configuration.set("general.auto-update", false);
            configuration.set("general.saving-records", false);
            configuration.set("general.maintenance-copyFileToDirectory", false);
            configuration.set("general.processQueueSize", (Runtime.getRuntime().availableProcessors() / 2));
            configuration.set("general.percentOfCPUForANewServer", 100D);
            configuration.set("general.percentOfCPUForANewProxy", 100D);

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, outputStreamWriter);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void setupWrapperKey() {
        try {
            Files.copy(Paths.get("WRAPPER_KEY.cnd"), Paths.get("wrapper/WRAPPER_KEY.cnd"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void setupSpigot(OptionSet obj) {
        Path path = Paths.get("wrapper/local/spigot.jar");
        if (!obj.has("disallow_bukkit_download") && !Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            SetupSpigotVersion setup = new SetupSpigotVersion();
            setup.setTarget(path);
            setup.accept(CloudNet.getLogger().getReader());
        }
    }

    // -------------------- SETUP --------------------

    // -------------------- PROCESS --------------------

    private void startup() {
        System.out.println("Starting local wrapper...");
        try {
            this.startProcess();

            System.out.println("Successfully started the local wrapper!");
        } catch (IOException exception) {
            System.err.println("Failed to start the local wrapper!");
            exception.printStackTrace();
        }
    }

    private void startProcess() throws IOException {
        System.out.println("Starting wrapper process...");
        this.process = new ProcessBuilder("java",
                                          "-Xmx256M",
                                          "-Djline.terminal=jline.UnsupportedTerminal",
                                          "-Dcloudnet.logging.prompt.disabled=true",
                                          "-jar",
                                          "CloudNet-Wrapper.jar").directory(new File("wrapper")).start();
        this.initConsoleThread();
        System.out.println("Successfully started the wrapper process!");
    }

    private void initConsoleThread() {
        this.executorService.execute(() -> {
            InputStream inputStream = this.process.getInputStream();
            this.readStream(inputStream, line -> {
                if (this.showConsoleOutput) {
                    System.out.println("LocalWrapper | " + line);
                }
            });
            if (!this.shutdown) {
                try {
                    this.startProcess();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                this.enabled = false;
            }
        });
        this.executorService.execute(() -> {
            InputStream inputStream = this.process.getErrorStream();
            this.readStream(inputStream, line -> {
                if (this.showConsoleOutput) {
                    System.err.println("LocalWrapper | " + line);
                }
            });
        });
    }

    private void readStream(InputStream inputStream, Consumer<String> consumer) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    consumer.accept(line);
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        this.enabled = false;
        if (this.process != null && this.process.isAlive()) {
            this.shutdown = true;
            this.stop();
        }
    }

    private void stop() throws IOException {
        System.out.println("Stopping the local wrapper...");
        this.executeCommand("stop");
        try {
            if (!this.process.waitFor(30, TimeUnit.SECONDS)) {
                this.process.destroy();
            }
            System.out.println("Successfully stopped the local wrapper!");
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public void executeCommand(String command) throws IOException {
        this.process.getOutputStream().write((command + '\n').getBytes(StandardCharsets.UTF_8));
        this.process.getOutputStream().flush();
    }

    public void restart() throws IOException {
        this.stop();
    }

    // -------------------- PROCESS --------------------

}
