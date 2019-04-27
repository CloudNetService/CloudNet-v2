/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Class that represents a request for setup data.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SetupRequest implements Nameable {

    /**
     * Name of this setup request.
     */
    private String name;

    /**
     * Question that is displayed to the user.
     */
    private String question;

    /**
     * Message that is shown when the input was invalid.
     */
    private String inValidMessage;

    /**
     * The type of response that is required from the user.
     */
    private SetupResponseType responseType;

    /**
     * The validation function that determines whether the input is valid or not.
     */
    private Function<String, Boolean> validater;

}
