/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol;

/**
 * Created by Tareko on 09.09.2017.
 */
public class ProtocolRequest {

    private int id;

    private Object element;

    public ProtocolRequest(int id, Object element) {
        this.id = id;
        this.element = element;
    }

    public int getId() {
        return id;
    }

    public Object getElement() {
        return element;
    }
}