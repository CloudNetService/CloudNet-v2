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
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Tareko on 29.09.2017.
 */
public class WebsiteDownloadService extends MethodWebHandlerAdapter {

    public WebsiteDownloadService()
    {
        super("/cloudnet/api/v1/download");
    }

    @Override
    public FullHttpResponse get(ChannelHandlerContext channelHandlerContext, QueryDecoder queryDecoder, PathProvider path, HttpRequest httpRequest) throws Exception
    {
        CloudNet.getLogger().debug("HTTP Request from " + channelHandlerContext.channel().remoteAddress());

        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.UNAUTHORIZED);
        fullHttpResponse.headers().set("Content-Type", "application/json");

        Document dataDocument = new Document("success", false).append("reason", new ArrayList<>()).append("response", new Document());
        if (!httpRequest.headers().contains("-Xcloudnet-user") || (!httpRequest.headers().contains("-Xcloudnet-token") && !httpRequest.headers().contains("-Xcloudnet-password")) || !httpRequest.headers().contains("-Xmessage") || !httpRequest.headers().contains("-Xvalue"))
        {
            dataDocument.append("reason", Arrays.asList("-Xcloudnet-user, -Xcloudnet-token or -Xmessage not found!"));
            fullHttpResponse.content().writeBytes(dataDocument.convertToJsonString().getBytes(StandardCharsets.UTF_8));
            return fullHttpResponse;
        }

        if (httpRequest.headers().contains("-Xcloudnet-token") ? !CloudNet.getInstance().authorization(httpRequest.headers().get("-Xcloudnet-user"), httpRequest.headers().get("-Xcloudnet-token")) : !CloudNet.getInstance().authorizationPassword(httpRequest.headers().get("-Xcloudnet-user"), httpRequest.headers().get("-Xcloudnet-password")))
        {
            dataDocument.append("reason", Arrays.asList("failed authorization!"));
            fullHttpResponse.content().writeBytes(dataDocument.toBytesAsUTF_8());
            return fullHttpResponse;
        }

        fullHttpResponse.headers().set("Content-Type", "application/octet-stream");

        switch (httpRequest.headers().get("-Xmessage"))
        {
            case "plugin":
            {
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                Path path1 = Paths.get("local/plugins/" + httpRequest.headers().get("-Xvalue") + ".jar");
                if (Files.exists(path1))
                {
                    byte[] value = Files.readAllBytes(path1);
                    fullHttpResponse.headers().set("content-disposition", "attachment; filename = " + httpRequest.headers().get("-Xvalue") + ".jar");
                    fullHttpResponse.content().writeBytes(value);
                } else
                {
                    fullHttpResponse.headers().set("Content-Type", "application/json");
                    dataDocument.append("reason", Arrays.asList("cannot find file \"" + httpRequest.headers().get("-Xvalue") + "\""));
                    fullHttpResponse.content().writeBytes(dataDocument.toBytesAsUTF_8());
                }
            }
            break;
            case "template":
            {
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                Document document = Document.load(httpRequest.headers().get("-Xvalue"));
                if (document.contains("template") && document.contains("group") && Files.exists(Paths.get("local/templates/" + document.getString("group") + "/" + document.getString("template"))))
                {
                    Path file = Paths.get("local/cache/" + NetworkUtils.RANDOM.nextLong() + ".zip");
                    if (!Files.exists(file))
                    {
                        Files.createFile(file);
                    }
                    String x = "local/templates/" + document.getString("group") + "/" + document.getString("template");
                    zipFolder(Paths.get(x), file);
                    byte[] value = Files.readAllBytes(file);
                    fullHttpResponse.headers().set("content-disposition", "attachment; filename = " + document.getString("template") + ".zip");
                    fullHttpResponse.content().writeBytes(value);
                    Files.deleteIfExists(file);
                } else
                {
                    fullHttpResponse.headers().set("Content-Type", "application/json");
                    dataDocument.append("reason", Arrays.asList("cannot find file \"" + httpRequest.headers().get("-Xvalue") + "\""));
                    fullHttpResponse.content().writeBytes(dataDocument.toBytesAsUTF_8());
                }
            }
            break;
            case "custom":
            {
                fullHttpResponse.setStatus(HttpResponseStatus.OK);
                String server = httpRequest.headers().get("-Xvalue");
                String x = "local/servers/" + server;

                if(!Files.exists(Paths.get(x)))
                {
                    Files.createDirectories(Paths.get(x + "/plugins"));
                }

                Path file = Paths.get("local/cache/" + NetworkUtils.RANDOM.nextLong() + ".zip");
                if (!Files.exists(file))
                {
                    Files.createFile(file);
                }

                zipFolder(Paths.get(x), file);
                byte[] value = Files.readAllBytes(file);
                fullHttpResponse.headers().set("content-disposition", "attachment; filename = " + server + ".zip");
                fullHttpResponse.content().writeBytes(value);
                Files.deleteIfExists(file);
            }
            break;
            default:
                break;
        }

        return fullHttpResponse;
    }

    private void zipFolder(Path sourceFolderPath, Path zipPath) throws Exception
    {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
        Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();
    }

}