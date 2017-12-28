/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.utility.Catcher;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Tareko on 21.10.2017.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SetupRequest implements Nameable {

    private String name;

    private String question;

    private String inValidMessage;

    private SetupResponseType responseType;

    private Catcher<Boolean, String> validater;

}