/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.libloader;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;

/**
 * Created by Tareko on 13.09.2017.
 */
@Getter
@AllArgsConstructor
public class Libary {

    private Path path;

    private String jarName;

}