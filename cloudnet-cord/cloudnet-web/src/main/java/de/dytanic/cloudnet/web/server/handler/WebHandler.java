/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.web.server.handler;

import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

/**
 * Abstract class that handles web requests for a given path.
 */
public abstract class WebHandler implements Cloneable {

    protected final String path;

    /**
     * Constructs a new web handler for a given path.
     *
     * @param path the path where this handler is available.
     */
    protected WebHandler(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    /**
     * Handle a request to the configured {@code path}.
     *
     * @param channelHandlerContext the Netty context this request originates from
     * @param queryDecoder          a decoder for possible query parameters
     * @param pathProvider          a path provider with the provided parameters
     * @param httpRequest           the HTTP request that was received and should be handled
     *
     * @return the full response to the HTTP client
     *
     * @throws Exception when any error occurred during the handling of the request
     */
    public abstract FullHttpResponse handleRequest(ChannelHandlerContext channelHandlerContext,
                                                   QueryDecoder queryDecoder,
                                                   PathProvider pathProvider,
                                                   HttpRequest httpRequest) throws Exception;

    /**
     * Creates and returns a new {@code HTTP 404 - Not found} response without
     * content
     *
     * @param httpVersion the HTTP version to use
     *
     * @return an HTTP 404 response
     */
    @Deprecated
    public FullHttpResponse newResponse(HttpVersion httpVersion) {
        return new DefaultFullHttpResponse(httpVersion, HttpResponseStatus.NOT_FOUND);
    }

    /**
     * Creates and returns a new {@code HTTP 404 - Not found} response with
     * content
     *
     * @param httpVersion the HTTP version to use
     * @param content     the content to put inside the code 404 response
     *
     * @return an HTTP 404 response with content
     */
    public FullHttpResponse newResponse(HttpVersion httpVersion, ByteBuf content) {
        return new DefaultFullHttpResponse(httpVersion, HttpResponseStatus.NOT_FOUND, content);
    }

    @Override
    public WebHandler clone() throws CloneNotSupportedException {
        return (WebHandler) super.clone();
    }
}
