/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.network.protocol.file;

import de.dytanic.cloudnet.lib.network.protocol.IProtocol;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolStream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Tareko on 09.09.2017.
 */
public class FileProtocol implements IProtocol {

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public Collection<Class<?>> getAvailableClasses() {
        return Arrays.asList(File.class, Path.class, FileDeploy.class);
    }

    @Override
    public ProtocolStream createElement(Object element) {
        if (element.getClass().equals(File.class)) {
            try {
                byte[] input = Files.readAllBytes(Paths.get(((File) element).getPath()));
                String dest = ((File) element).getPath();
                return new FileDeploy(dest, input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (element.getClass().equals(Path.class)) {
            try {
                byte[] input = Files.readAllBytes((Path) element);
                String dest = ((Path) element).toUri().toString();
                return new FileDeploy(dest, input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (element instanceof FileDeploy) {
            return (FileDeploy) element;
        }

        return null;
    }

    @Override
    public ProtocolStream createEmptyElement() {
        return new FileDeploy();
    }
}
