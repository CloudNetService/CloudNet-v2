package de.dytanic.cloudnet.web.server.handler;

import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Adapter for the dynamic method web handler.
 * Returns null by default.
 */
public class MethodDynamicWebHandlerAdapter extends MethodDynamicWebHandler {

    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse delete(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse put(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse trace(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse head(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse patch(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse connect(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider pathProvider, HttpRequest httpRequest) throws Exception {
        return null;
    }
}
