package eu.cloudnetservice.v2.web.server.handler;

import eu.cloudnetservice.v2.web.server.util.PathProvider;
import eu.cloudnetservice.v2.web.server.util.QueryDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Adapter for the method web handler.
 * Returns null by default.
 */
public class MethodWebHandlerAdapter extends MethodWebHandler {

    /**
     * Constructs a new method web handler adapter for a given path.
     *
     * @param path the path where this handler is available.
     */
    protected MethodWebHandlerAdapter(String path) {
        super(path);
    }

    @Override
    public FullHttpResponse connect(ChannelHandlerContext channelHandlerContext,
                                    QueryDecoder queryDecoder,
                                    PathProvider pathProvider,
                                    HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse delete(ChannelHandlerContext channelHandlerContext,
                                   QueryDecoder queryDecoder,
                                   PathProvider pathProvider,
                                   HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider pathProvider,
                                HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse put(ChannelHandlerContext channelHandlerContext,
                                QueryDecoder queryDecoder,
                                PathProvider pathProvider,
                                HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse head(ChannelHandlerContext channelHandlerContext,
                                 QueryDecoder queryDecoder,
                                 PathProvider pathProvider,
                                 HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
                                    QueryDecoder queryDecoder,
                                    PathProvider pathProvider,
                                    HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse patch(ChannelHandlerContext channelHandlerContext,
                                  QueryDecoder queryDecoder,
                                  PathProvider pathProvider,
                                  HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse trace(ChannelHandlerContext channelHandlerContext,
                                  QueryDecoder queryDecoder,
                                  PathProvider pathProvider,
                                  HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
                                 QueryDecoder queryDecoder,
                                 PathProvider pathProvider,
                                 HttpRequest httpRequest) throws Exception {
        return null;
    }
}
