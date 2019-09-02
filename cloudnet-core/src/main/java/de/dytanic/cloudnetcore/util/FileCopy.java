
/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util;

import de.dytanic.cloudnet.lib.NetworkUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class FileCopy {

    public static final void copyFileToDirectory(File file, File to) throws IOException {
        if (!to.exists()) {
            to.mkdirs();
        }
        File n = new File(to.getAbsolutePath() + NetworkUtils.SLASH_STRING + file.getName());
        Files.copy(file.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static final void copyFilesInDirectory(File from, File to) throws IOException {
        if (!to.exists()) {
            to.mkdirs();
        }
        for (File file : from.listFiles()) {
            if (file.isDirectory()) {
                copyFilesInDirectory(file, new File(to.getAbsolutePath() + NetworkUtils.SLASH_STRING + file.getName()));
            } else {
                File n = new File(to.getAbsolutePath() + NetworkUtils.SLASH_STRING + file.getName());
                Files.copy(file.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public static final void insertData(String paramString1, String paramString2) {
        InputStream localInputStream = FileCopy.class.getClassLoader().getResourceAsStream(paramString1);
        try {
            Files.copy(localInputStream, Paths.get(paramString2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
