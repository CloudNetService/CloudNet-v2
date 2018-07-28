/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.test;

import de.dytanic.cloudnet.lib.utility.document.Document;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by Tareko on 27.09.2017.
 */
public class DocumentTest {

    @Test
    public void reserve()
    {
        Document document = new Document().append("wdfqwaef", UUID.randomUUID());

        System.out.println(document.getObject("wdfqwaef", UUID.class));

    }
}