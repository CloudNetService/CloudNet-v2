package eu.cloudnetservice.v2.master.database;

import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import eu.cloudnetservice.v2.master.network.wrapper.WrapperSession;

/**
 * Created by Tareko on 23.09.2017.
 */
public class WrapperSessionDatabase extends DatabaseUsable {

    public WrapperSessionDatabase(Database database) {
        super(database);
    }

    public void addSession(WrapperSession session) {
        DatabaseDocument databaseDocument = new DatabaseDocument(session.getUniqueId().toString());
        databaseDocument.append("session", session);
        database.insert(databaseDocument);
    }
}
