package eu.cloudnetservice.v2.examples;

import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.web.server.handler.MethodWebHandlerAdapter;
import eu.cloudnetservice.v2.web.server.util.PathProvider;
import eu.cloudnetservice.v2.web.server.util.QueryDecoder;
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
        return newResponse(httpRequest.protocolVersion(),
                           Unpooled.copiedBuffer(pathProvider.getPathParameters().getString("myCustomPattern"),
                                                 StandardCharsets.UTF_8));
    }
}
