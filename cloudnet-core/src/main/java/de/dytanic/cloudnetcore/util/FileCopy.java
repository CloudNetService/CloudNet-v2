
/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileCopy {

    public static void insertData(String sourceFile, String destinationFile) {
        try (InputStream localInputStream = FileCopy.class.getClassLoader().getResourceAsStream(sourceFile)) {
            if (localInputStream != null) {
                Files.copy(localInputStream, Paths.get(destinationFile));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
