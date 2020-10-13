/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.examples;

import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.web.server.handler.MethodWebHandlerAdapter;
import eu.cloudnetservice.cloudnet.v2.web.server.util.PathProvider;
import eu.cloudnetservice.cloudnet.v2.web.server.util.QueryDecoder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

import java.nio.charset.StandardCharsets;

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
