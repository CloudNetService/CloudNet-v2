/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.setup;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 21.10.2017.
 */
@Getter
@AllArgsConstructor
public enum SetupResponseType {

    STRING,
    BOOL,
    NUMBER;

    /**
     * Returns a user-friendly representation of this type.
     *
     * @return the user-friendly representation of this type
     */
    @Override
    public String toString() {
        switch (this) {
            case BOOL:
                return "yes : no";
            case STRING:
                return "string";
            case NUMBER:
                return "number";
        }
        return "string";
    }
}
