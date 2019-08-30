/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 05.10.2017.
 */
@Getter
@AllArgsConstructor
public class DynamicFallback {

    private String defaultFallback;

    private List<ServerFallback> fallbacks;

    public ServerFallback getDefault()
    {
        return CollectionWrapper.filter(fallbacks, serverFallback -> serverFallback.getGroup().equals(defaultFallback));
    }

    public Collection<String> getNamedFallbackes()
    {
        return CollectionWrapper.transform(this.fallbacks, key -> key.getGroup());
    }

}