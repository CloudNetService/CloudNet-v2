/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.function.Function;

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
	private Function<String, Boolean> validater;

	public SetupRequest(String name, String question, String inValidMessage, SetupResponseType responseType, Function<String, Boolean> validater) {
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

	public Function<String, Boolean> getValidater() {
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
