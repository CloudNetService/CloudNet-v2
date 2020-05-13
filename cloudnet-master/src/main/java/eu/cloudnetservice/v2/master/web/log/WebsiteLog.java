package eu.cloudnetservice.v2.master.web.log;

import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.web.api.v1.WebsiteDocumentation;
import eu.cloudnetservice.v2.web.server.handler.WebHandler;
import eu.cloudnetservice.v2.web.server.util.PathProvider;
import eu.cloudnetservice.v2.web.server.util.QueryDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by Tareko on 04.10.2017.
 */
public class WebsiteLog extends WebHandler {

    public WebsiteLog() {
        super("/cloudnet/log");
    }

    @Override
    public FullHttpResponse handleRequest(ChannelHandlerContext channelHandlerContext,
                                          QueryDecoder queryDecoder,
                                          PathProvider path,
                                          HttpRequest httpRequest) throws Exception {
        CloudNet.getLogger().finest("HTTP Request from " + channelHandlerContext.channel().remoteAddress());
        if (!queryDecoder.getQueryParams().containsKey("server")) {
            return new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.NOT_FOUND);
        }
        if (!CloudNet.getInstance().getServerLogManager().getScreenInfos().contains(queryDecoder.getQueryParams().get("server"))) {
            return new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.NOT_FOUND);
        }

        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
        fullHttpResponse.headers().set("Content-Type", "text/html; charset=utf-8");

        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = WebsiteDocumentation.class.getClassLoader()
                                                                 .getResourceAsStream("files/log.html"); BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String input;
            while ((input = bufferedReader.readLine()) != null) {
                stringBuilder.append(input).append(System.lineSeparator());
            }
        }

        String site = stringBuilder.substring(0);
        //
        site = site.replace("%server_id_name%",
                            CloudNet.getInstance()
                                    .getServerLogManager()
                                    .getScreenInfos()
                                    .getF(queryDecoder.getQueryParams().get("server"))
                                    .getFirst()).replace("%input%",
                                                         CloudNet.getInstance()
                                                                 .getServerLogManager()
                                                                 .dispatch(queryDecoder.getQueryParams().get("server")));

        fullHttpResponse.content().writeBytes(site.getBytes(StandardCharsets.UTF_8));
        return fullHttpResponse;
    }
}
