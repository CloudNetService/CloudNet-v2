/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.handler;

import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
/**
 * Created by Tareko on 14.09.2017.
 */
public abstract class MethodWebHandler extends WebHandler {

    protected MethodWebHandler(String path)
    {
        super(path);
    }

    @Override
    public final FullHttpResponse handleRequest(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider path, HttpRequest httpRequest) throws Exception
    {
        if(httpRequest.getMethod().name().equals(HttpMethod.CONNECT.name()))
        {
            return connect(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if(httpRequest.getMethod().name().equals(HttpMethod.DELETE.name()))
        {
            return delete(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if(httpRequest.getMethod().name().equals(HttpMethod.GET.name()))
        {
            return get(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if(httpRequest.getMethod().name().equals(HttpMethod.PUT.name()))
        {
            return put(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if(httpRequest.getMethod().name().equals(HttpMethod.HEAD.name()))
        {
            return head(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if(httpRequest.getMethod().name().equals(HttpMethod.OPTIONS.name()))
        {
            return options(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if(httpRequest.getMethod().name().equals(HttpMethod.PATCH.name()))
        {
            return patch(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if(httpRequest.getMethod().name().equals(HttpMethod.TRACE.name()))
        {
            return trace(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if(httpRequest.getMethod().name().equals(HttpMethod.POST.name()))
        {
            return post(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        return null;
    }

    public abstract FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception;

    public abstract FullHttpResponse delete(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception;

    public abstract FullHttpResponse put(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception;

    public abstract FullHttpResponse post(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception;

    public abstract FullHttpResponse trace(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception;

    public abstract FullHttpResponse head(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception;

    public abstract FullHttpResponse patch(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception;

    public abstract FullHttpResponse connect(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception;

    public abstract FullHttpResponse options(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception;

}