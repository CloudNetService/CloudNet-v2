package de.dytanic.cloudnet.lib.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 25.05.2017.
 */
@AllArgsConstructor
@Getter
public class Return<F, S> {

    private F first;
    private S second;

}
