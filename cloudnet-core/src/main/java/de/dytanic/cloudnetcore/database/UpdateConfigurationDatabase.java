package de.dytanic.cloudnetcore.database;

import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;
import de.dytanic.cloudnet.lib.utility.document.Document;

public final class UpdateConfigurationDatabase extends DatabaseUsable {

    private static final String NAME = "update_configurations";

    public UpdateConfigurationDatabase(Database database) {
        super(database);

        if (database.getDocument(NAME) == null) {
            database.insert(new DatabaseDocument(NAME));
        }
    }

    public void set(Document document) {
        if (document.contains(Database.UNIQUE_NAME_KEY) && document.getString(Database.UNIQUE_NAME_KEY).equals(NAME)) {
            database.insert(document);
        }
    }

    public Document get() {
        return database.getDocument(NAME);
    }

}
