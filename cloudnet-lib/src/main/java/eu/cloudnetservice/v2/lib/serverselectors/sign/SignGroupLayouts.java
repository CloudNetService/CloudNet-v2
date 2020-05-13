package eu.cloudnetservice.v2.lib.serverselectors.sign;

import eu.cloudnetservice.v2.lib.interfaces.Nameable;

import java.util.Collection;

/**
 * Created by Tareko on 24.07.2017.
 */
public class SignGroupLayouts implements Nameable {

    private final String name;

    private final Collection<SignLayout> layouts;

    public SignGroupLayouts(String name, Collection<SignLayout> layouts) {
        this.name = name;
        this.layouts = layouts;
    }

    @Override
    public String getName() {
        return name;
    }

    public Collection<SignLayout> getLayouts() {
        return layouts;
    }
}