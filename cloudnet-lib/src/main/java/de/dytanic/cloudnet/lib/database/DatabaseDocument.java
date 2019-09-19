package de.dytanic.cloudnet.lib.database;

import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 01.07.2017.
 */
public class DatabaseDocument extends Document {

    public DatabaseDocument(String name) {
        super(name);
        append(Database.UNIQUE_NAME_KEY, name);
    }

    public DatabaseDocument insert(Database database) {
        database.insert(this);
        return this;
    }
}
