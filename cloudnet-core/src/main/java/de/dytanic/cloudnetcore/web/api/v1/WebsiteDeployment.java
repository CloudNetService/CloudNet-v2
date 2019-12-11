/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.web.api.v1;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.web.server.handler.MethodWebHandlerAdapter;
import de.dytanic.cloudnet.web.server.util.PathProvider;
import de.dytanic.cloudnet.web.server.util.QueryDecoder;
import de.dytanic.cloudnetcore.CloudNet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Tareko on 25.09.2017.
 */
public class WebsiteDeployment extends MethodWebHandlerAdapter {

    public WebsiteDeployment() {
        super("/cloudnet/api/v1/deployment");
    }

    @Override
    public FullHttpResponse post(ChannelHandlerContext channelHandlerContext,
                                 QueryDecoder queryDecoder,
                                 PathProvider path,
                                 HttpRequest httpRequest) throws Exception {
        CloudNet.getLogger().debug("HTTP Request from " + channelHandlerContext.channel().remoteAddress());

        if (!(httpRequest instanceof FullHttpRequest)) {
            return null;
        }

        FullHttpRequest fullHttpRequest = ((FullHttpRequest) httpRequest);
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.UNAUTHORIZED);

        Document dataDocument = new Document("success", false).append("reason", new ArrayList<>()).append("response", new Document());
        if (!httpRequest.headers().contains("-Xcloudnet-user") || (!httpRequest.headers()
                                                                               .contains("-Xcloudnet-token") && !httpRequest.headers()
                                                                                                                            .contains(
                                                                                                                                "-Xcloudnet-password")) || !httpRequest
            .headers()
            .contains("-Xmessage")) {
            dataDocument.append("reason", Arrays.asList("-Xcloudnet-user, -Xcloudnet-token or -Xmessage not found!"));
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }

        if (httpRequest.headers().contains("-Xcloudnet-token") ? !CloudNet.getInstance().authorization(httpRequest.headers()
                                                                                                                  .get("-Xcloudnet-user"),
                                                                                                       httpRequest.headers()
                                                                                                                  .get("-Xcloudnet-token")) : !CloudNet
            .getInstance()
            .authorizationPassword(httpRequest.headers().get("-Xcloudnet-user"), httpRequest.headers().get("-Xcloudnet-password"))) {
            dataDocument.append("reason", Arrays.asList("failed authorization!"));
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }

        switch (httpRequest.headers().get("-Xmessage").toLowerCase()) {
            case "plugin": {
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                String pluginName = httpRequest.headers().get("-Xvalue");
                File file = new File(new StringBuilder("local/plugins/").append(pluginName).append(".jar").substring(0));
                if (file.getParentFile().mkdirs()) {
                    ;
                }
                file.createNewFile();
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(fullHttpRequest.content().array());
                }
                System.out.println("Plugin deployed [\"" + pluginName + "\"]");
            }
            break;
            case "template": {
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                Document document = Document.load(httpRequest.headers().get("-Xvalue"));
                if (document.contains("template") && document.contains("group")) {
                    File file = new File("local/templates/" + document.getString("group") + NetworkUtils.SLASH_STRING + document.getString(
                        "template") + NetworkUtils.SLASH_STRING + document.getString("template") + ".zip");

                    file.getParentFile().mkdirs();
                    file.createNewFile();

                    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                        fileOutputStream.write(fullHttpRequest.content().readBytes(fullHttpRequest.content().readableBytes()).array());
                        fileOutputStream.flush();
                    }

                    ZipFile zipFile = new ZipFile(file);
                    StringBuilder stringBuilder = new StringBuilder("local/templates/").append(document.getString("group")).append(
                        NetworkUtils.SLASH_STRING).append(document.getString("template"));
                    for (ZipEntry zipEntry : Collections.list(zipFile.entries())) {
                        extractEntry(zipFile, zipEntry, stringBuilder.toString());
                    }
                    file.delete();
                    System.out.println("Template deployed [\"" + document.getString("template") + "\"] for the group [\"" + document.getString(
                        "group") + "\"]");
                }
            }
            break;
            case "custom": {
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                String payload = fullHttpRequest.headers().get("-Xvalue");
                File file = new File("local/servers/" + payload + "/payload.zip");

                file.getParentFile().mkdirs();
                file.createNewFile();

                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.write(fullHttpRequest.content().readBytes(fullHttpRequest.content().readableBytes()).array());
                    fileOutputStream.flush();
                }

                ZipFile zipFile = new ZipFile(file);
                for (ZipEntry zipEntry : Collections.list(zipFile.entries())) {
                    extractEntry(zipFile, zipEntry, "local/servers/" + payload);
                }
                file.delete();
                System.out.println("Custom server deployed \"" + payload + '"');
            }
            break;
        }
        return fullHttpResponse;
    }

    private void extractEntry(ZipFile zipFile, ZipEntry entry, String destDir) throws IOException {
        File file = new File(destDir, entry.getName());

        if (!file.toPath().normalize().startsWith(Paths.get(destDir))) {
            return;
        }

        final byte[] BUFFER = new byte[0xFFFF];

        if (entry.isDirectory()) {
            file.mkdirs();
        } else {
            new File(file.getParent()).mkdirs();

            InputStream is = null;
            OutputStream os = null;

            try {
                is = zipFile.getInputStream(entry);
                os = new FileOutputStream(file);

                int len;
                while ((len = is.read(BUFFER)) != -1) {
                    os.write(BUFFER, 0, len);
                }
            } finally {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            }
        }
    }

}
