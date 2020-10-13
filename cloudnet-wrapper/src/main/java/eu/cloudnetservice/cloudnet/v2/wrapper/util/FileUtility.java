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

package eu.cloudnetservice.cloudnet.v2.wrapper.util;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class FileUtility {

    private static final Yaml YAML;

    static {
        // Kept in sync with BungeeCord
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        YAML = new Yaml(dumperOptions);
    }

    private FileUtility() {
    }

    public static void copyFileToDirectory(File from, File to) throws IOException {
        copy(from.toPath(), to.toPath().resolve(from.getName()));
    }

    public static void copy(Path from, Path to) throws IOException {
        if (from == null || to == null || !Files.exists(from)) {
            return;
        }

        if (!Files.exists(to.getParent())) {
            Files.createDirectories(to.getParent());
        }

        Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void copyFileToDirectory(Path from, Path to) throws IOException {
        copy(from, to.resolve(from.getFileName()));
    }

    public static void copyFilesInDirectory(File from, File to) throws IOException {
        final Path sourcePath = from.toPath();
        final Path targetPath = to.toPath();
        copyFilesInDirectory(sourcePath, targetPath);
    }

    public static void copyFilesInDirectory(Path sourcePath, Path targetPath) throws IOException {
        if (!Files.isDirectory(sourcePath)) {
            return;
        }
        if (Files.notExists(targetPath)) {
            Files.createDirectories(targetPath);
        }

        try (Stream<Path> sourceFiles = Files.walk(sourcePath)) {
            sourceFiles.forEach(path -> {
                try {
                    final Path absoluteTargetPath = targetPath.resolve(
                        sourcePath.relativize(path));
                    if (Files.isDirectory(path) && Files.notExists(absoluteTargetPath)) {
                        Files.createDirectories(absoluteTargetPath);
                    } else if (Files.isRegularFile(path)) {
                        copy(path, absoluteTargetPath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void insertData(String fileName, String destination) {
        final Path destinationPath = Paths.get(destination);
        try (InputStream localInputStream = FileUtility.class.getClassLoader().getResourceAsStream(fileName)) {
            Files.deleteIfExists(destinationPath);
            if (localInputStream != null) {
                Files.copy(localInputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insertData(String file, Path destination) {
        try (InputStream localInputStream = FileUtility.class.getClassLoader().getResourceAsStream(file)) {
            Files.deleteIfExists(destination);
            if (localInputStream != null) {
                Files.copy(localInputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(File directory) {
        deleteDirectory(directory.toPath());
    }

    public static void deleteDirectory(Path directory) {
        if (Files.notExists(directory)) {
            return;
        }
        try {
            Files.walkFileTree(directory, new DeletingFileVisitor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void rewriteFileUtils(Path path, String host) throws Exception {

        Map<String, Object> configuration;
        try (Reader reader = Files.newBufferedReader(path)) {
            configuration = YAML.load(reader);
        }

        if (configuration != null) {
            List listeners = (List) configuration.get("listeners");
            final Map map = (Map) listeners.get(0);
            map.put("host", host);
            try (Writer writer = Files.newBufferedWriter(path)) {
                YAML.dump(configuration, writer);
            }
        }
    }

    private static class DeletingFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            super.visitFile(file, attrs);
            Files.deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            super.postVisitDirectory(dir, exc);
            Files.deleteIfExists(dir);
            return FileVisitResult.CONTINUE;
        }
    }

}
