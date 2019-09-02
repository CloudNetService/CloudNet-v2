/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.bootstrap;

import de.dytanic.cloudnet.help.HelpService;
import de.dytanic.cloudnet.help.ServiceDescription;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.SystemTimer;
import de.dytanic.cloudnet.logging.CloudLogger;
import de.dytanic.cloudnet.logging.handler.ICloudLoggerHandler;
import de.dytanic.cloudnet.logging.util.HeaderFunction;
import de.dytanic.cloudnetcore.CloudConfig;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.util.ResourceLeakDetector;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Tareko on 24.07.2017.
 */
public final class CloudBootstrap {

    public static synchronized void main(String[] args) throws Exception {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("io.netty.noPreferDirect", "true");
        System.setProperty("client.encoding.override", "UTF-8");
        System.setProperty("io.netty.maxDirectMemory", "0");
        System.setProperty("io.netty.leakDetectionLevel", "DISABLED");
        System.setProperty("io.netty.recycler.maxCapacity", "0");
        System.setProperty("io.netty.recycler.maxCapacity.default", "0");

        OptionParser optionParser = new OptionParser();

        optionParser.allowsUnrecognizedOptions();
        optionParser.acceptsAll(Arrays.asList("version", "v"));
        optionParser.acceptsAll(Arrays.asList("help", "?"));
        optionParser.acceptsAll(Arrays.asList("notifyWrappers"));
        optionParser.acceptsAll(Arrays.asList("disable-autoupdate"));
        optionParser.accepts("debug");
        optionParser.accepts("noconsole");
        optionParser.accepts("ssl");
        optionParser.accepts("systemTimer");
        optionParser.accepts("disable-statistics");
        optionParser.accepts("disable-modules");
        optionParser.accepts("installWrapper");
        optionParser.accepts("onlyConsole");

        OptionSet optionSet = optionParser.parse(args);

        List<String> consolePreInit = new CopyOnWriteArrayList<>();

        if (optionSet.has("help") || optionSet.has("?")) {
            HelpService helpService = new HelpService();
            helpService.getDescriptions().put("help",
                                              new ServiceDescription[] {new ServiceDescription("--help | --?",
                                                                                               "This is the main argument to get all information about other parameters")});
            helpService.getDescriptions().put("ssl",
                                              new ServiceDescription[] {new ServiceDescription("--ssl",
                                                                                               "Allows SSL encryption via a system-contained certificate or an open SSL certificate")});
            helpService.getDescriptions().put("debug",
                                              new ServiceDescription[] {new ServiceDescription("--debug",
                                                                                               "Enables the debug mode, for extra consoles issues with more information, nothing is for people with interest in alto many consoles issues")});
            helpService.getDescriptions().put("noconsole",
                                              new ServiceDescription[] {new ServiceDescription("--noconsole",
                                                                                               "Disables the console, for the rest of the service run time")});
            helpService.getDescriptions().put("notifyWrappers",
                                              new ServiceDescription[] {new ServiceDescription("--notifyWrappers",
                                                                                               "Unites all the consoles that issued all wrapper instances to the master instance")});
            helpService.getDescriptions().put("disable-autoupdate",
                                              new ServiceDescription[] {new ServiceDescription("--disable-autoupdate",
                                                                                               "Disabled the autoupdate function of cloudnet 2")});
            helpService.getDescriptions().put("version",
                                              new ServiceDescription[] {new ServiceDescription("--version | --v",
                                                                                               "Displays the current version of CloudNet used")});
            helpService.getDescriptions().put("systemTimer",
                                              new ServiceDescription[] {new ServiceDescription("--systemTimer",
                                                                                               "Time all informations of this instance into a custom log file")});
            helpService.getDescriptions().put("disable-statistics",
                                              new ServiceDescription[] {new ServiceDescription("--disable-statistics",
                                                                                               "Disables the statistic service from cloudnet")});
            helpService.getDescriptions().put("disable-modules",
                                              new ServiceDescription[] {new ServiceDescription("--disable-modules",
                                                                                               "Modules doesn't working in the \"/modules\" directory")});
            helpService.getDescriptions().put("installWrapper",
                                              new ServiceDescription[] {new ServiceDescription("--installWrapper",
                                                                                               "Install a local wrapper automatic")});
            helpService.getDescriptions().put("requestTerminationSignal",
                                              new ServiceDescription[] {new ServiceDescription("--requestTerminationSignal",
                                                                                               "Enables the request if you use STRG+C")});
            System.out.println(helpService.toString());
            return;
        }

        if (optionSet.has("systemTimer")) {
            new SystemTimer();
        }

        if (optionSet.has("version")) {
            System.out.println("CloudNet-Core RezSyM Version " + CloudBootstrap.class.getPackage()
                                                                                     .getImplementationVersion() + '-' + CloudBootstrap.class
                .getPackage()
                .getSpecificationVersion());
            return;
        }

        CloudLogger cloudNetLogging = new CloudLogger();
        if (optionSet.has("debug")) {
            cloudNetLogging.setDebugging(true);
        }

        cloudNetLogging.getHandler().add(new ICloudLoggerHandler() {
            @Override
            public void handleConsole(String input) {
                if (!CloudNet.RUNNING) {
                    consolePreInit.add(input);
                }
            }
        });

        new HeaderFunction();
        CloudConfig coreConfig = new CloudConfig(cloudNetLogging.getReader());
        CloudNet cloudNetCore = new CloudNet(coreConfig, cloudNetLogging, optionSet, consolePreInit, Arrays.asList(args));

        if (!cloudNetCore.bootstrap()) {
            System.exit(0);
        }

        if (!optionSet.has("noconsole")) {
            System.out.println("Use the command \"help\" for further information!");
            String commandLine;

            String user = System.getProperty("user.name");

            try {
                while (true) {
                    while ((commandLine = cloudNetLogging.readLine(user + "@Master $ ")) != null && CloudNet.RUNNING) {
                        String dispatcher = cloudNetCore.getDbHandlers().getCommandDispatcherDatabase().findDispatcher(commandLine);
                        if (dispatcher != null) {
                            try {
                                if (!cloudNetCore.getCommandManager().dispatchCommand(dispatcher)) {
                                    continue;
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        if (!cloudNetCore.getCommandManager().dispatchCommand(commandLine)) {
                            System.out.println("Command not found. Use the command \"help\" for further information!");
                        }
                    }
                }
            } catch (Exception ex) {

            }
        } else {
            while (true) {
                NetworkUtils.sleepUninterruptedly(Long.MAX_VALUE);
            }
        }
    }
}
