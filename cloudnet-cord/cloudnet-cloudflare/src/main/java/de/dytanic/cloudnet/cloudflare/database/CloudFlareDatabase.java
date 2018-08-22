/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare.database;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.cloudflare.PostResponse;
import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Database to store information about the CloudFlare requests and cache.
 */
public class CloudFlareDatabase extends DatabaseUsable {

    public CloudFlareDatabase(Database database)
    {
        super(database);
        if (database.getDocument("cloudflare_cache") == null)
        {
            database.insert(new DatabaseDocument("cloudflare_cache"));
        }

        if (database.getDocument("cloudflare_cache_dnsrequests") == null)
        {
            database.insert(new DatabaseDocument("cloudflare_cache_dnsrequests"));
        }
    }

    public Collection<String> getAll()
    {
        Collection<String> collection = database.getDocument("cloudflare_cache").keys();
        collection.remove(Database.UNIQUE_NAME_KEY);
        return collection;
    }

    public void putPostResponse(String wrapper, PostResponse postResponse)
    {
        Document document = database.getDocument("cloudflare_cache");
        document.append(wrapper, postResponse);
        database.insert(document);
    }

    public boolean contains(String wrapper)
    {
        Document document = database.getDocument("cloudflare_cache");
        return document.contains(wrapper);
    }

    public void remove(String wrapper)
    {
        database.getDocument("cloudflare_cache").remove(wrapper);
    }

    public PostResponse getResponse(String wrapper)
    {
        return database.getDocument("cloudflare_cache").getObject(wrapper, new TypeToken<PostResponse>() {
        }.getType());
    }

    public void add(PostResponse postResponse)
    {
        if (postResponse == null) return;

        Document document = database.getDocument("cloudflare_cache_dnsrequests");
        if (document.contains("requests"))
        {
            Collection<String> responses = document.getObject("requests", new TypeToken<Collection<String>>() {
            }.getType());
            responses.add(postResponse.getId());
            document.append("requests", responses);
        } else
        {
            document.append("requests", new String[]{postResponse.getId()});
        }

        database.insert(document);
    }

    public void remove(PostResponse postResponse)
    {
        Document document = database.getDocument("cloudflare_cache_dnsrequests");
        if (document.contains("requests"))
        {
            Collection<String> responses = document.getObject("requests", new TypeToken<Collection<String>>() {
            }.getType());
            responses.remove(postResponse.getId());
            document.append("requests", responses);
        } else
        {
            document.append("requests", new String[0]);
        }

        database.insert(document);
    }

    public Collection<String> getAndRemove()
    {
        Document document = database.getDocument("cloudflare_cache_dnsrequests");
        if (document.contains("requests"))
        {
            Collection<String> responses = document.getObject("requests", new TypeToken<Collection<String>>() {
            }.getType());
            document.append("requests", new String[0]);
            database.insert(document);
            return responses;
        }
        return new ArrayList<>();
    }

}
