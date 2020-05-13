package eu.cloudnetservice.v2.master.database;

import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;

public final class UpdateConfigurationDatabase extends DatabaseUsable {

    private static final String NAME = "update_configurations";

    public UpdateConfigurationDatabase(Database database) {
        super(database);

        if (database.getDocument(NAME) == null) {
            database.insert(new DatabaseDocument(NAME));
        }
    }

    public void set(DatabaseDocument document) {
        if (document.contains(Database.UNIQUE_NAME_KEY) && document.getString(Database.UNIQUE_NAME_KEY).equals(NAME)) {
            database.insert(document);
        }
    }

    public DatabaseDocument get() {
        return database.getDocument(NAME);
    }

}
