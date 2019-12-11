/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.database;

import de.dytanic.cloudnet.database.DatabaseUsable;
import de.dytanic.cloudnet.lib.database.Database;
import de.dytanic.cloudnet.lib.database.DatabaseDocument;

public class CommandDispatcherDatabase extends DatabaseUsable {

    public CommandDispatcherDatabase(Database database) {
        super(database);
        if (!database.contains("dispatch_commands")) {
            database.insert(new DatabaseDocument("dispatch_commands"));
        }
    }

    public void appendCommand(String dispatcher, String commandLine) {
        DatabaseDocument document = database.getDocument("dispatch_commands");
        document.append(dispatcher, commandLine);
        database.insert(document);
    }

    public void deleteDispatch(String dispatch) {
        DatabaseDocument document = database.getDocument("dispatch_commands");
        document.remove(dispatch);
        database.insert(document);
    }

    public String findDispatcher(String dispatch) {
        DatabaseDocument document = database.getDocument("dispatch_commands");
        return document.getString(dispatch);
    }

}
