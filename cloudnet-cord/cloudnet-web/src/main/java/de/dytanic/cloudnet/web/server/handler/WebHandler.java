/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.handler;

import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.Getter;

/**
 * Created by Tareko on 14.09.2017.
 */
@Getter
public abstract class WebHandler implements Cloneable {

    protected String path;

    protected WebHandler(String path)
    {
        this.path = path;
    }

    public abstract FullHttpResponse handleRequest(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception;

    public FullHttpResponse newResponse(HttpVersion httpVersion)
    {
        return new DefaultFullHttpResponse(httpVersion, HttpResponseStatus.NOT_FOUND);
    }

    public FullHttpResponse newResponse(HttpVersion httpVersion, ByteBuf content)
    {
        return new DefaultFullHttpResponse(httpVersion, HttpResponseStatus.NOT_FOUND, content);
    }

    @Override
    public WebHandler clone()
    {
        try
        {
            return (WebHandler) super.clone();
        } catch (CloneNotSupportedException e)
        {
        }
        return null;
    }
}