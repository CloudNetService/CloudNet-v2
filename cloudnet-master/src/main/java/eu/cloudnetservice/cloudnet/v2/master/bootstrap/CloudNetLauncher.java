package eu.cloudnetservice.cloudnet.v2.master.bootstrap;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * Created by Tareko on 18.09.2017.
 */
public class CloudNetLauncher {

    public static synchronized void main(String[] args) throws Exception {
        if (Float.parseFloat(System.getProperty("java.class.version")) < 52D) {
            System.out.println("This application needs Java 8 or 10.0.1");
            return;
        }
        AnsiConsole.systemInstall();
        CloudBootstrap.main(args);

    }
}
