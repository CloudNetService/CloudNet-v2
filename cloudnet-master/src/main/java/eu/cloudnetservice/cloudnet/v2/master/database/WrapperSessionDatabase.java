package eu.cloudnetservice.cloudnet.v2.master.database;

import eu.cloudnetservice.cloudnet.v2.database.DatabaseUsable;
import eu.cloudnetservice.cloudnet.v2.lib.database.Database;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import eu.cloudnetservice.cloudnet.v2.master.network.wrapper.WrapperSession;

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
