/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.web.log;

import de.dytanic.cloudnet.web.server.handler.WebHandler;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.web.api.v1.WebsiteDocumentation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by Tareko on 04.10.2017.
 */
public class WebsiteLog extends WebHandler {

    public WebsiteLog() {
        super("/cloudnet/log");
    }

    @Override
    public FullHttpResponse handleRequest(ChannelHandlerContext channelHandlerContext,
                                          QueryDecoder queryDecoder,
                                          PathProvider path,
                                          HttpRequest httpRequest) throws Exception {
        CloudNet.getLogger().debug("HTTP Request from " + channelHandlerContext.channel().remoteAddress());
        if (!queryDecoder.getQueryParams().containsKey("server")) {
            FullHttpResponse fullHttpResponse = newResponse(httpRequest.getProtocolVersion());
            fullHttpResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            return fullHttpResponse;
        }
        if (!CloudNet.getInstance().getServerLogManager().getScreenInfos().contains(queryDecoder.getQueryParams().get("server"))) {
            FullHttpResponse fullHttpResponse = newResponse(httpRequest.getProtocolVersion());
            fullHttpResponse.setStatus(HttpResponseStatus.NOT_FOUND);
            return fullHttpResponse;
        }

        FullHttpResponse fullHttpResponse = newResponse(httpRequest.getProtocolVersion());
        fullHttpResponse.setStatus(HttpResponseStatus.OK);
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
