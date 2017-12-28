/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.handler;

import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Created by Tareko on 16.09.2017.
 */
public class MethodWebHandlerAdapter extends MethodWebHandler {

    protected MethodWebHandlerAdapter(String path)
    {
        super(path);
    }

    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception
    {
        return null;
    }

    @Override
    public FullHttpResponse delete(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception
    {
        return null;
    }

    @Override
    public FullHttpResponse put(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception
    {
        return null;
    }

    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception
    {
        return null;
    }

    @Override
    public FullHttpResponse trace(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception
    {
        return null;
    }

    @Override
    public FullHttpResponse head(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception
    {
        return null;
    }

    @Override
    public FullHttpResponse patch(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception
    {
        return null;
    }

    @Override
    public FullHttpResponse connect(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception
    {
        return null;
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception
    {
        return null;
    }
}