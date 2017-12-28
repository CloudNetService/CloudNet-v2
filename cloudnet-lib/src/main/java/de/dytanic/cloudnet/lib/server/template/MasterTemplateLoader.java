/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.template;

import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Tareko on 24.09.2017.
 */
@Getter
@AllArgsConstructor
public class MasterTemplateLoader {

    private String url;

    private String dest;

    private SimpledUser simpledUser;

    private Template template;

    private String group;

    private String customName;

    public MasterTemplateLoader load()
    {
        try
        {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("-Xcloudnet-user", simpledUser.getUserName());
            urlConnection.setRequestProperty("-Xcloudnet-token", simpledUser.getApiToken());
            urlConnection.setRequestProperty("-Xmessage", customName != null ? "custom" : "template");
            urlConnection.setRequestProperty("-Xvalue",customName != null ? customName : new Document("template", template.getName()).append("group", group).convertToJsonString());
            urlConnection.setUseCaches(false);
            urlConnection.connect();

            if(urlConnection.getHeaderField("-Xresponse") == null)
            {
                Files.copy(urlConnection.getInputStream(), Paths.get(dest));
            }
            urlConnection.disconnect();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public MasterTemplateLoader unZip(String dest)
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
        final byte[] buffer = new byte[0xFFFF];
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
                while ((len = is.read(buffer)) != -1)
                    os.write(buffer, 0, len);
            } finally
            {
                if (os != null) os.close();
                if (is != null) is.close();
            }
        }
    }

}