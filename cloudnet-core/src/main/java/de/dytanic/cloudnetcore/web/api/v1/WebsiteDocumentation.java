/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.web.api.v1;

import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by Tareko on 24.09.2017.
 */
public class WebsiteDocumentation extends MethodWebHandlerAdapter {

    public WebsiteDocumentation() {
        super("/cloudnet/api/v1");
    }

    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider path,
                                HttpRequest httpRequest) throws Exception {
        CloudNet.getLogger().debug("HTTP Request from " + channelHandlerContext.channel().remoteAddress());

        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream = WebsiteDocumentation.class.getClassLoader()
                                                                 .getResourceAsStream("files/api-doc.txt"); BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String input;
            while ((input = bufferedReader.readLine()) != null) {
                stringBuilder.append(input).append(System.lineSeparator());
            }
        }

        String output = stringBuilder.substring(0);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(output.getBytes(StandardCharsets.UTF_8));
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.OK, byteBuf);
        fullHttpResponse.headers().set("Content-Type", "text/plain");
        return fullHttpResponse;
    }
}
