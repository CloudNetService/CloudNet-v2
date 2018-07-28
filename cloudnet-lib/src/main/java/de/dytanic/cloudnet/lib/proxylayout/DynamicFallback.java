/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.proxylayout;

import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.Catcher;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

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
        return CollectionWrapper.filter(fallbacks, new Acceptable<ServerFallback>() {
            @Override
            public boolean isAccepted(ServerFallback serverFallback)
            {
                return serverFallback.getGroup().equals(defaultFallback);
            }
        });
    }

    public Collection<String> getNamedFallbackes()
    {
        return CollectionWrapper.transform(this.fallbacks, new Catcher<String, ServerFallback>() {
            @Override
            public String doCatch(ServerFallback key)
            {
                return key.getGroup();
            }
        });
    }

}