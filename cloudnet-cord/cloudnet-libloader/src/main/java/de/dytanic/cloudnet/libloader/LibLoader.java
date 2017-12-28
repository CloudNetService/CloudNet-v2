/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.libloader;

import lombok.Getter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Tareko on 07.09.2017.
 */
@Getter
public class LibLoader {

    private File file;

    private Collection<Libary> libarys;

    private URLClassLoader urlClassLoader;

    public LibLoader(File file)
    {
        this.file = file;
    }

    public void load()
    {
        URL[] url = new URL[file.list().length];
        for(File file : this.file.listFiles())
        {
            if(file.getName().endsWith(".jar"))
            {
                libarys.add(new Libary(Paths.get(file.getPath()), file.getName()));
                try
                {
                    Arrays.fill(url, file.toURI().toURL());
                } catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        urlClassLoader = new URLClassLoader(url);
    }
}