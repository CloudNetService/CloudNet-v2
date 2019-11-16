
/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class FileUtility {

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
        copy(from.toPath(), new File(to.getPath(), from.getName()).toPath());
    }

    public static void copy(Path from, Path to) throws IOException {
        if (from == null || to == null || !Files.exists(from)) {
            return;
        }

        if (!Files.exists(to)) {
            Files.createDirectories(to.getParent());
            Files.deleteIfExists(to);
            Files.createFile(to);
        }

        Files.copy(from, to);
    }

    public static void copyFilesInDirectory(File from, File to) throws IOException {
        if (to == null || from == null || !from.exists()) {
            return;
        }

        if (!to.exists()) {
            to.mkdirs();
        }

        if (!from.isDirectory()) {
            return;
        }

        File[] list = from.listFiles();
        byte[] buffer = new byte[16384];
        if (list != null) {
            for (File file : list) {
                if (file == null) {
                    continue;
                }

                if (file.isDirectory()) {
                    copyFilesInDirectory(file, new File(to.getAbsolutePath() + '/' + file.getName()));
                } else {
                    File n = new File(to.getAbsolutePath() + '/' + file.getName());
                    copy(file.toPath(), n.toPath());
                }
            }
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
        if (file == null) {
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();

            if (files != null) {
                for (File entry : files) {
                    if (entry.isDirectory()) {
                        deleteDirectory(entry);
                    } else {
                        entry.delete();
                    }
                }
            }
        }

        file.delete();
    }

    // TODO
    public static void rewriteFileUtils(File file, String host) throws Exception {
        file.setReadable(true);
        FileInputStream in = new FileInputStream(file);
        List<String> liste = new CopyOnWriteArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String input;
        boolean value = false;
        while ((input = reader.readLine()) != null) {
            if (value) {
                liste.add("  host: " + host + '\n');
                value = false;
            } else {
                if (input.startsWith("  query_enabled")) {
                    liste.add(input + '\n');
                    value = true;
                } else {
                    liste.add(input + '\n');
                }
            }
        }
        file.delete();
        file.createNewFile();
        file.setReadable(true);
        FileOutputStream out = new FileOutputStream(file);
        PrintWriter w = new PrintWriter(out);
        for (String wert : liste) {
            w.write(wert);
            w.flush();
        }
        reader.close();
        w.close();
    }

}
