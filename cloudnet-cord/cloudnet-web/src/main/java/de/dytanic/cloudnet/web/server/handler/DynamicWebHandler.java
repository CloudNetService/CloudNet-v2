package de.dytanic.cloudnet.web.server.handler;

import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Created by Tareko on 18.11.2017.
 */
public abstract class DynamicWebHandler {

    public abstract FullHttpResponse handleRequest(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception;

}