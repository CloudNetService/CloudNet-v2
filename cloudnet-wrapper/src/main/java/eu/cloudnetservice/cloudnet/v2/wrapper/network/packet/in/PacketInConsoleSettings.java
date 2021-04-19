package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.console.completer.CloudNetCompleter;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.completer.ArgumentCompleter;

public class PacketInConsoleSettings implements PacketInHandler {
    @Override
    public void handleInput(final Packet packet, final PacketSender packetSender) {
        if (!packet.getData().contains("console")) {
            return;
        }
        CloudNetWrapper.getInstance().getCommandManager().setAliases(packet.getData().getDocument("console").getBoolean("aliases"));
        CloudNetWrapper.getInstance().getCommandManager().setShowDescription(packet.getData().getDocument("console").getBoolean("showdescription"));
        final LineReader lineReader = CloudNetWrapper.getInstance().getConsoleManager().getLineReader();
        lineReader.option(LineReader.Option.GROUP, packet.getData().getDocument("console").getBoolean("showgroup"));
        lineReader.option(LineReader.Option.ERASE_LINE_ON_FINISH, packet.getData().getDocument("console").getBoolean("elof"));
        lineReader.option(LineReader.Option.AUTO_GROUP, packet.getData().getDocument("console").getBoolean("showgroup"));
        lineReader.option(LineReader.Option.MENU_COMPLETE, packet.getData().getDocument("console").getBoolean("showmenu"));
        lineReader.option(LineReader.Option.AUTO_MENU, packet.getData().getDocument("console").getBoolean("showmenu"));
        lineReader.option(LineReader.Option.AUTO_LIST, packet.getData().getDocument("console").getBoolean("autolist"));
        if (lineReader instanceof LineReaderImpl) {
            Completer completer = ((LineReaderImpl) lineReader).getCompleter();
            if (completer instanceof ArgumentCompleter) {
                ((CloudNetCompleter) ((ArgumentCompleter) completer).getCompleters().get(0)).setGroupColor(packet.getData().getDocument("console").getString("groupcolor"));
                ((CloudNetCompleter) ((ArgumentCompleter) completer).getCompleters().get(0)).setShowDescription(packet.getData().getDocument("console").getBoolean("showdescription"));
                ((CloudNetCompleter) ((ArgumentCompleter) completer).getCompleters().get(0)).setColor(packet.getData().getDocument("console").getString("color"));
            }

        }
    }
}
