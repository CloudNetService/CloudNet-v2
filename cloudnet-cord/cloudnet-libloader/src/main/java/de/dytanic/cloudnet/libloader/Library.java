/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.libloader;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;

/**
 * Data class to define a library
 */
@Getter
@AllArgsConstructor
public class Library {

    /**
     * The path where this library is located at
     */
    private Path path;

    /**
     * The file name of this library
     */
    private String jarName;

}
