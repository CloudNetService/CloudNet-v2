package de.dytanic.cloudnetwrapper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import jline.console.ConsoleReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class SpigotBuilder {

  private final static String buildToolsUrl = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";
  private final static String versionsUrl = "https://hub.spigotmc.org/versions/";
  public static Process exec;

  /**
   * Start the process to choice the Spigot version And build after choice
   *
   * @param reader to read the answer
   */
  public static void start(final ConsoleReader reader) {
    System.out.println("Fetch Spigot Versions");
    List<String> versions = loadVersions();
    System.out.println("Available Spigot Versions");
    System.out
        .println("-----------------------------------------------------------------------------");
    System.out.println("Spigot Version");
    System.out.println();
    System.out
        .println("-----------------------------------------------------------------------------");
    versions.forEach(v -> {
      System.out.format("%s", v);
      System.out.println();
    });
    System.out
        .println("-----------------------------------------------------------------------------");
    String answer = null;
    while (answer == null) {
      String name = null;
      try {
        name = reader.readLine().toLowerCase();
      } catch (IOException e) {
        e.printStackTrace();
      }
      String finalAnswer = name;
      if (versions.stream().anyMatch(e -> e.equalsIgnoreCase(finalAnswer))) {
        answer = name;
        buildSpigot(finalAnswer);
      } else if (versions.stream().noneMatch(e -> e.equalsIgnoreCase(finalAnswer))) {
        System.out.println("Version does not exist!");
      }
    }
  }

  /**
   * @return A list for available versions of spigot
   */
  private static LinkedList<String> loadVersions() {
    LinkedList<String> array = new LinkedList<>();
    try {
      Document doc = Jsoup.connect(versionsUrl).userAgent(
          "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
          .get();
      for (Element file : doc.select("a")) {
        String rawName = file.attr("href");
        if (rawName.contains("../")) {
          continue;
        }
        if (!(rawName.contains("latest.json") | rawName.startsWith("1."))) {
          continue;
        }
        array.add(rawName.replace(".json", ""));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return array;
  }

  /**
   * Build the spigot or copy if exists
   *
   * @param version the version of spigot
   */
  private static void buildSpigot(final String version) {
    File builder = new File("local/builder/spigot");
    File buildFolder = new File(builder, version);
    buildFolder.mkdirs();
    File buildTools = new File(buildFolder, "buildtools.jar");
    if (!buildTools.exists()) {
      runBuildTools(version, buildFolder, buildTools);
    } else {
      if(Objects.requireNonNull(
          buildFolder.listFiles(pathname -> pathname.getName().startsWith("spigot-"))).length > 0){
      System.out.println("Skipping build");
      System.out.println("Copy spigot.jar");
      try {
          Files.copy(new FileInputStream(Objects.requireNonNull(
              buildFolder.listFiles(pathname -> pathname.getName().startsWith("spigot-")))[0]),
              Paths.get("local/spigot.jar"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }else{
        runBuildTools(version, buildFolder, buildTools);
      }
    }

  }

  /**
   * Run the build tools of the jar not exists
   * @param version the version of spigot
   * @param buildFolder the folder in there are build
   * @param buildTools the path of the build tools
   */
  private static void runBuildTools(String version, File buildFolder, File buildTools) {
    try {
      System.out.println("Downloading BuildTools.jar...");
      URLConnection connection = new URL(buildToolsUrl).openConnection();
      connection.setRequestProperty("User-Agent",
          "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
      connection.connect();
      try (InputStream inputStream = connection.getInputStream()) {
        Files.copy(inputStream, Paths.get(buildTools.toURI()),
            StandardCopyOption.REPLACE_EXISTING);
      }
      System.out.println("Download was successfully completed!");
      System.out.println("Building Spigot " + version);
      exec = Runtime.getRuntime()
          .exec(String.format("java -jar buildtools.jar --rev %s", version), null, buildFolder);
      PaperBuilder.printProcessOutputToConsole(exec);
      if(Objects.requireNonNull(
          buildFolder.listFiles(pathname -> pathname.getName().startsWith("spigot-"))).length > 0){
        Files.copy(new FileInputStream(Objects.requireNonNull(
            buildFolder.listFiles(pathname -> pathname.getName().startsWith("spigot-")))[0]),
            Paths.get("local/spigot.jar"), StandardCopyOption.REPLACE_EXISTING);
      }else{
        FileUtility.deleteDirectory(buildFolder);
        runBuildTools(version,buildFolder,buildTools);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
