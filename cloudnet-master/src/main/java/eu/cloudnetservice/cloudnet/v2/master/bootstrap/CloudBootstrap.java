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

package eu.cloudnetservice.cloudnet.v2.master.bootstrap;

import com.google.gson.stream.JsonToken;
import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.console.ConsoleManager;
import eu.cloudnetservice.cloudnet.v2.console.ConsoleRegistry;
import eu.cloudnetservice.cloudnet.v2.console.SignalManager;
import eu.cloudnetservice.cloudnet.v2.console.completer.CloudNetCompleter;
import eu.cloudnetservice.cloudnet.v2.console.logging.JlineColoredConsoleHandler;
import eu.cloudnetservice.cloudnet.v2.help.HelpService;
import eu.cloudnetservice.cloudnet.v2.help.ServiceDescription;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.SystemTimer;
import eu.cloudnetservice.cloudnet.v2.logging.CloudLogger;
import eu.cloudnetservice.cloudnet.v2.logging.LoggingFormatter;
import eu.cloudnetservice.cloudnet.v2.logging.handler.ColoredConsoleHandler;
import eu.cloudnetservice.cloudnet.v2.master.CloudConfig;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.completer.ArgumentCompleter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CloudBootstrap {

    private static final InternalLoggerFactory INTERNAL_LOGGER_FACTORY = InternalLoggerFactory.getDefaultFactory();

    public static synchronized void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

        InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE);

        OptionParser optionParser = new OptionParser();

        optionParser.allowsUnrecognizedOptions();
        optionParser.acceptsAll(Arrays.asList("version", "v"));
        optionParser.acceptsAll(Arrays.asList("help", "?"));
        optionParser.accepts("notifyWrappers");
        optionParser.accepts("disable-autoupdate");
        optionParser.accepts("debug");
        optionParser.accepts("noconsole");
        optionParser.accepts("systemTimer");
        optionParser.accepts("disable-statistics");
        optionParser.accepts("disable-modules");
        optionParser.accepts("installWrapper");
        optionParser.accepts("onlyConsole");

        OptionSet optionSet = optionParser.parse(args);

        if (optionSet.has("help") || optionSet.has("?")) {
            HelpService helpService = new HelpService();
            helpService.getDescriptions().put("help",
                                              new ServiceDescription[] {new ServiceDescription("--help | --?",
                                                                                               "This is the main argument to get all information about other parameters")});
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
            System.out.println(helpService);
            return;
        }

        if (optionSet.has("version")) {
            System.out.printf("CloudNet-Core RezSyM Version %s-%s%n",
                              CloudBootstrap.class.getPackage().getImplementationVersion(),
                              CloudBootstrap.class.getPackage().getSpecificationVersion());
            return;
        }
        ConsoleManager consoleManager = new ConsoleManager(new ConsoleRegistry(), new SignalManager());
        CloudConfig coreConfig = new CloudConfig();
        Executors.newSingleThreadExecutor().execute(() -> {
            if (!optionSet.has("noconsole")) {
                consoleManager.useDefaultConsole();
                consoleManager.setRunning(true);
                final LineReader lineReader = consoleManager.getLineReader();
                lineReader.option(LineReader.Option.GROUP, coreConfig.isShowGroup());
                lineReader.option(LineReader.Option.ERASE_LINE_ON_FINISH, coreConfig.isElof());
                lineReader.option(LineReader.Option.AUTO_GROUP, coreConfig.isShowGroup());
                lineReader.option(LineReader.Option.MENU_COMPLETE, coreConfig.isShowMenu());
                lineReader.option(LineReader.Option.AUTO_MENU, coreConfig.isShowMenu());
                lineReader.option(LineReader.Option.AUTO_LIST, coreConfig.isAutoList());
                if (lineReader instanceof LineReaderImpl) {
                    Completer completer = ((LineReaderImpl) lineReader).getCompleter();
                    if (completer instanceof ArgumentCompleter) {
                        ((CloudNetCompleter) ((ArgumentCompleter) completer).getCompleters().get(0)).setGroupColor(coreConfig.getGroupColor());
                        ((CloudNetCompleter) ((ArgumentCompleter) completer).getCompleters().get(0)).setShowDescription(coreConfig.isShowDescription());
                        ((CloudNetCompleter) ((ArgumentCompleter) completer).getCompleters().get(0)).setColor(coreConfig.getColor());
                    }
                }
                consoleManager.startConsole();
            } else {
                while (!Thread.currentThread().isInterrupted()) {
                    NetworkUtils.sleepUninterruptedly(Long.MAX_VALUE);
                }
            }

            System.out.println("Shutting down now!");
        });
        CloudLogger cloudNetLogging = new CloudLogger();
        if (optionSet.has("debug")) {
            cloudNetLogging.setDebugging(true);
        }
        if (optionSet.has("systemTimer")) {
            CloudNet.getExecutor().scheduleWithFixedDelay(SystemTimer::run, 0, 1, TimeUnit.SECONDS);
        }
        CloudNet cloudNetCore = new CloudNet(coreConfig, cloudNetLogging, optionSet, Arrays.asList(args), consoleManager);
        if (!cloudNetCore.bootstrap()) {
            System.exit(0);
        }
        cloudNetCore.getConsoleRegistry().registerInput(cloudNetCore.getCommandManager());
        cloudNetCore.getConsoleManager().changeConsoleInput(CommandManager.class);
        NetworkUtils.header();
        cloudNetCore.getCommandManager().setShowDescription(coreConfig.isShowDescription());
        cloudNetCore.getCommandManager().setAliases(coreConfig.isAliases());
        System.out.println("Use the command \"§ehelp§r\" for further information!");
    }
}
