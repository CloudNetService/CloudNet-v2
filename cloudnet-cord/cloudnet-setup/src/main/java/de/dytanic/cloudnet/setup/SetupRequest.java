/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.utility.Catcher;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Class that represents a request for setup data.
 */
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
    private Catcher<Boolean, String> validater;

    public SetupRequest(String name, String question, String inValidMessage, SetupResponseType responseType, Catcher<Boolean, String> validater) {
        this.name = name;
        this.question = question;
        this.inValidMessage = inValidMessage;
        this.responseType = responseType;
        this.validater = validater;
    }

    @Override
    public String getName() {
        return name;
    }

    public Catcher<Boolean, String> getValidater() {
        return validater;
    }

    public SetupResponseType getResponseType() {
        return responseType;
    }

    public String getInValidMessage() {
        return inValidMessage;
    }

    public String getQuestion() {
        return question;
    }
}
