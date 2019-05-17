package de.dytanic.cloudnetwrapper.util;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnetwrapper.models.SpigotVersion;
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
import java.text.SimpleDateFormat;
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
  private final static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

  public static void start(final ConsoleReader reader){
    System.out.println("Fetch Spigot Versions");
    List<SpigotVersion> versions = loadVersions();
    System.out.println("Available Spigot Versions");
    System.out.println("-----------------------------------------------------------------------------");
    System.out.format("%s", "Spigot Version");
    System.out.println();
    System.out.println("-----------------------------------------------------------------------------");
    versions.forEach(v->{
      System.out.format("%s", v.getName());
      System.out.println();
    });
    System.out.println("-----------------------------------------------------------------------------");
    String awnser = null;
    while (awnser == null) {
      String name = null;
      try {
        name = reader.readLine().toLowerCase();
      } catch (IOException e) {
        e.printStackTrace();
      }
      String finalAwnser = name;
      if (versions.stream().anyMatch(e->e.getName().equalsIgnoreCase(finalAwnser))) {
        awnser = name;
        buildSpigot(finalAwnser,reader);
      }else if(versions.stream().noneMatch(e->e.getName().equalsIgnoreCase(finalAwnser))) {
        System.out.println("Version donsent exsists!");
      }
    }
  }
  private static List<SpigotVersion> loadVersions(){
    List<SpigotVersion> array = new LinkedList<>();
    try {
      Document doc = Jsoup.connect(versionsUrl).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11").get();
      for (Element file : doc.select("a")) {
        //String[] rawDate = file.nextSibling().toString().trim().split(" ");
        String rawName = file.attr("href");
        if(rawName.contains("../")){
          continue;
        }
        if (!(rawName.contains("latest.json") | rawName.startsWith("1."))) continue;
        /*if(rawName.endsWith(".json") && !( rawName.replace(".json","").startsWith("1.") || rawName.replace(".json","").contains("latest"))){
          continue;
        }*/
        SpigotVersion version = new SpigotVersion(rawName.replace(".json",""),versionsUrl+rawName);
        array.add(version);
      }
    }catch (Exception e){
      e.printStackTrace();
    }
    return array;
  }
  private static void buildSpigot(final String version,ConsoleReader reader){
    File builder = new File("local/builder/spigot");
    builder.delete();
    File buildFolder = new File(builder,version);
    buildFolder.mkdirs();
    File buildTools = new File(buildFolder,"buildtools.jar");
    if(!buildTools.exists()){
      try
      {
        System.out.println("Downloading BuildTools.jar...");
        URLConnection connection = new URL(buildToolsUrl).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();
        try (InputStream inputStream = connection.getInputStream())
        {
          Files.copy(inputStream, Paths.get(buildTools.toURI()), StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("Download was successfully completed!");
        System.out.println("Building Spigot "+version);
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("java -jar ");
        commandBuilder.append("buildtools.jar ");
        commandBuilder.append("--rev "+version);
        Process exec = Runtime.getRuntime()
            .exec(commandBuilder.toString().split(NetworkUtils.SPACE_STRING), null, buildFolder);
        while (true) {
          if (!exec.isAlive()) {
            System.out.println("Build finish!");
            System.out.println("Copy spigot.jar");
            Files.copy(new FileInputStream(Objects.requireNonNull(
                buildFolder.listFiles(pathname -> pathname.getName().startsWith("spigot-")))[0]), Paths.get("local/spigot.jar"), StandardCopyOption.REPLACE_EXISTING);
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
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }else{
      System.out.println("Skipping build");
      System.out.println("Copy spigot.jar");
      try {
        Files.copy(new FileInputStream(Objects.requireNonNull(
            buildFolder.listFiles(pathname -> pathname.getName().startsWith("spigot-")))[0]), Paths.get("local/spigot.jar"), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }
}
