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
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by Tareko on 04.10.2017.
 */
public final class CommandLog extends Command {

  private final Gson g = new Gson();

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
          List<String> hasteServer = CloudNet.getInstance().getConfig().getHasteServer().stream().filter(this::checkUrl)
                  .collect(Collectors.toList());
          sendPaste(sender, rndm, hasteServer, minecraftServer);
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
  private void sendPaste(CommandSender sender, String random, List<String> url,
                         MinecraftServer minecraftServer) {
    CloudNet.getInstance().getScheduler().runTaskDelayAsync(() -> {
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
      Stack<String> urls = new Stack();
      urls.addAll(url);
      String s = "";
      while (postTo(sender,s + "/documents", paste)){
        s = urls.pop();
      }
    }, 3*50);
  }

  /**
   * Push the log to url
   * @param url The url to push it
   * @param builder The content of the log
   * @return The finish url
   */
  private boolean postTo(CommandSender sender, String url, StringBuilder builder) {
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

      JsonObject object = g.fromJson(new InputStreamReader(inputStream), JsonObject.class);
      int responseCode = connection.getResponseCode();
      if (responseCode == 200) {
        connection.disconnect();
        sender.sendMessage("You can see the log at: " + String.format(url + "/%s", object.get("key").getAsString()));
        return false;
      } else {
        return true;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  /**
   * Check url is page available
   * @param url The url to check
   * @return Return true if exist and connectible, else return false
   */
  private boolean checkUrl(String url) {
    HttpURLConnection.setFollowRedirects(false);
    HttpURLConnection con = null;
    try {
      con = (HttpURLConnection) new URL(url).openConnection();
      con.setConnectTimeout(5);
      con.setReadTimeout(5);
      con.setRequestMethod("HEAD");
      return con.getResponseCode() == HttpURLConnection.HTTP_OK;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }
}