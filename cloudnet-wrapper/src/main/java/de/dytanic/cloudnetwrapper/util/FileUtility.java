
/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.util;

import de.dytanic.cloudnet.lib.NetworkUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class FileUtility {

    private FileUtility()
    {
    }

    public static void copyFileToDirectory(File file, File to) throws IOException
    {
        if (to == null || file == null) return;

        if (!to.exists()) to.mkdirs();

        File n = new File(to.getAbsolutePath() + NetworkUtils.SLASH_STRING + file.getName());
        Files.copy(file.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void copyFilesInDirectory(File from, File to) throws IOException
    {
        if (to == null || from == null) return;

        if (!to.exists()) to.mkdirs();

        if (!from.isDirectory()) return;

        for (File file : from.listFiles())
        {
            if (file == null) continue;

            if (file.isDirectory())
            {
                copyFilesInDirectory(file, new File(to.getAbsolutePath() + NetworkUtils.SLASH_STRING + file.getName()));
            } else
            {
                File n = new File(to.getAbsolutePath() + NetworkUtils.SLASH_STRING + file.getName());
                Files.copy(file.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public static void insertData(String paramString1, String paramString2)
    {
        File file = new File(paramString2);
        file.delete();

        try (InputStream localInputStream = FileUtility.class.getClassLoader().getResourceAsStream(paramString1))
        {
            Files.copy(localInputStream, Paths.get(paramString2), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e)
        {
        }
    }

    public static void deleteDirectory(File file)
    {
        if (file == null) return;

        if (file.isDirectory())
        {
            File[] files = file.listFiles();

            if (files != null)
                for (File entry : files)
                    if (entry.isDirectory())
                        deleteDirectory(entry);
                    else
                        entry.delete();
        }

        file.delete();
    }

    public static void rewriteFileUtils(File file, String host) throws Exception
    {
        file.setReadable(true);
        FileInputStream in = new FileInputStream(file);
        List<String> liste = new CopyOnWriteArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String input;
        boolean value = false;
        while ((input = reader.readLine()) != null)
        {
            if (value)
            {
                liste.add("  host: " + host + "\n");
                value = false;
            } else
            {
                if (input.startsWith("  query_enabled"))
                {
                    liste.add(input + "\n");
                    value = true;
                } else
                {
                    liste.add(input + "\n");
                }
            }
        }
        file.delete();
        file.createNewFile();
        file.setReadable(true);
        FileOutputStream out = new FileOutputStream(file);
        PrintWriter w = new PrintWriter(out);
        for (String wert : liste)
        {
            w.write(wert);
            w.flush();
        }
        reader.close();
        w.close();
    }

}
