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

package eu.cloudnetservice.cloudnet.v2.lib;

import com.google.gson.Gson;
import com.sun.management.OperatingSystemMXBean;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.codec.ProtocolInDecoder;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.codec.ProtocolLengthDeserializer;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.codec.ProtocolLengthSerializer;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.codec.ProtocolOutEncoder;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;

public final class NetworkUtils {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";
    public static final Gson GSON = new Gson();
    public static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");
    public static final String EMPTY_STRING = "";
    public static final String SPACE_STRING = " ";
    public static final String SLASH_STRING = "/";
    private static final char[] ALPHABET = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private NetworkUtils() {
    }

    public static ScheduledExecutorService getExecutor() {
        return executor;
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            return "127.0.0.1";
        }
    }

    public static double cpuUsage() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getSystemCpuLoad() * 100;
    }

    public static double internalCpuUsage() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100;
    }

    public static long systemMemory() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
    }

    public static OperatingSystemMXBean system() {
        return ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
    }

    public static Class<? extends SocketChannel> socketChannel() {
        if (Epoll.isAvailable()) {
            return EpollSocketChannel.class;
        } else if (KQueue.isAvailable()) {
            return KQueueSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }

    public static Class<? extends ServerSocketChannel> serverSocketChannel() {
        if (Epoll.isAvailable()) {
            return EpollServerSocketChannel.class;
        } else if (KQueue.isAvailable()) {
            return KQueueServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }

    public static EventLoopGroup eventLoopGroup() {
        return eventLoopGroup(Math.min(Runtime.getRuntime().availableProcessors(), 4));
    }

    public static EventLoopGroup eventLoopGroup(int threads) {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup(threads);
        } else if (KQueue.isAvailable()) {
            return new KQueueEventLoopGroup(threads);
        } else {
            return new NioEventLoopGroup(threads);
        }
    }

    public static Channel initChannel(Channel channel) {
        channel.pipeline().addLast(new ProtocolLengthDeserializer(),
                                   new ProtocolInDecoder(),
                                   new ProtocolLengthSerializer(),
                                   new ProtocolOutEncoder());
        return channel;
    }

    public static boolean checkIsNumber(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String randomString(int size) {
        StringBuilder stringBuilder = new StringBuilder();
        for (short i = 0; i < size; i++) {
            stringBuilder.append(ALPHABET[RANDOM.nextInt(ALPHABET.length)]);
        }
        return stringBuilder.substring(0);
    }

    public static void writeWrapperKey() {
        Path path = Paths.get("WRAPPER_KEY.cnd");
        if (Files.notExists(path)) {
            StringBuilder stringBuilder = new StringBuilder(4096);
            for (int i = 0; i < 4096; i++) {
                stringBuilder.append(ALPHABET[RANDOM.nextInt(ALPHABET.length)]);
            }

            try {
                Files.createFile(path);
                Files.write(path, stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static String readWrapperKey() {
        Path path = Paths.get("WRAPPER_KEY.cnd");
        if (!Files.exists(path)) {
            return null;
        }

        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sleepUninterruptedly(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void header() {
        //System.out.println();
        System.out.print(
             String.format("██████ █      ██████ █   █ ████  §9██    █ █████ █████ §r[%s]§r", NetworkUtils.class.getPackage().getImplementationVersion().trim()));
        System.out.println("█      █      █    █ █   █ █   █ §9█ █   █ █       █§r");
        System.out.println("█      █      █    █ █   █ █   █ §9█  █  █ █████   █§r");
        System.out.println("█      █      █    █ █   █ █   █ §9█   █ █ █       █§r");
        System.out.println("██████ ██████ ██████ █████ ████  §9█    ██ █████   █§r");
        headerOut();
    }

    private static void headerOut() {
        System.out.println("«» The Cloud Network Environment Technology 2");
        System.out.println(String.format("«» Support https://discord.cloudnetservice.eu/      [%s]",
                                       NetworkUtils.class.getPackage().getSpecificationVersion()));
        System.out.println(String.format("«» Java %s @%s %s",
                                       System.getProperty("java.version"),
                                       System.getProperty("user.name"),
                                       System.getProperty("os.name")));
    }

}
