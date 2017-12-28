/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.exception;

/**
 * Created by Tareko on 18.09.2017.
 */
public class JavaReqVersionException extends RuntimeException {

    public JavaReqVersionException()
    {
        super("You must have the java version 8! Please check out your Java Version with the command \"java -version\"");
    }
}