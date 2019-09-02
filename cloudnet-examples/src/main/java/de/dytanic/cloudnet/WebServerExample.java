/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet;

import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

import java.nio.charset.StandardCharsets;

/**
 * Created by Tareko on 15.10.2017.
 */
public class WebServerExample
    extends MethodWebHandlerAdapter { //Extend a abstract WebHandler. The MethodWebHandler or WebHandle can also use

    protected WebServerExample(CloudNet cloudNet) {
        super("/myRestAPI/{myCustomPattern}");
        cloudNet.getWebServer().getWebServerProvider().registerHandler(this); //Registered the webhandler
    }

    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider pathProvider,
                                HttpRequest httpRequest) throws Exception {
        return newResponse(httpRequest.getProtocolVersion(),
                           Unpooled.wrappedBuffer(pathProvider.getPathParameters()
                                                              .getString("myCustomPattern")
                                                              .getBytes(StandardCharsets.UTF_8)));
    }
}
