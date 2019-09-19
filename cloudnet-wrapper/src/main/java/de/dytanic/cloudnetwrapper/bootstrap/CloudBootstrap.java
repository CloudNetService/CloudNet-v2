/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.bootstrap;

import de.dytanic.cloudnet.help.HelpService;
import de.dytanic.cloudnet.help.ServiceDescription;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.SystemTimer;
import de.dytanic.cloudnet.logging.CloudLogger;
import de.dytanic.cloudnet.logging.util.HeaderFunction;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;
import de.dytanic.cloudnetwrapper.CloudNetWrapperConfig;
import de.dytanic.cloudnetwrapper.util.FileUtility;
import io.netty.util.ResourceLeakDetector;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class CloudBootstrap {

    public static void main(String[] args) throws Exception {
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
        optionParser.accepts("disable-queue");
        optionParser.accepts("ssl");
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
            helpService.getDescriptions().put("ssl",
                                              new ServiceDescription[] {new ServiceDescription("--ssl",
                                                                                               "Allows SSL encryption via a system-contained certificate or an open SSL certificate")});
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
            System.out.println(helpService.toString());
            return;
        }

        if (optionSet.has("systemTimer")) {
            new SystemTimer();
        }

        if (optionSet.has("version") || optionSet.has("v")) {
            System.out.println("CloudNet-Wrapper RezSyM Version " + CloudBootstrap.class.getPackage()
                                                                                        .getImplementationVersion() + '-' + CloudBootstrap.class
                .getPackage()
                .getSpecificationVersion());
            return;
        }

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

        new HeaderFunction();
        CloudNetWrapperConfig cloudNetWrapperConfig = new CloudNetWrapperConfig(cloudNetLogging.getReader());
        CloudNetWrapper cloudNetWrapper = new CloudNetWrapper(optionSet, cloudNetWrapperConfig, cloudNetLogging);

        if (!cloudNetWrapper.bootstrap()) {
            System.exit(0);
        }

        if (!optionSet.has("noconsole")) {
            System.out.println("Use the command \"help\" for further information!");
            String commandLine;

            String user = System.getProperty("user.name");

            while (true) {
                try {
                    while ((commandLine = cloudNetLogging.readLine(user + '@' + cloudNetWrapper.getWrapperConfig()
                                                                                               .getWrapperId() + " $ ")) != null) {

                        try {
                            if (!cloudNetWrapper.getCommandManager().dispatchCommand(commandLine)) {
                                System.out.println("Command not found. Use the command \"help\" for further information!");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ex) {

                }
            }
        } else {
            while (true) {
                NetworkUtils.sleepUninterruptedly(Long.MAX_VALUE);
            }
        }
    }
}
