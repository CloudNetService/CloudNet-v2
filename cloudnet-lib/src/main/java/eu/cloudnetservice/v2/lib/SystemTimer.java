package eu.cloudnetservice.v2.lib;

import java.lang.management.ManagementFactory;

/**
 * Created by Tareko on 23.09.2017.
 */
public class SystemTimer {

    public static void run() {
        System.out.println(String.format("Memory [\"%dKB\"] | CPU Programm [\"%s\"] | CPU System [\"%s\"]",
                                         ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024,
                                         NetworkUtils.DECIMAL_FORMAT.format(NetworkUtils.internalCpuUsage()),
                                         NetworkUtils.DECIMAL_FORMAT.format(NetworkUtils.cpuUsage())));
    }
}
