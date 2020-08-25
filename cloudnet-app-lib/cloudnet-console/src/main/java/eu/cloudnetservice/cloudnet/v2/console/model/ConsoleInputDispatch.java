package eu.cloudnetservice.cloudnet.v2.console.model;

import org.jline.reader.LineReader;

import java.util.Collection;
import java.util.function.Supplier;

public interface ConsoleInputDispatch extends Supplier<Collection<String>> {

    void dispatch(String line, LineReader lineReader);

}
