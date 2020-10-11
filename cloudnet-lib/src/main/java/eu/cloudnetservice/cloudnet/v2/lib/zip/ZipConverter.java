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

package eu.cloudnetservice.cloudnet.v2.lib.zip;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZipConverter 2.0
 */
public final class ZipConverter {

    private ZipConverter() {
    }

    public static Path convert(Path zipPath, Path... directories) throws IOException {
        if (directories == null) {
            return null;
        }

        if (!Files.exists(zipPath)) {
            Files.createFile(zipPath);
        }

        try (OutputStream outputStream = Files.newOutputStream(zipPath); ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream,
                                                                                                                               StandardCharsets.UTF_8)) {
            for (Path dir : directories) {
                if (Files.exists(dir)) {
                    convert0(zipOutputStream, dir);
                }
            }
        }
        return zipPath;
    }

    private static void convert0(ZipOutputStream zipOutputStream, Path directory) throws IOException {
        Files.walkFileTree(directory, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    zipOutputStream.putNextEntry(new ZipEntry(directory.relativize(file).toString()));
                    Files.copy(file, zipOutputStream);
                    zipOutputStream.closeEntry();
                } catch (Exception ex) {
                    zipOutputStream.closeEntry();
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static byte[] convert(Path... directories) {
        if (directories == null) {
            return new byte[0];
        }

        try (ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(byteBuffer, StandardCharsets.UTF_8)) {

            for (Path dir : directories) {
                if (Files.exists(dir)) {
                    convert0(zipOutputStream, dir);
                }
            }

            return byteBuffer.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Path extract(Path zipPath, Path targetDirectory) throws IOException {
        if (zipPath == null || targetDirectory == null || !Files.exists(zipPath)) {
            return targetDirectory;
        }

        try (InputStream inputStream = Files.newInputStream(zipPath)) {
            extractStreamToDirectory(inputStream, targetDirectory);
        }

        return targetDirectory;
    }

    public static void extractStreamToDirectory(InputStream inputStream, Path targetDirectory) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream, StandardCharsets.UTF_8)) {
            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                extractEntryToDirectory(zipInputStream, zipEntry, targetDirectory);
                zipInputStream.closeEntry();
            }
        }
    }

    private static void extractEntryToDirectory(ZipInputStream zipInputStream, ZipEntry zipEntry, Path targetDirectory) throws IOException {
        Path file = Paths.get(targetDirectory.toString(), zipEntry.getName());

        if (zipEntry.isDirectory()) {
            if (!Files.exists(file)) {
                Files.createDirectories(file);
            }
        } else {
            Path parent = file.getParent();
            if (!Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            Files.deleteIfExists(file);
            Files.createFile(file);
            Files.copy(zipInputStream, file);
        }
    }

    public static Path extract(byte[] zipData, Path targetDirectory) throws IOException {
        if (zipData == null || zipData.length == 0 || targetDirectory == null) {
            return targetDirectory;
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(zipData)) {
            extractStreamToDirectory(byteArrayInputStream, targetDirectory);
        }

        return targetDirectory;
    }

}
