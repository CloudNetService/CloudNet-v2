package de.dytanic.cloudnet.lib.serverselectors.sign;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 24.07.2017.
 */
@Getter
@AllArgsConstructor
public class SignGroupLayouts implements Nameable {

    private String name;

    private Collection<SignLayout> layouts;

}