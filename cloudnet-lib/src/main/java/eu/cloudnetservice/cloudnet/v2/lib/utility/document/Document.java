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

package eu.cloudnetservice.cloudnet.v2.lib.utility.document;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Document {

    public static Gson GSON = new GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    protected String name;
    private JsonObject dataCatcher;
    private File file;

    public Document(String name) {
        this.name = name;
        this.dataCatcher = new JsonObject();
    }

    public Document(String name, JsonObject source) {
        this.name = name;
        this.dataCatcher = source;
    }

    public Document(File file, JsonObject jsonObject) {
        this.file = file;
        this.dataCatcher = jsonObject;
    }

    public Document(String key, String value) {
        this.dataCatcher = new JsonObject();
        this.append(key, value);
    }

    public Document append(String key, String value) {
        if (value == null) {
            return this;
        }
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public Document append(String key, Number value) {
        if (value == null) {
            return this;
        }
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public Document append(String key, Boolean value) {
        if (value == null) {
            return this;
        }
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public Document append(String key, JsonElement value) {
        if (value == null) {
            return this;
        }
        this.dataCatcher.add(key, value);
        return this;
    }

    public Document append(String key, Object value) {
        if (value == null) {
            return this;
        }
        if (value instanceof Document) {
            this.append(key, (Document) value);
            return this;
        }
        this.dataCatcher.add(key, GSON.toJsonTree(value));
        return this;
    }

    public Document remove(String key) {
        this.dataCatcher.remove(key);
        return this;
    }

    public Set<String> keys() {
        Set<String> c = new HashSet<>();

        for (Map.Entry<String, JsonElement> x : dataCatcher.entrySet()) {
            c.add(x.getKey());
        }

        return c;
    }

    public String getString(String key) {
        if (!dataCatcher.has(key)) {
            return null;
        }
        return dataCatcher.get(key).getAsString();
    }

    public int getInt(String key) {
        if (!dataCatcher.has(key)) {
            return 0;
        }
        return dataCatcher.get(key).getAsInt();
    }

    public long getLong(String key) {
        if (!dataCatcher.has(key)) {
            return 0L;
        }
        return dataCatcher.get(key).getAsLong();
    }

    public double getDouble(String key) {
        if (!dataCatcher.has(key)) {
            return 0D;
        }
        return dataCatcher.get(key).getAsDouble();
    }

    public boolean getBoolean(String key) {
        if (!dataCatcher.has(key)) {
            return false;
        }
        return dataCatcher.get(key).getAsBoolean();
    }

    public float getFloat(String key) {
        if (!dataCatcher.has(key)) {
            return 0F;
        }
        return dataCatcher.get(key).getAsFloat();
    }

    public short getShort(String key) {
        if (!dataCatcher.has(key)) {
            return 0;
        }
        return dataCatcher.get(key).getAsShort();
    }

    public String convertToJson() {
        return GSON.toJson(dataCatcher);
    }

    public static Document loadDocument(Path backend) {
        return new Document().loadToExistingDocument(backend);
    }

    public boolean saveAsConfig(String path) {
        return saveAsConfig(Paths.get(path));
    }

    public Document getDocument(String key) {
        if (!dataCatcher.has(key)) {
            return null;
        }
        return new Document(dataCatcher.get(key).getAsJsonObject());
    }

    public Document(String key, Object value) {
        this.dataCatcher = new JsonObject();
        this.append(key, value);
    }

    public Document append(String key, Document value) {
        if (value == null) {
            return this;
        }
        this.dataCatcher.add(key, value.dataCatcher);
        return this;
    }

    public Document(String key, Number value) {
        this.dataCatcher = new JsonObject();
        this.append(key, value);
    }

    public Document(Document defaults, String name) {
        this.dataCatcher = defaults.dataCatcher;
        this.name = name;
    }

    public Document() {
        this.dataCatcher = new JsonObject();
    }

    public Document(JsonObject source) {
        this.dataCatcher = source;
    }

    public static Document loadDocument(File backend) {
        return loadDocument(backend.toPath());
    }

    public static Document load(String input) {
        try {
            return new Document(JsonParser.parseString(input).getAsJsonObject());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return new Document();
        }
    }

    public Document loadToExistingDocument(Path path) {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            this.dataCatcher = JsonParser.parseReader(reader).getAsJsonObject();
            return this;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Document();
    }

    public boolean saveAsConfig(File backend) {
        if (backend == null) {
            return false;
        }

        try (OutputStreamWriter writer = new FileWriter(backend, false)) {
            GSON.toJson(dataCatcher, writer);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static Document load(JsonObject input) {
        return new Document(input);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public JsonObject obj() {
        return dataCatcher;
    }

    public Document append(String key, List<String> value) {
        if (value == null) {
            return this;
        }
        JsonArray jsonElements = new JsonArray();

        for (String b : value) {
            jsonElements.add(b);
        }

        this.dataCatcher.add(key, jsonElements);
        return this;
    }

    public Document appendValues(Map<String, Object> values) {
        values.forEach(this::append);
        return this;
    }

    public JsonElement get(String key) {
        if (!dataCatcher.has(key)) {
            return null;
        }
        return dataCatcher.get(key);
    }

    public <T> T getObject(String key, Class<T> class_) {
        if (!dataCatcher.has(key)) {
            return null;
        }

        return GSON.fromJson(dataCatcher.get(key), class_);
    }

    public Document clear() {
        for (String key : keys()) {
            remove(key);
        }
        return this;
    }

    public int size() {
        return this.dataCatcher.size();
    }

    public Document loadProperties(Properties properties) {
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            Object x = enumeration.nextElement();
            this.append(x.toString(), properties.getProperty(x.toString()));
        }
        return this;
    }

    public boolean isEmpty() {
        return this.dataCatcher.size() == 0;
    }

    public JsonArray getArray(String key) {
        return dataCatcher.get(key).getAsJsonArray();
    }

    public boolean saveAsConfig(Path path) {
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(dataCatcher, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Document loadToExistingDocument(File file) {
        try (Reader reader = new FileReader(file)) {
            this.dataCatcher = JsonParser.parseReader(reader).getAsJsonObject();
            this.file = file;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return convertToJsonString();
    }

    public String convertToJsonString() {
        return dataCatcher.toString();
    }

    public <T> T getObject(String key, Type type) {
        if (!contains(key)) {
            return null;
        }

        return GSON.fromJson(dataCatcher.get(key), type);
    }

    public boolean contains(String key) {
        return this.dataCatcher.has(key);
    }

    public byte[] toBytes() {
        return convertToJsonString().getBytes(StandardCharsets.UTF_8);
    }

}
