package de.dytanic.cloudnet.lib;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Tareko on 23.06.2017.
 */
@Data
@AllArgsConstructor
public class Value<E> {

    private E value;

}