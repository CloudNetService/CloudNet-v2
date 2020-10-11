/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.master.web.api.v1;

import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.lib.user.User;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.database.StatisticManager;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.process.CoreProxyProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.master.process.CoreServerProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.web.server.handler.MethodWebHandlerAdapter;
import eu.cloudnetservice.cloudnet.v2.web.server.util.PathProvider;
import eu.cloudnetservice.cloudnet.v2.web.server.util.QueryDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class WebsiteUtils extends MethodWebHandlerAdapter {

    public WebsiteUtils() {
        super("/cloudnet/api/v1/util");
    }

    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider path,
                                HttpRequest httpRequest) throws Exception {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.UNAUTHORIZED);
        fullHttpResponse.headers().set("Content-Type", "application/json");

        Document dataDocument = new Document("success", false).append("reason", new ArrayList<>()).append("response", new Document());
        if (!httpRequest.headers().contains("-Xcloudnet-user") || (!httpRequest.headers()
                                                                               .contains("-Xcloudnet-token") && !httpRequest.headers()
                                                                                                                            .contains(
                                                                                                                                "-Xcloudnet-password")) || !httpRequest
            .headers()
            .contains("-Xmessage")) {
            dataDocument.append("reason", Arrays.asList("-Xcloudnet-user, -Xcloudnet-token or -Xmessage not found!"));
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }

        if (httpRequest.headers().contains("-Xcloudnet-token") ? !CloudNet.getInstance().authorization(httpRequest.headers()
                                                                                                                  .get("-Xcloudnet-user"),
                                                                                                       httpRequest.headers()
                                                                                                                  .get("-Xcloudnet-token")) : !CloudNet
            .getInstance()
            .authorizationPassword(httpRequest.headers().get("-Xcloudnet-user"), httpRequest.headers().get("-Xcloudnet-password"))) {
            dataDocument.append("reason", Arrays.asList("failed authorization!"));
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }

        User user = CloudNet.getInstance().getUser(httpRequest.headers().get("-Xcloudnet-user"));

        switch (httpRequest.headers().get("-Xmessage").toLowerCase()) {
            case "serverinfos": {

                if (!user.getPermissions().contains("cloudnet.web.serverinfos") && !user.getPermissions().contains("*")) {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }

                dataDocument.append("success", true);
                Document response = new Document();
                for (MinecraftServer minecraftServer : CloudNet.getInstance().getServers().values()) {
                    response.append(minecraftServer.getServiceId().getServerId(), minecraftServer.getServerInfo());
                }
                dataDocument.append("response", response);
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            }
            case "proxyinfos": {

                if (!user.getPermissions().contains("cloudnet.web.proxyinfos") && !user.getPermissions().contains("*")) {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }

                dataDocument.append("success", true);
                Document response = new Document();
                for (ProxyServer minecraftServer : CloudNet.getInstance().getProxys().values()) {
                    response.append(minecraftServer.getServiceId().getServerId(), minecraftServer.getProxyInfo());
                }
                dataDocument.append("response", response);
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            }
            case "onlineplayers":

                if (!user.getPermissions().contains("cloudnet.web.onlineplayers") && !user.getPermissions().contains("*")) {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }

                dataDocument.append("success", true);
                Document response = new Document();
                for (CloudPlayer cloudPlayer : CloudNet.getInstance().getNetworkManager().getOnlinePlayers().values()) {
                    response.append(cloudPlayer.getUniqueId().toString(), cloudPlayer);
                }
                dataDocument.append("response", response);
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            case "statistic":

                if (!user.getPermissions().contains("cloudnet.web.statistic") && !user.getPermissions().contains("*")) {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }

                dataDocument.append("success", true).append("response", StatisticManager.getInstance().getStatistics());
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            case "cloudnetwork":

                if (!user.getPermissions().contains("cloudnet.web.cloudnetwork") && !user.getPermissions().contains("*")) {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }

                dataDocument.append("success", true).append("response", CloudNet.getInstance().getNetworkManager().newCloudNetwork());
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            case "startserver":

                if (!user.getPermissions().contains("cloudnet.web.startserver") && !user.getPermissions().contains("*")) {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }

                dataDocument.append("success", true);
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));

                if (httpRequest.headers().contains("-Xvalue")) {
                    String group = httpRequest.headers().get("-Xvalue");
                    CloudNet.getExecutor().submit(
                        () -> CoreServerProcessBuilder.create(group).startServer());
                }

                return fullHttpResponse;
            case "startproxy":

                if (!user.getPermissions().contains("cloudnet.web.startproxy") && !user.getPermissions().contains("*")) {
                    dataDocument.append("reason", Arrays.asList("permission denied!"));
                    fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                    fullHttpResponse.setStatus(HttpResponseStatus.FORBIDDEN);
                    return fullHttpResponse;
                }

                dataDocument.append("success", true);
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));

                if (httpRequest.headers().contains("-Xvalue")) {
                    String group = httpRequest.headers().get("-Xvalue");
                    CloudNet.getExecutor().submit(
                        () -> CoreProxyProcessBuilder.create(group).startProxy());
                }

                return fullHttpResponse;
            default: {
                dataDocument.append("success", true).append("reason", Arrays.asList("No available -Xmessage command found!"));
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
                return fullHttpResponse;
            }
        }
    }
}
