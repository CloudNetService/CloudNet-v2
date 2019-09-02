package de.dytanic.cloudnet.web.server.handler;

import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Dynamic web handler that handles requests with methods for each of the common
 * HTTP methods.
 */
public abstract class MethodDynamicWebHandler extends DynamicWebHandler {

    @Override
    public final FullHttpResponse handleRequest(ChannelHandlerContext channelHandlerContext,
                                                QueryDecoder queryDecoder,
                                                PathProvider path,
                                                HttpRequest httpRequest) throws Exception {
        if (httpRequest.method().equals(HttpMethod.CONNECT)) {
            return connect(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if (httpRequest.method().equals(HttpMethod.DELETE)) {
            return delete(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if (httpRequest.method().equals(HttpMethod.GET)) {
            return get(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if (httpRequest.method().equals(HttpMethod.PUT)) {
            return put(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if (httpRequest.method().equals(HttpMethod.HEAD)) {
            return head(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if (httpRequest.method().equals(HttpMethod.OPTIONS)) {
            return options(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if (httpRequest.method().equals(HttpMethod.PATCH)) {
            return patch(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if (httpRequest.method().equals(HttpMethod.TRACE)) {
            return trace(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        if (httpRequest.method().equals(HttpMethod.POST)) {
            return post(channelHandlerContext, queryDecoder, path, httpRequest);
        }
        return null;
    }

    /**
     * Handle a dynamic {@code CONNECT} request to the configured {@code path}.
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
    public abstract FullHttpResponse connect(ChannelHandlerContext channelHandlerContext,
                                             QueryDecoder queryDecoder,
                                             PathProvider pathProvider,
                                             HttpRequest httpRequest) throws Exception;

    /**
     * Handle a dynamic {@code DELETE} request to the configured {@code path}.
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
    public abstract FullHttpResponse delete(ChannelHandlerContext channelHandlerContext,
                                            QueryDecoder queryDecoder,
                                            PathProvider pathProvider,
                                            HttpRequest httpRequest) throws Exception;

    /**
     * Handle a dynamic {@code GET} request to the configured {@code path}.
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
    public abstract FullHttpResponse get(ChannelHandlerContext channelHandlerContext,
                                         QueryDecoder queryDecoder,
                                         PathProvider pathProvider,
                                         HttpRequest httpRequest) throws Exception;

    /**
     * Handle a dynamic {@code PUT} request to the configured {@code path}.
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
    public abstract FullHttpResponse put(ChannelHandlerContext channelHandlerContext,
                                         QueryDecoder queryDecoder,
                                         PathProvider pathProvider,
                                         HttpRequest httpRequest) throws Exception;

    /**
     * Handle a dynamic {@code HEAD} request to the configured {@code path}.
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
    public abstract FullHttpResponse head(ChannelHandlerContext channelHandlerContext,
                                          QueryDecoder queryDecoder,
                                          PathProvider pathProvider,
                                          HttpRequest httpRequest) throws Exception;

    /**
     * Handle a dynamic {@code OPTIONS} request to the configured {@code path}.
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
    public abstract FullHttpResponse options(ChannelHandlerContext channelHandlerContext,
                                             QueryDecoder queryDecoder,
                                             PathProvider pathProvider,
                                             HttpRequest httpRequest) throws Exception;

    /**
     * Handle a dynamic {@code PATCH} request to the configured {@code path}.
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
    public abstract FullHttpResponse patch(ChannelHandlerContext channelHandlerContext,
                                           QueryDecoder queryDecoder,
                                           PathProvider pathProvider,
                                           HttpRequest httpRequest) throws Exception;

    /**
     * Handle a dynamic {@code TRACE} request to the configured {@code path}.
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
    public abstract FullHttpResponse trace(ChannelHandlerContext channelHandlerContext,
                                           QueryDecoder queryDecoder,
                                           PathProvider pathProvider,
                                           HttpRequest httpRequest) throws Exception;

    /**
     * Handle a dynamic {@code POST} request to the configured {@code path}.
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
    public abstract FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
                                          QueryDecoder queryDecoder,
                                          PathProvider pathProvider,
                                          HttpRequest httpRequest) throws Exception;
}
