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

package eu.cloudnetservice.cloudnet.v2.master.web.log;

import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.web.api.v1.WebsiteDocumentation;
import eu.cloudnetservice.cloudnet.v2.web.server.handler.WebHandler;
import eu.cloudnetservice.cloudnet.v2.web.server.util.PathProvider;
import eu.cloudnetservice.cloudnet.v2.web.server.util.QueryDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class WebsiteLog extends WebHandler {

    public WebsiteLog() {
        super("/cloudnet/log");
    }

    @Override
    public FullHttpResponse handleRequest(ChannelHandlerContext channelHandlerContext,
                                          QueryDecoder queryDecoder,
                                          PathProvider path,
                                          HttpRequest httpRequest) throws Exception {
        CloudNet.getLogger().finest("HTTP Request from " + channelHandlerContext.channel().remoteAddress());
        if (!queryDecoder.getQueryParams().containsKey("server")) {
            return new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.NOT_FOUND);
        }
        if (!CloudNet.getInstance().getServerLogManager().getScreenInfos().contains(queryDecoder.getQueryParams().get("server"))) {
            return new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.NOT_FOUND);
        }

        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
        fullHttpResponse.headers().set("Content-Type", "text/html; charset=utf-8");

        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = WebsiteDocumentation.class.getClassLoader()
                                                                 .getResourceAsStream("files/log.html"); BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String input;
            while ((input = bufferedReader.readLine()) != null) {
                stringBuilder.append(input).append(System.lineSeparator());
            }
        }

        String site = stringBuilder.substring(0);
        //
        site = site.replace("%server_id_name%",
                            CloudNet.getInstance()
                                    .getServerLogManager()
                                    .getScreenInfos()
                                    .getF(queryDecoder.getQueryParams().get("server"))
                                    .getFirst()).replace("%input%",
                                                         CloudNet.getInstance()
                                                                 .getServerLogManager()
                                                                 .dispatch(queryDecoder.getQueryParams().get("server")));

        fullHttpResponse.content().writeBytes(site.getBytes(StandardCharsets.UTF_8));
        return fullHttpResponse;
    }
}
