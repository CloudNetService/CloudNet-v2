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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that loads libraries from a given directory to its own {@link URLClassLoader}.
 */
@Getter
public class LibLoader {

    /**
     * The directory to load the libraries from.
     */
    private final File directory;

    /**
     * The currently loaded libraries
     */
    private Collection<Library> libraries = new LinkedList<>();

    /**
     * The classloader that is able to load all the libraries of this library loader.
     */
    private URLClassLoader urlClassLoader;

    /**
     * Constructs a new library loader with an uninitialized state.
     *
     * @param directory the directory to search for libraries
     */
    public LibLoader(final File directory) {
        this.directory = directory;
    }

    /**
     * Loads the libraries from {@link #directory} into {@link #libraries} and
     * the {@link #urlClassLoader}.
     * <p>
     * Filters based on file name, so that the file name ends with {@code .jar}.
     */
    public void load() {
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) {
            return;
        }
        List<URL> urls = new LinkedList<>();
        for (File file: files) {
            libraries.add(new Library(Paths.get(file.getPath()), file.getName()));
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        urlClassLoader = new URLClassLoader(urls.toArray(new URL[0]), urlClassLoader);
    }
}
