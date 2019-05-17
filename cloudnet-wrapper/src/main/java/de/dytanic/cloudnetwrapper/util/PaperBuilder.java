package de.dytanic.cloudnetwrapper.util;

import com.google.gson.Gson;
import de.dytanic.cloudnetwrapper.models.PaperMCProject;
import de.dytanic.cloudnetwrapper.models.PaperMCProjectVersion;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;
import jline.console.ConsoleReader;

public final class PaperBuilder {
  private static String API_PROJECT_URL = "https://papermc.io/api/v1/paper";
  private static String API_PROJECT_VERSION_URL = "https://papermc.io/api/v1/paper/%s";
  private static String API_PROJECT_VERSION_DOWNLOAD = "https://papermc.io/api/v1/paper/%s/%s/download";
  private static Gson gson = new Gson();
  private static PaperMCProject paperMCProject;
  private static PaperMCProjectVersion paperMCProjectVersion;

  public static void start(ConsoleReader reader){
    try {
      System.out.println("Fetch Versions");
      URLConnection connection = new URL(API_PROJECT_URL).openConnection();
      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
      connection.connect();
      paperMCProject = gson
          .fromJson(new InputStreamReader(connection.getInputStream()), PaperMCProject.class);
      System.out.println("Available PaperSpigot Versions");
      System.out.println("-----------------------------------------------------------------------------");
      System.out.format("%s", "PaperSpigot Version");
      System.out.println();
      System.out.println("-----------------------------------------------------------------------------");
      for (String version : paperMCProject.getVersions()) {
        System.out.format("%s", version);
        System.out.println();
      }
      System.out.println("-----------------------------------------------------------------------------");
      String answer = null;
      while (answer == null) {
        String name = null;
        try {
          name = reader.readLine().toLowerCase();
        } catch (IOException e) {
          e.printStackTrace();
        }
        String finalAwnser = name;
        if (Arrays.stream(paperMCProject.getVersions()).anyMatch(e->e.equalsIgnoreCase(finalAwnser))) {
          answer = name;
          buildPaperVersion(answer);
        }else if(Arrays.stream(paperMCProject.getVersions()).noneMatch(e->e.equalsIgnoreCase(finalAwnser))) {
          System.out.println("Version not exists!");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  private static void buildPaperVersion(String version) throws Exception {
    System.out.println("Fetch Builds");
    URLConnection connection = new URL(String.format(API_PROJECT_VERSION_URL,version)).openConnection();
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
    connection.connect();
    paperMCProjectVersion = gson
        .fromJson(new InputStreamReader(connection.getInputStream()), PaperMCProjectVersion.class);
    connection = new URL(String.format(API_PROJECT_VERSION_DOWNLOAD,version,paperMCProjectVersion.getBuilds().getLatest())).openConnection();
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
    connection.connect();
    File builder = new File("local/builder/papermc");
    File buildFolder = new File(builder,version);
    buildFolder.mkdirs();
    File paperclip = new File(buildFolder,"paperclip.jar");
    if(!paperclip.exists()){
      System.out.println("Download PaperClip");
      try (InputStream inputStream = connection.getInputStream())
      {
        Files.copy(inputStream, Paths.get(paperclip.toURI()), StandardCopyOption.REPLACE_EXISTING);
      }
      Process exec = Runtime.getRuntime()
          .exec("java -jar paperclip.jar", null, buildFolder);
      while (true) {
        if (!exec.isAlive()) {
          System.out.println("Build finish!");
          System.out.println("Copy spigot.jar");
          Files.copy(new FileInputStream(Objects.requireNonNull(
              buildFolder.listFiles(pathname -> pathname.getName().startsWith("paperclip")))[0]), Paths.get("local/spigot.jar"), StandardCopyOption.REPLACE_EXISTING);
          break;
        }else{
          InputStream is = exec.getInputStream();
          InputStreamReader isr = new InputStreamReader(is);
          BufferedReader br = new BufferedReader(isr);
          String line;
          while ((line = br.readLine()) != null) {
            System.out.println(line);
          }
        }
      }
    }else{
      System.out.println("Skipping build");
      System.out.println("Copy spigot.jar");
      try {
        Files.copy(new FileInputStream(Objects.requireNonNull(
            buildFolder.listFiles(pathname -> pathname.getName().startsWith("paper")))[0]), Paths.get("local/spigot.jar"), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

}
