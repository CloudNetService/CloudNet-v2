package de.dytanic.cloudnet.lib;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by Tareko on 26.07.2017.
 */
@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MultiValue<F, S> {

    private F first;

    private S second;

}