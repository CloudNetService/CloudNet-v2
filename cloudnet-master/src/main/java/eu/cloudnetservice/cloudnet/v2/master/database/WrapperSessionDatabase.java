/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.master.database;

import eu.cloudnetservice.cloudnet.v2.database.DatabaseUsable;
import eu.cloudnetservice.cloudnet.v2.lib.database.Database;
import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import eu.cloudnetservice.cloudnet.v2.master.network.wrapper.WrapperSession;

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
