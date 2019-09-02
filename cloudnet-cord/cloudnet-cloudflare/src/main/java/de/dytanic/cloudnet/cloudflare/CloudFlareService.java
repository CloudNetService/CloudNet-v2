/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.cloudflare;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.dytanic.cloudnet.cloudflare.database.CloudFlareDatabase;
import de.dytanic.cloudnet.cloudflare.exception.CloudFlareDNSRecordException;
import de.dytanic.cloudnet.cloudflare.util.DNSRecord;
import de.dytanic.cloudnet.cloudflare.util.DNSType;
import de.dytanic.cloudnet.cloudflare.util.DefaultDNSRecord;
import de.dytanic.cloudnet.cloudflare.util.SRVRecord;
import de.dytanic.cloudnet.lib.MultiValue;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.service.SimpledWrapperInfo;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;


/**
 * CloudFlare Service supports the api.cloudflare.com API for dynamic DNS records for BungeeCord Server.
 */
public class CloudFlareService {

    private static final String PREFIX_URL = "https://api.cloudflare.com/client/v4/";
    private static CloudFlareService instance;
    private final String prefix = "[CLOUDFLARE] | ";
    // WrapperId DNSRecord
    private final Map<String, MultiValue<PostResponse, String>> ipARecords = NetworkUtils.newConcurrentHashMap();
    private final Map<String, MultiValue<PostResponse, String>> bungeeSRVRecords = NetworkUtils.newConcurrentHashMap();
    private Collection<CloudFlareConfig> cloudFlareConfigs;

    /**
     * Constructs a new CloudFlare Service module with a given configuration.
     *
     * @param cloudFlareConfigs the configurations for this instance
     */
    public CloudFlareService(Collection<CloudFlareConfig> cloudFlareConfigs) {
        instance = this;

        this.cloudFlareConfigs = cloudFlareConfigs;
    }

    public static CloudFlareService getInstance() {
        return instance;
    }

    public String getPrefix() {
        return prefix;
    }

    public Collection<CloudFlareConfig> getCloudFlareConfigs() {
        return cloudFlareConfigs;
    }

    public Map<String, MultiValue<PostResponse, String>> getBungeeSRVRecords() {
        return bungeeSRVRecords;
    }

    public Map<String, MultiValue<PostResponse, String>> getIpARecords() {
        return ipARecords;
    }

    @Deprecated
    public boolean bootstrap(Map<String, SimpledWrapperInfo> wrapperInfoMap,
                             Map<String, ProxyGroup> groups,
                             CloudFlareDatabase cloudFlareDatabase) {
        for (MultiValue<PostResponse, String> id : cloudFlareDatabase.getAndRemove().values()) {
            this.deleteRecord(id.getFirst());
        }

        for (CloudFlareConfig cloudFlareConfig : this.cloudFlareConfigs) {
            if (cloudFlareConfig.isEnabled()) {
                for (CloudFlareProxyGroup cloudFlareProxyGroup : cloudFlareConfig.getGroups()) {
                    ProxyGroup proxyGroup = groups.get(cloudFlareProxyGroup.getName());
                    for (String wrapper : proxyGroup.getWrapper()) {
                        if (!cloudFlareDatabase.contains(cloudFlareConfig, wrapper)) {
                            String host = wrapperInfoMap.get(wrapper).getHostName();
                            DNSRecord dnsRecord = new DefaultDNSRecord(DNSType.A,
                                                                       wrapper + '.' + cloudFlareConfig.getDomainName(),
                                                                       host,
                                                                       new Document().obj());
                            if (!ipARecords.containsKey(wrapper)) {
                                PostResponse postResponse = this.createRecord(cloudFlareConfig, dnsRecord);
                                ipARecords.put(postResponse.getId(), new MultiValue<>(postResponse, wrapper));
                                cloudFlareDatabase.putPostResponse(new MultiValue<>(postResponse, wrapper));
                                NetworkUtils.sleepUninterruptedly(400);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Deletes a DNSRecord with the id of the DNS record
     *
     * @param postResponse the cached information about the dns record
     */
    public void deleteRecord(PostResponse postResponse) {

        try {
            HttpURLConnection delete = (HttpURLConnection) new URL(PREFIX_URL + "zones/" + postResponse.getCloudFlareConfig()
                                                                                                       .getZoneId() + "/dns_records/" + postResponse
                .getId()).openConnection();

            delete.setRequestMethod("DELETE");
            delete.setRequestProperty("X-Auth-Email", postResponse.getCloudFlareConfig().getEmail());
            delete.setRequestProperty("X-Auth-Key", postResponse.getCloudFlareConfig().getToken());
            delete.setRequestProperty("Accept", "application/json");
            delete.setRequestProperty("Content-Type", "application/json");
            delete.connect();

            try (InputStream inputStream = delete.getResponseCode() < 400 ? delete.getInputStream() : delete.getErrorStream()) {
                JsonObject jsonObject = toJsonInput(inputStream);
                if (jsonObject.get("success").getAsBoolean()) {
                    System.out.println(prefix + "DNSRecord [" + postResponse.getId() + "] was removed");
                } else {
                    throw new CloudFlareDNSRecordException("Failed to delete DNSRecord \n " + jsonObject.toString());
                }
            }

            delete.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates a new DNS record in the configured zone.
     *
     * @param dnsRecord the record to create
     *
     * @return the response from CloudFlare or null on failure
     */
    public PostResponse createRecord(CloudFlareConfig cloudFlareConfig, DNSRecord dnsRecord) {
        try {
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

            try (DataOutputStream dataOutputStream = new DataOutputStream(httpPost.getOutputStream())) {
                dataOutputStream.writeBytes(values);
                dataOutputStream.flush();
            }

            try (InputStream inputStream = httpPost.getResponseCode() < 400 ? httpPost.getInputStream() : httpPost.getErrorStream()) {
                JsonObject jsonObject = toJsonInput(inputStream);
                if (jsonObject.get("success").getAsBoolean()) {
                    System.out.println(prefix + "DNSRecord [" + dnsRecord.getName() + '/' + dnsRecord.getType() + "] was created");
                } else {
                    throw new CloudFlareDNSRecordException("Failed to create DNSRecord \n " + jsonObject.toString());
                }

                httpPost.disconnect();
                return new PostResponse(cloudFlareConfig, dnsRecord, jsonObject.get("result").getAsJsonObject().get("id").getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private JsonObject toJsonInput(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        String input;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        try {
            while ((input = bufferedReader.readLine()) != null) {
                stringBuilder.append(input);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonParser().parse(stringBuilder.substring(0)).getAsJsonObject();
    }

    public boolean shutdown(CloudFlareDatabase cloudFlareDatabase) {
        for (MultiValue<PostResponse, String> postResponse : this.bungeeSRVRecords.values()) {
            try {
                this.deleteRecord(postResponse.getFirst());
                NetworkUtils.sleepUninterruptedly(500);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        for (MultiValue<PostResponse, String> id : cloudFlareDatabase.getAndRemove().values()) {
            if (id.getFirst() == null) {
                continue;
            }

            this.deleteRecord(id.getFirst());
            NetworkUtils.sleepUninterruptedly(500);
        }
        return true;
    }

    /**
     * Adds a new proxy to this service and creates the SRV record at CloudFlare for it.
     *
     * @param proxyServer        the proxy server to create the SRV record for
     * @param cloudFlareDatabase the database to store the response and configuration in.
     */
    public void addProxy(ProxyProcessMeta proxyServer, CloudFlareDatabase cloudFlareDatabase) {
        for (CloudFlareConfig cloudFlareConfig : this.cloudFlareConfigs) {
            if (cloudFlareConfig.isEnabled()) {
                CloudFlareProxyGroup cloudFlareProxyGroup = cloudFlareProxyGroup(cloudFlareConfig, proxyServer.getServiceId().getGroup());
                if (cloudFlareProxyGroup != null) {
                    SRVRecord srvRecord;
                    if (cloudFlareProxyGroup.getSub().startsWith("@")) {
                        srvRecord = new SRVRecord("_minecraft._tcp." + cloudFlareConfig.getDomainName(),
                                                  "SRV 1 1 " + proxyServer.getPort() + ' ' + proxyServer.getServiceId()
                                                                                                        .getWrapperId() + '.' + cloudFlareConfig
                                                      .getDomainName(),
                                                  "_minecraft",
                                                  "_tcp",
                                                  cloudFlareConfig.getDomainName(),
                                                  1,
                                                  1,
                                                  proxyServer.getPort(),
                                                  proxyServer.getServiceId().getWrapperId() + '.' + cloudFlareConfig.getDomainName());
                    } else {
                        srvRecord = new SRVRecord("_minecraft._tcp." + cloudFlareConfig.getDomainName(),
                                                  "SRV 1 1 " + proxyServer.getPort() + ' ' + proxyServer.getServiceId()
                                                                                                        .getWrapperId() + '.' + cloudFlareConfig
                                                      .getDomainName(),
                                                  "_minecraft",
                                                  "_tcp",
                                                  cloudFlareProxyGroup.getSub(),
                                                  1,
                                                  1,
                                                  proxyServer.getPort(),
                                                  proxyServer.getServiceId().getWrapperId() + '.' + cloudFlareConfig.getDomainName());
                    }
                    PostResponse postResponse = this.createRecord(cloudFlareConfig, srvRecord);
                    cloudFlareDatabase.add(postResponse);
                    this.bungeeSRVRecords.put(postResponse.getId(),
                                              new MultiValue<>(postResponse, proxyServer.getServiceId().getServerId()));
                    NetworkUtils.sleepUninterruptedly(500);
                }
            }
        }
    }

    /**
     * Returns the first {@link CloudFlareProxyGroup} for a given {@code group} name.
     *
     * @param group the CloudFlare Proxy Group to search for
     *
     * @return the CloudFlareProxyGroup with the given group or null
     */
    public CloudFlareProxyGroup cloudFlareProxyGroup(CloudFlareConfig cloudFlareConfig, String group) {
        return CollectionWrapper.filter(cloudFlareConfig.getGroups(), value -> value.getName().equals(group));
    }

    /**
     * Removes a proxy and its DNS records from CloudFlare.
     *
     * @param proxyServer        the proxy server to remove
     * @param cloudFlareDatabase the database to remove the proxy server from
     */
    public void removeProxy(ProxyProcessMeta proxyServer, CloudFlareDatabase cloudFlareDatabase) {
        //if (!bungeeSRVRecords.containsKey(proxyServer.getServiceId().getServerId())) return;

        Collection<MultiValue<PostResponse, String>> postResponses = CollectionWrapper.filterMany(bungeeSRVRecords.values(),
                                                                                                  new Acceptable<MultiValue<PostResponse, String>>() {
                                                                                                      @Override
                                                                                                      public boolean isAccepted(MultiValue<PostResponse, String> postResponseStringMultiValue) {
                                                                                                          return postResponseStringMultiValue
                                                                                                              .getSecond()
                                                                                                              .equalsIgnoreCase(proxyServer.getServiceId()
                                                                                                                                           .getServerId());
                                                                                                      }
                                                                                                  });

        //MultiValue<PostResponse, String> postResponse = bungeeSRVRecords.get(proxyServer.getServiceId().getServerId());
            /*
            if (postResponse != null)
            {
                cloudFlareDatabase.remove(postResponse.getFirst().getId());
                deleteRecord(postResponse.getFirst());
            } else break;
            */

        for (MultiValue<PostResponse, String> postResponse : postResponses) {
            if (postResponse != null) {
                bungeeSRVRecords.remove(postResponse.getSecond());
                cloudFlareDatabase.remove(postResponse.getFirst().getId());
                deleteRecord(postResponse.getFirst());

                NetworkUtils.sleepUninterruptedly(500);
            }
        }
    }
}
