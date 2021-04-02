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

package eu.cloudnetservice.cloudnet.v2.setup.spigot;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.setup.models.GetBukkitVersion;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SetupSpigotVersion implements Consumer<ConsoleReader> {

    private Path target;

    private final Predicate<String> download = url -> {
        try {
            System.out.println("Downloading spigot.jar...");
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
            connection.connect();
            try (InputStream inputStream = connection.getInputStream()) {
                Files.copy(inputStream, SetupSpigotVersion.this.getTarget(), StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Download was successfully completed!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    };

    /**
     * A method to choose on start the spigot version
     *
     * @param reader The console reader
     */
    @Override
    public void accept(ConsoleReader reader) {
        System.out.println("No spigot.jar has been found!");

        String spigotType;
        do {
            System.out.println("Choose a minecraft server version [\"paper\", \"spigot\", \"buildtools\"]");
            spigotType = System.getProperty("spigot-type") != null ? System.getProperty("spigot-type") : this.askForServerType(reader);
        } while (!this.install(reader, spigotType));
    }

    private boolean install(ConsoleReader reader, String spigotType) {
        switch (spigotType) {
            case "spigot":
                return this.installSpigot(reader);
            case "buildtools":
                return SpigotBuilder.start(reader, this.getTarget());
            case "paper":
                return PaperBuilder.start(reader, this.getTarget());
        }
        return false;
    }

    private boolean installSpigot(ConsoleReader reader) {
        GetBukkitVersion[] versions = GetBukkitVersions.getVersions("1.8.8");

        StringBuilder builder = new StringBuilder("Choose a Spigot version [");
        builder.append(versions[0].getVersion());
        for (int i = 1; i < versions.length; i++) {
            GetBukkitVersion version = versions[i];
            builder.append(", \"")
                   .append(version.getVersion())
                   .append("\"");
        }
        builder.append("]");
        System.out.println(builder.toString());

        HashMap<String, GetBukkitVersion> versionsMap = new HashMap<>();
        for (GetBukkitVersion version : versions)
            versionsMap.put(version.getVersion(), version);

        while (true) {
            try {
                String input = reader.readLine().toLowerCase();
                if (!versionsMap.containsKey(input)) {
                    System.out.println("This version is not supported!");
                    continue;
                }

                GetBukkitVersion version = versionsMap.get(input);
                URL url = version.getDownloadURL();
                return download.test(url.toString());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private String askForServerType(ConsoleReader reader) {
        String answer = null;

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

        return answer;
    }

    public Path getTarget() {
        return this.target != null ? this.target : Paths.get("local/spigot.jar");
    }

    public void setTarget(Path target) {
        this.target = target;
    }
}
