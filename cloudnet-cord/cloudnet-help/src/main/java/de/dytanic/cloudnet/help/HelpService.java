/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.help;

import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.map.Maps;
import lombok.Getter;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Tareko on 18.09.2017.
 */
@Getter
public final class HelpService {

    private Maps.ArrayMap<String, ServiceDescription> descriptions = new Maps.ArrayMap<>();

    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder("Help service of the Cloud:\n");

        CollectionWrapper.iterator(descriptions.entrySet(), new Consumer<Map.Entry<String, ServiceDescription[]>>() {

            @Override
            public void accept(final Map.Entry<String, ServiceDescription[]> obj) {
                stringBuilder.append(obj.getKey()).append(":\n");
                for(ServiceDescription serviceDescription : obj.getValue())
                {
                    stringBuilder.append("Usage: ").append(serviceDescription.getUsage()).append("\n");
                    stringBuilder.append("Description: ").append(serviceDescription.getDescription()).append("\n\n");
                }
            }
        });
        return stringBuilder.substring(0);
    }

    public void descripe()
    {
        System.out.println(toString());
    }

}