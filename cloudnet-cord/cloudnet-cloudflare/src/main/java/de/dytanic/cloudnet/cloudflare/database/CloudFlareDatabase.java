package de.dytanic.cloudnet.cloudflare.database;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.cloudflare.CloudFlareConfig;
import de.dytanic.cloudnet.cloudflare.PostResponse;
import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Database to store information about the CloudFlare requests and cache.
 */
public class CloudFlareDatabase extends DatabaseUsable {

    private static final Type MAP_STRING_POST_RESPONSE_TYPE = TypeToken.getParameterized(MultiValue.class, String.class, PostResponse.class)
                                                                       .getType();
    private static final String CLOUDFLARE_CACHE = "cloudflare_cache";
    private static final String CLOUDFLARE_CACHE_REQ = "cloudflare_cache_dnsreq";

    public CloudFlareDatabase(Database database) {
        super(database);
        if (database.getDocument(CLOUDFLARE_CACHE) == null) {
            database.insert(new DatabaseDocument(CLOUDFLARE_CACHE));
        }

        if (database.getDocument(CLOUDFLARE_CACHE_REQ) == null) {
            database.insert(new DatabaseDocument(CLOUDFLARE_CACHE_REQ));
        }
    }

    public Collection<String> getAll() {
        Collection<String> collection = database.getDocument(CLOUDFLARE_CACHE).keySet();
        collection.remove(Database.UNIQUE_NAME_KEY);
        return collection;
    }

    public void putPostResponse(MultiValue<PostResponse, String> postResponse) {
        DatabaseDocument document = database.getDocument(CLOUDFLARE_CACHE);
        document.append(postResponse.getFirst().getId(), postResponse);
        database.insert(document);
    }

    public boolean contains(CloudFlareConfig cloudFlareConfig, String wrapper) {
        DatabaseDocument document = database.getDocument(CLOUDFLARE_CACHE);

        /*
        Map<String, MultiValue<PostResponse, String>> responses = document.getObject("requests", new TypeToken<Map<String, MultiValue<PostResponse, String>>>() {
        }.getType());
        */

        /*
        return CollectionWrapper.filter(responses.values(), new Acceptable<MultiValue<PostResponse, String>>() {
            @Override
            public boolean isAccepted(MultiValue<PostResponse, String> value)
            {
                return value.getSecond().equalsIgnoreCase(wrapper) && value.getFirst().getCloudFlareConfig().getDomainName().equalsIgnoreCase(cloudFlareConfig.getDomainName());
            }
        }) != null;
        */

        for (String key : document.keySet()) {
            if (!key.equals(Database.UNIQUE_NAME_KEY)) {
                MultiValue<PostResponse, String> value = document.getObject(key, MAP_STRING_POST_RESPONSE_TYPE);

                if (value != null && value.getSecond().equalsIgnoreCase(wrapper) &&
                    value.getFirst().getCloudFlareConfig().getDomainName().equalsIgnoreCase(cloudFlareConfig.getDomainName())) {
                    return true;
                }
            }
        }

        return false;
        //return document.contains(wrapper);
    }

    public void remove(String wrapper) {
        database.getDocument(CLOUDFLARE_CACHE).remove(wrapper);
    }

    public PostResponse getResponse(String wrapper) {
        return database.getDocument(CLOUDFLARE_CACHE).getObject(wrapper, PostResponse.class);
    }

    public void add(PostResponse postResponse) {
        if (postResponse == null) {
            return;
        }

        DatabaseDocument document = database.getDocument(CLOUDFLARE_CACHE_REQ);
        if (document.contains("requests")) {
            Map<String, PostResponse> responses = document.getObject("requests", MAP_STRING_POST_RESPONSE_TYPE);
            responses.put(postResponse.getId(), postResponse);
            document.append("requests", responses);
        } else {
            Map<String, PostResponse> responses = new HashMap<>();
            responses.put(postResponse.getId(), postResponse);
            document.append("requests", responses);
        }

        database.insert(document);
    }

    public void remove(PostResponse postResponse) {
        DatabaseDocument document = database.getDocument(CLOUDFLARE_CACHE_REQ);
        if (document.contains("requests")) {
            Map<String, PostResponse> responses = document.getObject("requests", MAP_STRING_POST_RESPONSE_TYPE);
            responses.remove(postResponse.getId());
            document.append("requests", responses);
        } else {
            document.append("requests", new HashMap<>(0));
        }

        database.insert(document);
    }

    public Map<String, MultiValue<PostResponse, String>> getAndRemove() {
        DatabaseDocument document = database.getDocument(CLOUDFLARE_CACHE_REQ);
        if (document.contains("requests")) {
            Map<String, MultiValue<PostResponse, String>> responses = document.getObject("requests", MAP_STRING_POST_RESPONSE_TYPE);
            document.append("requests", Collections.emptyMap());
            database.insert(document);
            return responses;
        }
        return Collections.emptyMap();
    }
}
