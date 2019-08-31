/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup.spigot;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

/**
 * Created by Tareko on 25.05.2017.
 */
public class SetupSpigotVersion implements Consumer<ConsoleReader> {

	private Path target;

	private final Consumer<String> download = new Consumer<String>() {
		@Override
		public void accept(String url) {
			try {
				System.out.println("Downloading spigot.jar...");
				URLConnection connection = new URL(url).openConnection();
				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
				connection.connect();
				try (InputStream inputStream = connection.getInputStream()) {
					Files.copy(inputStream, target != null ? target : Paths.get("local/spigot.jar"), StandardCopyOption.REPLACE_EXISTING);
				}
				System.out.println("Download was successfully completed!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

    /**
     * A method to choose on start the spigot version
     * @param reader The console reader
     */
	@Override
	public void accept(ConsoleReader reader) {
		System.out.println("No spigot.jar has been found!");

		System.out.println("Choose a minecraft server version [\"paper\",\"spigot\", \"buildtools\"]");

		String answer = null;

		if (System.getProperty("spigot-type") != null) {
			answer = System.getProperty("spigot-type");
		}

		String input;

		while (answer == null) {
			try {
				input = reader.readLine();
				switch (input.toLowerCase()) {
					case "spigot":
						answer = "spigot";
						break;
					case "buildtools":
						answer = "buildtools";
						break;

					case "paper":
						answer = "paper";
						break;
					default:
						System.out.println("This version is not supported!");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (System.getProperty("spigot-version") != null) {
			answer = System.getProperty("spigot-version");
		}
		switch (answer) {
			case "spigot":
				System.out.println("Choose a Spigot version [\"1.7.10\", \"1.8.8\", \"1.9.4\", \"1.10.2\", \"1.11.2\", \"1.12.2\", \"1.13\", \"1.13.1\", \"1.13.2\", \"1.14\", \"1.14.1\", \"1.14.2\", \"1.14.3\", \"1.14.4\"]");
				while (true) {
					try {
						switch (reader.readLine().toLowerCase()) {
							case "1.7.10":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.7.10-SNAPSHOT-b1657.jar");
								return;
							case "1.8.8":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar");
								return;
							case "1.9.4":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar");
								return;
							case "1.10.2":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.10.2-R0.1-SNAPSHOT-latest.jar");
								return;
							case "1.11.2":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.11.2.jar");
								return;
							case "1.12.2":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar");
								return;
							case "1.13":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.13.jar");
								return;
							case "1.13.1":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.13.1.jar");
								return;
							case "1.13.2":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.13.2.jar");
								return;
							case "1.14":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.14.jar");
								return;
							case "1.14.1":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.14.1.jar");
								return;
							case "1.14.2":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.14.2.jar");
								return;
							case "1.14.3":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.14.3.jar");
								return;
							case "1.14.4":
								download.accept("https://cdn.getbukkit.org/spigot/spigot-1.14.4.jar");
								return;
							default:
								System.out.println("This version is not supported!");
								break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			case "buildtools":
				SpigotBuilder.start(reader);
				break;
			case "paper":
				PaperBuilder.start(reader);
				break;
		}
	}

    public void setTarget(Path target) {
        this.target = target;
    }
}
