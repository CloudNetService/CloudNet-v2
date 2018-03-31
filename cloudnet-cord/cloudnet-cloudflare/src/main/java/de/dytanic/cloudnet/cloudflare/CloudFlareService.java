/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dytanic.cloudnet.cloudflare.database.CloudFlareDatabase;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.service.SimpledWrapperInfo;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.cloudflare.exception.CloudFlareDNSRecordException;
import de.dytanic.cloudnet.cloudflare.util.DNSRecord;
import de.dytanic.cloudnet.cloudflare.util.DNSType;
import de.dytanic.cloudnet.cloudflare.util.DefaultDNSRecord;
import de.dytanic.cloudnet.cloudflare.util.SRVRecord;
import lombok.Getter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 * CloudFlare Service Supports the with the api.cloudflare.com API dynamic dns records for BungeeCord Server
 */
@Getter
public class CloudFlareService {

    public static final String PREFIX_URL = "https://api.cloudflare.com/client/v4/";

    private final String prefix = "[CLOUDFLARE] | ";

    @Getter
    private static CloudFlareService instance;

    private CloudFlareConfig cloudFlareConfig;

    // WrapperId DNSRecord
    private Map<String, PostResponse> ipARecords = NetworkUtils.newConcurrentHashMap();
    private Map<String, PostResponse> bungeeSRVRecords = NetworkUtils.newConcurrentHashMap();

    public CloudFlareService(CloudFlareConfig cloudFlareConfig)
    {
        instance = this;
        this.cloudFlareConfig = cloudFlareConfig;
    }

    @Deprecated
    public boolean bootstrap(Map<String, SimpledWrapperInfo> wrapperInfoMap, Map<String, ProxyGroup> groups, CloudFlareDatabase cloudFlareDatabase)
    {
        if (!cloudFlareConfig.isEnabled()) return false;

        for(String id : cloudFlareDatabase.getAndRemove()) this.deleteRecord(id);

        for (String key : cloudFlareDatabase.getAll())
        {
            if (!key.equalsIgnoreCase(Database.UNIQUE_NAME_KEY) && !wrapperInfoMap.containsKey(key))
            {
                deleteRecord(cloudFlareDatabase.getResponse(key).getId());
            }
        }

        for (CloudFlareProxyGroup cloudFlareProxyGroup : cloudFlareConfig.getGroups())
        {
            ProxyGroup proxyGroup = groups.get(cloudFlareProxyGroup.getName());
            for (String wrapper : proxyGroup.getWrapper())
            {
                if (!cloudFlareDatabase.contains(wrapper))
                {
                    String host = wrapperInfoMap.get(wrapper).getHostName();
                    DNSRecord dnsRecord = new DefaultDNSRecord(DNSType.A, wrapper + "." + cloudFlareConfig.getDomainName(), host, new Document().obj());
                    if (!ipARecords.containsKey(wrapper))
                    {
                        PostResponse postResponse = this.createRecord(dnsRecord);
                        ipARecords.put(wrapper, postResponse);
                        cloudFlareDatabase.putPostResponse(wrapper, postResponse);
                        NetworkUtils.sleepUninterruptedly(400);
                    }
                }
            }
        }
        return true;
    }

    public boolean shutdown(CloudFlareDatabase cloudFlareDatabase)
    {
        if (!cloudFlareConfig.isEnabled()) return false;

        for (PostResponse postResponse : this.bungeeSRVRecords.values())
        {
            this.deleteRecord(postResponse.getId());
            NetworkUtils.sleepUninterruptedly(400);
        }

        for(String id : cloudFlareDatabase.getAndRemove())
        {
            this.deleteRecord(id);
            NetworkUtils.sleepUninterruptedly(400);
        }
        return true;
    }

    /**
     * Returns a cloudFlareProxyGroup which is request a parameter
     *
     * @param group
     * @return
     */
    public CloudFlareProxyGroup cloudFlareProxyGroup(String group)
    {
        return CollectionWrapper.filter(this.cloudFlareConfig.getGroups(), new Acceptable<CloudFlareProxyGroup>() {
            @Override
            public boolean isAccepted(CloudFlareProxyGroup value)
            {
                return value.getName().equals(group);
            }
        });
    }

    public void addProxy(ProxyProcessMeta proxyServer, CloudFlareDatabase cloudFlareDatabase)
    {
        if (cloudFlareConfig.isEnabled())
        {
            CloudFlareProxyGroup cloudFlareProxyGroup = cloudFlareProxyGroup(proxyServer.getServiceId().getGroup());
            if (cloudFlareProxyGroup != null)
            {
                SRVRecord srvRecord = null;
                if (cloudFlareProxyGroup.getSub().startsWith("@"))
                {

                    /*==================================================================================================*/

                    srvRecord = new SRVRecord(
                            "_minecraft._tcp." + cloudFlareConfig.getDomainName(),
                            "SRV 1 1 " + proxyServer.getPort() + " " + proxyServer.getServiceId().getWrapperId() + "." +
                                    cloudFlareConfig.getDomainName(),
                            "_minecraft",
                            "_tcp",
                            cloudFlareConfig.getDomainName(),
                            1,
                            1,
                            proxyServer.getPort(),
                            proxyServer.getServiceId().getWrapperId() + "." + cloudFlareConfig.getDomainName()
                    );

                    /*==================================================================================================*/

                } else
                {
                    /*==================================================================================================*/

                    srvRecord = new SRVRecord(
                            "_minecraft._tcp." + cloudFlareConfig.getDomainName(),
                            "SRV 1 1 " + proxyServer.getPort() + " " + proxyServer.getServiceId().getWrapperId() + "." +
                                    cloudFlareConfig.getDomainName(),
                            "_minecraft",
                            "_tcp",
                            cloudFlareProxyGroup.getSub(),
                            1,
                            1,
                            proxyServer.getPort(),
                            proxyServer.getServiceId().getWrapperId() + "." + cloudFlareConfig.getDomainName()
                    );

                    /*==================================================================================================*/
                }
                PostResponse postResponse1 = this.createRecord(srvRecord);
                cloudFlareDatabase.add(postResponse1);
                this.bungeeSRVRecords.put(proxyServer.getServiceId().getServerId(), postResponse1);
            }
        }
    }

    public void removeProxy(ProxyProcessMeta proxyServer, CloudFlareDatabase cloudFlareDatabase)
    {
        if (!cloudFlareConfig.isEnabled()) return;
        PostResponse postResponse = bungeeSRVRecords.get(proxyServer.getServiceId().getServerId());
        if (postResponse != null)
        {
            cloudFlareDatabase.remove(postResponse.getId());
            deleteRecord(postResponse.getId());
        }
    }

    /**
     * Creates a dns record
     *
     * @param dnsRecord
     * @return
     */
    public PostResponse createRecord(DNSRecord dnsRecord)
    {
        try
        {
            HttpURLConnection httpPost = (HttpURLConnection) new URL(PREFIX_URL + "zones/" + cloudFlareConfig.getZoneId() + "/dns_records").openConnection();
            String values = NetworkUtils.GSON.toJson(dnsRecord);

            httpPost.setRequestMethod("POST");
            httpPost.setRequestProperty("X-Auth-Email", cloudFlareConfig.getEmail());
            httpPost.setRequestProperty("X-Auth-Key", cloudFlareConfig.getToken());
            httpPost.setRequestProperty("Content-Length", values.getBytes().length + NetworkUtils.EMPTY_STRING);
            httpPost.setRequestProperty("Accept", "application/json");
            httpPost.setRequestProperty("Content-Type", "application/json");
            httpPost.setDoOutput(true);
            httpPost.connect();
            try (DataOutputStream dataOutputStream = new DataOutputStream(httpPost.getOutputStream()))
            {
                dataOutputStream.writeBytes(values);
                dataOutputStream.flush();
            }
            try (InputStream inputStream = httpPost.getInputStream())
            {
                JsonObject jsonObject = toJsonInput(inputStream);
                if (jsonObject.get("success").getAsBoolean())
                {
                    System.out.println(prefix + "DNSRecord [" + dnsRecord.getName() + "/" + dnsRecord.getType() + "] was created");
                } else
                {
                    throw new CloudFlareDNSRecordException("Failed to create DNSRecord \n " + jsonObject.toString());
                }
                httpPost.disconnect();
                return new PostResponse(dnsRecord, jsonObject.get("result").getAsJsonObject().get("id").getAsString());
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deletes a DNSRecord with the Id of the dns
     *
     * @param recordId
     */
    public void deleteRecord(String recordId)
    {

        try
        {
            HttpURLConnection delete = (HttpURLConnection) new URL(PREFIX_URL + "zones/" + cloudFlareConfig.getZoneId() + "/dns_records/" + recordId).openConnection();
            delete.setRequestMethod("DELETE");
            delete.setRequestProperty("X-Auth-Email", cloudFlareConfig.getEmail());
            delete.setRequestProperty("X-Auth-Key", cloudFlareConfig.getToken());
            delete.setRequestProperty("Accept", "application/json");
            delete.setRequestProperty("Content-Type", "application/json");
            delete.connect();
            try (InputStream inputStream = delete.getInputStream())
            {
                JsonObject jsonObject = toJsonInput(inputStream);
                if (jsonObject.get("success").getAsBoolean())
                {
                    System.out.println(prefix + " DNSRecord [" + recordId + "] was removed");
                }
                inputStream.close();
            }
            delete.disconnect();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private JsonObject toJsonInput(InputStream inputStream)
    {
        StringBuilder stringBuilder = new StringBuilder();
        String input = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        try
        {
            while ((input = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(input);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return new JsonParser().parse(stringBuilder.substring(0)).getAsJsonObject();
    }
}