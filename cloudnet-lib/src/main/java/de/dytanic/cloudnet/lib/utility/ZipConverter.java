package de.dytanic.cloudnet.lib.utility;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class ZipConverter {

    private ZipConverter()
    {
    }

    public static Path convert(Path zipPath, Path... directorys) throws IOException
    {
        if(!Files.exists(zipPath))
            Files.createFile(zipPath);

        try (OutputStream outputStream = Files.newOutputStream(zipPath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream, StandardCharsets.UTF_8))
        {
            for (Path dir : directorys)
                if (Files.exists(dir))
                    convert0(zipOutputStream, zipPath, dir);
        }
        return zipPath;
    }

    private static void convert0(ZipOutputStream zipOutputStream, Path zipPath, Path directory) throws IOException
    {
        Files.walkFileTree(
                directory,
                EnumSet.noneOf(FileVisitOption.class),
                Integer.MAX_VALUE,
                new SimpleFileVisitor<Path>() {
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                    {
                        try
                        {
                            zipOutputStream.putNextEntry(new ZipEntry(directory.relativize(file).toString()));
                            Files.copy(file, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (Exception ex)
                        {
                            zipOutputStream.closeEntry();
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    public static Path extract(Path zipPath, Path targetDirectory) throws IOException
    {
        if (!Files.exists(zipPath)) throw new FileNotFoundException("zip file doesn't exists");

        if (!Files.exists(targetDirectory))
            Files.createDirectory(targetDirectory);

        else if (!Files.isDirectory(targetDirectory)) throw new IOException("File is not a directory");

        try (ZipFile zipFile = new ZipFile(zipPath.toFile(), StandardCharsets.UTF_8))
        {
            extract0(zipFile, targetDirectory);
        }

        return targetDirectory;
    }

    private static void extract0(ZipFile zipFile, Path targetDirectory) throws IOException
    {
        Enumeration<? extends ZipEntry> entryEnumeration = zipFile.entries();
        while (entryEnumeration.hasMoreElements()) extract1(zipFile, entryEnumeration.nextElement(), targetDirectory);
    }

    private static void extract1(ZipFile zipFile, ZipEntry zipEntry, Path targetDirectory) throws IOException
    {
        byte[] buffer = new byte[0xFFFF];
        Path file = Paths.get(targetDirectory.toString(), zipEntry.getName());

        if (zipEntry.isDirectory())
        {
            if (!Files.exists(file))
                Files.createDirectories(file);
        } else
        {
            new File(file.toFile().getParent()).mkdirs();

            Files.createFile(file);
            try (InputStream zipInputStream = zipFile.getInputStream(zipEntry); OutputStream outputStream = Files.newOutputStream(file))
            {
                int len;
                while ((len = zipInputStream.read(buffer)) != -1) outputStream.write(buffer, 0, len);
            }
        }
    }

}