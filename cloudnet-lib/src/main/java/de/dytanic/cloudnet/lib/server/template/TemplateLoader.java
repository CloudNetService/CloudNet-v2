/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.template;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Getter
@AllArgsConstructor
public class TemplateLoader {

    private String url;

    private String dest;

    public TemplateLoader load()
    {
        try
        {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            urlConnection.setUseCaches(false);
            urlConnection.connect();
            Files.copy(urlConnection.getInputStream(), Paths.get(dest));
            ((HttpURLConnection)urlConnection).disconnect();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public TemplateLoader unZip(String dest)
    {
        try{

            ZipFile zipFile = new ZipFile(this.dest);
            ZipEntry z;
            Enumeration<? extends ZipEntry> entryEnumeration = zipFile.entries();
            while (entryEnumeration.hasMoreElements())
            {
                z = entryEnumeration.nextElement();
                extractEntry(zipFile, z, dest);
            }
            new File(this.dest).delete();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return this;
    }

    private void extractEntry(ZipFile zipFile, ZipEntry entry, String destDir)
            throws IOException
    {
        final byte[] bytes = new byte[0xFFFF];
        File file = new File(destDir, entry.getName());

        if (entry.isDirectory())
            file.mkdirs();
        else
        {
            new File(file.getParent()).mkdirs();

            InputStream is = null;
            OutputStream os = null;

            try {
                is = zipFile.getInputStream(entry);
                os = new FileOutputStream(file);

                int len;
                while ((len = is.read(bytes)) != -1)
                    os.write(bytes, 0, len);
            } finally
            {
                if (os != null) os.close();
                if (is != null) is.close();
            }
        }
    }

}