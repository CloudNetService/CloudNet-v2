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

package eu.cloudnetservice.cloudnet.v2.database;

import eu.cloudnetservice.cloudnet.v2.database.nitrite.NitriteDatabase;
import eu.cloudnetservice.cloudnet.v2.lib.database.Database;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import org.dizitart.no2.Nitrite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


/**
 * Manager for {@link Database} instances.
 */
public class DatabaseManager {

    /**
     * Directory name where old and upgraded databases are to be moved to.
     */
    private static final String NITRITE_UPGRADED_DIR = ".upgraded_nitrite";

    /**
     * Base database store for all used databases with the nitrite format.
     * This database store is the default, starting with 2.2.0.
     */
    private final Nitrite nitrite;

    /**
     * Collection of all currently loaded databases.
     * This serves as a means of reducing the amount of managed databases to
     * one per database name.
     */
    private final Map<String, Database> databaseCollection = new ConcurrentHashMap<>();

    /**
     * Constructs a new database manager.
     */
    public DatabaseManager() {
        final Path dir = Paths.get("database");
        try {
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                Files.deleteIfExists(dir);
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.nitrite = Nitrite.builder()
                              .filePath(dir.resolve("cloudnet.db").toFile())
                              .openOrCreate();

        if (needsUpgrade(dir)) {
            System.out.println("Database upgrade necessary.");
            upgradeDatabases(dir);
        }
    }

    /**
     * Checks whether the given database directory is in need of a database upgrade.
     * If the given path is not a directory, returns false.
     *
     * @param path the database path to check.
     *
     * @return whether the given path is in need of a database upgrade.
     *
     * @see #upgradeDatabases(Path)
     */
    private static boolean needsUpgrade(Path path) {
        final Path upgradeDir = path.resolve(NITRITE_UPGRADED_DIR);
        return !Files.exists(upgradeDir) || !Files.isDirectory(upgradeDir);
    }

    /**
     * Upgrades the databases in the given directory path to a {@link NitriteDatabase}.
     *
     * @param path the path to upgrade the databases from.
     *
     * @see #needsUpgrade(Path)
     */
    private void upgradeDatabases(final Path path) {
        final Path upgradedDir = path.resolve(NITRITE_UPGRADED_DIR);
        try {
            Files.deleteIfExists(upgradedDir);
            Files.createDirectory(upgradedDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Stream<Path> directories = Files.list(path)) {
            directories.filter(Files::isDirectory)
                       .filter(p -> !p.endsWith(NITRITE_UPGRADED_DIR))
                       .forEach(dir -> {
                           String dbName = dir.getFileName().toString();
                           DatabaseImpl oldDb = new DatabaseImpl(dbName, new ConcurrentHashMap<>(), dir.toFile());

                           NitriteDatabase newDb = new NitriteDatabase(dbName, nitrite);

                           System.out.println(String.format("Upgrading %s...", dbName));

                           final String[] files = oldDb.getBackendDir().list();
                           if (files != null) {
                               System.out.println(String.format("Converting %d documents", files.length));
                               for (int i = 0, filesLength = files.length; i < filesLength; i++) {
                                   final String file = files[i];
                                   try {
                                       final DatabaseDocument document = oldDb.getDocument(file);
                                       newDb.insert(document);
                                       // Clear every time to prevent OOM
                                       oldDb.getDocuments().clear();
                                   } catch (Exception exception) {
                                       System.err.println(String.format("Error processing document %s", file));
                                       exception.printStackTrace();
                                   }

                                   if ((i + 1) % 1000 == 0 || i == filesLength - 1) {
                                       System.out.println(String.format("Progress: %d/%d (%.2f%%)",
                                                                        i + 1,
                                                                        filesLength,
                                                                        ((double) i + 1) / filesLength * 100.0));
                                   }
                               }
                               nitrite.commit();
                               System.out.println(String.format("Upgraded %s.", dbName));
                           }
                           try {
                               Files.move(dir, upgradedDir.resolve(dbName));
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the currently opened documents in the loaded databases.
     *
     * @return this manager for chaining
     *
     * @see Database#save()
     */
    public DatabaseManager save() {
        for (Database database : databaseCollection.values()) {
            database.save();
        }
        return this;
    }

    /**
     * Clears the currently opened documents of the loaded databases.
     *
     * @return this manager for chaining
     *
     * @see Database#clear()
     */
    public DatabaseManager clear() {
        for (Database database : databaseCollection.values()) {
            database.clear();
        }
        return this;
    }

    /**
     * Returns a database for the given {@code name}.
     * If the database does not exist, it will be created.
     *
     * @param name the name of the database
     *
     * @return the database for the given {@code name}
     */
    public Database getDatabase(String name) {
        if (databaseCollection.containsKey(name)) {
            return databaseCollection.get(name);
        }

        Database database = new NitriteDatabase(name, nitrite);
        this.databaseCollection.put(name, database);

        return database;
    }

}
