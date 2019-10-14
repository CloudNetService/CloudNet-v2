/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.utility.Catcher;

import java.util.Objects;

/**
 * Class that represents a request for setup data.
 */
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

    public SetupRequest(String name,
                        String question,
                        String inValidMessage,
                        SetupResponseType responseType,
                        Catcher<Boolean, String> validater) {
        this.name = name;
        this.question = question;
        this.inValidMessage = inValidMessage;
        this.responseType = responseType;
        this.validater = validater;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (question != null ? question.hashCode() : 0);
        result = 31 * result + (inValidMessage != null ? inValidMessage.hashCode() : 0);
        result = 31 * result + (responseType != null ? responseType.hashCode() : 0);
        result = 31 * result + (validater != null ? validater.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SetupRequest)) {
            return false;
        }
        final SetupRequest that = (SetupRequest) o;
        return Objects.equals(name, that.name) && Objects.equals(question, that.question) && Objects.equals(inValidMessage,
                                                                                                            that.inValidMessage) && responseType == that.responseType && Objects
            .equals(validater, that.validater);
    }

    @Override
    public String toString() {
        return "SetupRequest{" + "name='" + name + '\'' + ", question='" + question + '\'' + ", inValidMessage='" + inValidMessage + '\'' + ", responseType=" + responseType + ", validater=" + validater + '}';
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
