/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.bootstrap;

import de.dytanic.cloudnet.lib.exception.JavaReqVersionException;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

/**
 * Created by Tareko on 18.09.2017.
 */
public class CloudNetLauncher {

    public static void main(String[] args) throws Exception
    {
        if(Float.parseFloat(System.getProperty("java.class.version")) < 52D)
        {
            throw new JavaReqVersionException();
        }

        //Dytanic Zum testen für UTF-8 mache ich den Kram zweimal
        try
        {
            Field field = Charset.class.getDeclaredField("defaultCharset");
            field.setAccessible(true);
            field.set(null, Charset.forName("UTF-8"));
        }catch (Exception ex) {

        }

        CloudBootstrap.main(args);

    }
}