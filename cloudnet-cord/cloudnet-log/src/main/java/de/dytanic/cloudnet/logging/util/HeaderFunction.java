/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.logging.util;

import de.dytanic.cloudnet.lib.NetworkUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Class that only displays a possible {@code HEADER.txt}, if present.
 */
@Deprecated
public class HeaderFunction {

    /**
     * Whether this class has been run
     */
    private boolean executed = false;

    /**
     * Constructs a new instance of the header function and automatically
     * display the header message either from a {@code HEADER.txt} or from
     * {@link NetworkUtils#header()}.
     */
    public HeaderFunction() {
        File file = new File("HEADER.txt");
        if (file.exists()) {
            executed = true;
            try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file),
                                                                             StandardCharsets.UTF_8); BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader)) {
                bufferedReader.lines().forEach(System.out::println);
                NetworkUtils.headerOut();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            NetworkUtils.header();
        }
    }

    public boolean isExecuted() {
        return executed;
    }
}
