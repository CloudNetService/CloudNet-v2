package de.dytanic.cloudnet.lib.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Tareko on 20.01.2018.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Quad<F, S, T, FF> {

    private F first;

    private S second;

    private T third;

    private FF fourth;

}