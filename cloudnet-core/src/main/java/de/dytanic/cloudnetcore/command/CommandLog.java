/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.command;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.dytanic.cloudnet.command.Command;
import de.dytanic.cloudnet.command.CommandSender;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 04.10.2017.
 */
public final class CommandLog extends Command {

  public CommandLog() {
    super("log", "cloudnet.command.log");

    description = "Creates a web server log";

  }

  @Override
  public void onExecuteCommand(CommandSender sender, String[] args) {
    switch (args.length) {
      case 1: {
        MinecraftServer minecraftServer = CloudNet.getInstance().getServer(args[0]);
        if (minecraftServer != null) {
          String rndm = NetworkUtils.randomString(10);
          CloudNet.getInstance().getServerLogManager().append(rndm, minecraftServer.getServerId());
          List<String> hasteServer = CloudNet.getInstance().getConfig().getHasteServer();
          Random r = new Random();
          sendPaste(sender, rndm, hasteServer.get(r.nextInt(hasteServer.size())), minecraftServer);
          String x = new StringBuilder("http://")
              .append(CloudNet.getInstance().getConfig().getWebServerConfig().getAddress())
              .append(":").append(CloudNet.getInstance().getConfig().getWebServerConfig().getPort())
              .append("/cloudnet/log?server=").append(rndm).substring(0);
          sender.sendMessage("You can see the log at: " + x);

          sender.sendMessage("The log is dynamic and will be deleted in 10 minutes");
        } else {
          sender.sendMessage("The server doesn't exist!");
        }
      }
      break;
      default:
        sender.sendMessage("log <server> | Creates a web server log");
        break;
    }
  }

  /**
   * Send the log to a random past site
   * @param sender The sender of the command
   * @param random A random string
   * @param url The url of the paste site
   * @param minecraftServer The minecraft server with the log inside
   */
  private void sendPaste(CommandSender sender, String random, String url,
      MinecraftServer minecraftServer) {
    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.schedule(() -> {
      StringBuilder paste = new StringBuilder();
      paste.append("----------------------------------------------------------------")
          .append('\n');
      paste.append(String.format("Time: %s", ZonedDateTime.now())).append('\n');
      paste.append(String.format("Server: %s", minecraftServer.getServerId())).append('\n');
      paste.append(String.format("Java Version: %s", System.getProperty("java.version")))
          .append('\n');
      paste.append(String.format("User: %s", System.getProperty("user.name")))
          .append('\n');
      paste.append(String.format("OS: %s %s (%s)", System.getProperty("os.name"),
          System.getProperty("os.arch"),
          System.getProperty("os.version")))
          .append('\n');
      paste.append(String.format("Memory Used: %d Mb", (NetworkUtils.system().getTotalPhysicalMemorySize() -
              NetworkUtils.system().getFreePhysicalMemorySize() )/ 1024L)).append('\n');
      paste.append(String.format("Memory Free: %d Mb", NetworkUtils.system().getFreePhysicalMemorySize() / 1024L)).append('\n');
      paste.append(String.format("Memory Max: %d Mb", NetworkUtils.system().getTotalPhysicalMemorySize() / 1024L)).append('\n');
      paste.append(String.format("CPU Cores: %d", NetworkUtils.system().getAvailableProcessors())).append('\n');
      paste.append(String.format("CloudNet Version: %s # %s",
          NetworkUtils.class.getPackage().getSpecificationVersion(),
          NetworkUtils.class.getPackage().getImplementationVersion())).append('\n');

      paste.append("----------------------------------------------------------------").append('\n');
      CloudNet.getInstance().getServerLogManager().getScreenInfos().getS(random).forEach(
          screenInfo -> paste.append(screenInfo.getLine()).append('\n'));
      String key = postTo(url + "/documents", paste);
      sender.sendMessage(
          "You can see the log at: " + String.format(url + "/%s", key));
    }, 3, TimeUnit.SECONDS);
    executorService.shutdown();
  }

  /**
   * Push the log to url
   * @param url The url to push it
   * @param builder The content of the log
   * @return The finish url
   */
  private String postTo(String url, StringBuilder builder) {
    try {
      URL pasteUrl = new URL(url);
      HttpURLConnection connection = (HttpURLConnection) pasteUrl.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "text/plain");
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.connect();
      OutputStream outputStream = connection.getOutputStream();
      outputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
      outputStream.flush();
      outputStream.close();
      InputStream inputStream = connection.getInputStream();
      Gson g = new Gson();
      JsonObject object = g.fromJson(new InputStreamReader(inputStream), JsonObject.class);
      connection.disconnect();
      return object.get("key").getAsString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "KEY_NOT_FOUND";
  }
}