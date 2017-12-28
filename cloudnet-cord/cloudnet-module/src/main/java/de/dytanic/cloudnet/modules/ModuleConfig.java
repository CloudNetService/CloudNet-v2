/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

/**
 * Created by Tareko on 23.07.2017.
 */
@Getter
@AllArgsConstructor
public class ModuleConfig {

    private File file;

    private String name;

    private String version;

    private String author;

    private String main;

}