package eu.cloudnetservice.cloudnet.v2.wrapper.bootstrap;

import org.fusesource.jansi.AnsiConsole;

public class CloudNetLauncher {

    public static void main(String[] args) {
        if (Float.parseFloat(System.getProperty("java.class.version")) < 52D) {
            System.out.println("This application needs Java 8 or 10.0.1");
            return;
        }
        AnsiConsole.systemInstall();
        CloudBootstrap.main(args);

    }
}
