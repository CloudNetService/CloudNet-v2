
/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.util;

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

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[8192];
        copy(inputStream, outputStream, buffer);
    }

    public static void copy(InputStream inputStream, OutputStream outputStream, byte[] buffer) throws IOException {
        int len;

        while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.flush();
    }

    public static void copyFileToDirectory(File from, File to) throws IOException {
        copy(from.toPath(), new File(to, from.getName()).toPath());
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

    public static void copyFilesInDirectory(File from, File to) throws IOException {
        final Path sourcePath = from.toPath();
        final Path targetPath = to.toPath();
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

    public static void deleteDirectory(File file) {
        if (Files.notExists(file.toPath())) {
            return;
        }
        try {
            Files.walkFileTree(file.toPath(), new DeletingFileVisitor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void rewriteFileUtils(File file, String host) throws Exception {

        Map<String, Object> configuration;
        try (Reader reader = new FileReader(file)) {
            configuration = YAML.load(reader);
        }

        if (configuration != null) {
            //noinspection unchecked
            List<Map<String, Object>> listeners = (List<Map<String, Object>>) configuration.get("listeners");
            listeners.get(0).put("host", host);
            try (Writer writer = new FileWriter(file)) {
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
