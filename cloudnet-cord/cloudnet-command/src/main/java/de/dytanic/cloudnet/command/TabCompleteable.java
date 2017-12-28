/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.command;

import java.util.List;

/**
 * Created by Tareko on 23.05.2017.
 */
public interface TabCompleteable {

    List<String> onTab(long argsLength, String lastWord);

}