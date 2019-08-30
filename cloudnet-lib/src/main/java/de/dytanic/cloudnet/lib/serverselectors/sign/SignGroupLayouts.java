package de.dytanic.cloudnet.lib.serverselectors.sign;

import de.dytanic.cloudnet.lib.interfaces.Nameable;

import java.util.Collection;

/**
 * Created by Tareko on 24.07.2017.
 */
public class SignGroupLayouts implements Nameable {

    private String name;

    private Collection<SignLayout> layouts;

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