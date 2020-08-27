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
        /*System.out.println("RAINBOW-TEST");
        char block = '\u2588';
        StringBuilder stringBuilder = new StringBuilder();
        for (final Ansi.Color value : Ansi.Color.values()) {
            Ansi.ansi(stringBuilder).bg(value).a(block).a( " BGD " + value.name()).reset();
            stringBuilder.append("\n");
        }
        for (final Ansi.Color value : Ansi.Color.values()) {
            Ansi.ansi(stringBuilder).bgBright(value).a(block).a( " BGB " + value.name()).reset();
            stringBuilder.append("\n");
        }
        Ansi.ansi(stringBuilder).reset();
        Ansi.ansi(stringBuilder).bg(Ansi.Color.BLACK);
        for (final Ansi.Color value : Ansi.Color.values()) {
            Ansi.ansi(stringBuilder).fg(value).a(block).a( " FGD " + value.name()).reset();
            stringBuilder.append("\n");
        }
        for (final Ansi.Color value : Ansi.Color.values()) {
            Ansi.ansi(stringBuilder).fgBright(value).a(block).a( " FGB " + value.name()).reset();
            stringBuilder.append("\n");
        }
        System.out.println(stringBuilder.toString());
        System.out.println(Ansi.ansi().reset().toString());*/
        CloudBootstrap.main(args);

    }
}
