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

package eu.cloudnetservice.cloudnet.v2.wrapper.handlers;

import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.screen.AbstractScreenService;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.BungeeCord;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.GameServer;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ReadConsoleLogHandler implements IWrapperHandler {

    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void accept(CloudNetWrapper wrapper) {
        for (BungeeCord bungeeCord : wrapper.getProxies().values()) {
            if (bungeeCord.isAlive()) {
                readConsoleLog(bungeeCord);
            }
        }

        for (GameServer gameServer : wrapper.getServers().values()) {
            if (gameServer.isAlive()) {
                readConsoleLog(gameServer);
            }
        }
    }

    private synchronized void readConsoleLog(AbstractScreenService server) {
        if (server.getInstance().isAlive() && server.getInstance().getInputStream() != null) {
            readStream(server, server.getInstance().getInputStream());
            readStream(server, server.getInstance().getErrorStream());
        }
    }

    private synchronized void readStream(AbstractScreenService screenService, InputStream inputStream) {
        try {
            int len;
            final byte[] buffer = new byte[1024];
            while (inputStream.available() > 0 && (len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                stringBuilder.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
            }

            if (stringBuilder.lastIndexOf("\n") == -1 ||
                stringBuilder.lastIndexOf("\r") == -1) {
                return;
            }

            String stringText = stringBuilder.toString();
            for (String text : stringText.split("\r?\n")) {
                if (!text.trim().isEmpty()) {
                    screenService.addCachedItem(text);
                }
            }

            stringBuilder.setLength(0);

        } catch (Exception ignored) {
            stringBuilder.setLength(0);
        }
    }

}
