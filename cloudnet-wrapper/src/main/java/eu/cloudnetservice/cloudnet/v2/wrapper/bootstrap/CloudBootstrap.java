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

package eu.cloudnetservice.cloudnet.v2.wrapper.bootstrap;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.console.ConsoleManager;
import eu.cloudnetservice.cloudnet.v2.console.ConsoleRegistry;
import eu.cloudnetservice.cloudnet.v2.console.SignalManager;
import eu.cloudnetservice.cloudnet.v2.console.logging.JlineColoredConsoleHandler;
import eu.cloudnetservice.cloudnet.v2.help.HelpService;
import eu.cloudnetservice.cloudnet.v2.help.ServiceDescription;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.SystemTimer;
import eu.cloudnetservice.cloudnet.v2.logging.CloudLogger;
import eu.cloudnetservice.cloudnet.v2.logging.LoggingFormatter;
import eu.cloudnetservice.cloudnet.v2.logging.handler.ColoredConsoleHandler;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapperConfig;
import eu.cloudnetservice.cloudnet.v2.wrapper.util.FileUtility;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;

public class CloudBootstrap {

    public static void main(String[] args) {

        System.setProperty("file.encoding", "UTF-8");

        OptionParser optionParser = new OptionParser();

        optionParser.allowsUnrecognizedOptions();
        optionParser.acceptsAll(Arrays.asList("version", "v"));
        optionParser.acceptsAll(Arrays.asList("help", "?"));
        optionParser.accepts("disable-queue");
        optionParser.accepts("systemTimer");
        optionParser.accepts("noconsole");
        optionParser.accepts("systemTimer");
        optionParser.accepts("debug");
        optionParser.accepts("disable-autoupdate");
        optionParser.accepts("disallow_bukkit_download");

        OptionSet optionSet = optionParser.parse(args);

        if (optionSet.has("help") || optionSet.has("?")) {
            HelpService helpService = new HelpService();
            helpService.getDescriptions().put("help",
                                              new ServiceDescription[] {new ServiceDescription("--help | --?",
                                                                                               "This is the main argument to get all information about other parameters")});
            helpService.getDescriptions().put("noconsole",
                                              new ServiceDescription[] {new ServiceDescription("--noconsole",
                                                                                               "Disables the console, for the rest of the service run time")});
            helpService.getDescriptions().put("disable-autoupdate",
                                              new ServiceDescription[] {new ServiceDescription("--disable-autoupdate",
                                                                                               "Disabled the autoupdate function of cloudnet 2")});
            helpService.getDescriptions().put("version",
                                              new ServiceDescription[] {new ServiceDescription("--version | --v",
                                                                                               "Displays the current version of CloudNet used")});
            helpService.getDescriptions().put("systemTimer",
                                              new ServiceDescription[] {new ServiceDescription("--systemTimer",
                                                                                               "Time all informations of this instance into a custom log file")});
            helpService.getDescriptions().put("requestTerminationSignal",
                                              new ServiceDescription[] {new ServiceDescription("--requestTerminationSignal",
                                                                                               "Enables the request if you use STRG+C")});
            System.out.println(helpService);
            return;
        }

        if (optionSet.has("version") || optionSet.has("v")) {
            System.out.printf("CloudNet-Wrapper RezSyM Version %s-%s%n",
                              CloudBootstrap.class.getPackage().getImplementationVersion(),
                              CloudBootstrap.class.getPackage().getSpecificationVersion());
            return;
        }

        CloudNetWrapperConfig cloudNetWrapperConfig = null;
        try {
            cloudNetWrapperConfig = new CloudNetWrapperConfig();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        if (!Files.exists(cloudNetWrapperConfig.getConfigPath())) {
            System.err.println("config.yml missing close wrapper");
            System.exit(-1);
        }
        cloudNetWrapperConfig = cloudNetWrapperConfig.load();

        ConsoleManager consoleManager = new ConsoleManager(new ConsoleRegistry(), new SignalManager());
        Executors.newSingleThreadExecutor().execute(() -> {
            if (!optionSet.has("noconsole")) {
                consoleManager.useDefaultConsole();
                consoleManager.setRunning(true);
                consoleManager.startConsole();
            } else {
                while (!Thread.currentThread().isInterrupted()) {
                    NetworkUtils.sleepUninterruptedly(Long.MAX_VALUE);
                }
            }


            System.out.println("Shutting down now!");
        });
        /*==============================================*/
        FileUtility.deleteDirectory(new File("temp"));

        if (Files.exists(Paths.get("local"))) {
            FileUtility.deleteDirectory(new File("local/cache"));
        }
        /*==============================================*/

        CloudLogger cloudNetLogging = new CloudLogger();
        if (optionSet.has("debug")) {
            cloudNetLogging.setDebugging(true);
        }

        NetworkUtils.header();
        CloudNetWrapper cloudNetWrapper = new CloudNetWrapper(optionSet, cloudNetWrapperConfig, cloudNetLogging, consoleManager);
        if (optionSet.has("systemTimer")) {
            NetworkUtils.getExecutor().scheduleWithFixedDelay(SystemTimer::run, 0, 1, TimeUnit.SECONDS);
        }

        if (!cloudNetWrapper.bootstrap()) {
            System.exit(0);
        }
        cloudNetWrapper.getConsoleManager().getConsoleRegistry().registerInput(cloudNetWrapper.getCommandManager());
        cloudNetWrapper.getConsoleManager().changeConsoleInput(CommandManager.class);
        System.out.println("Use the command \"§ehelp§r\" for further information!");
    }
}
