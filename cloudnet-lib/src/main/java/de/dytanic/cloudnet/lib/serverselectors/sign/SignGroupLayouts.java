package de.dytanic.cloudnet.lib.serverselectors.sign;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

/**
 * Created by Tareko on 24.07.2017.
 */
@Getter
@AllArgsConstructor
public class SignGroupLayouts implements Nameable {

    private String name;

    private Collection<SignLayout> layouts;

}