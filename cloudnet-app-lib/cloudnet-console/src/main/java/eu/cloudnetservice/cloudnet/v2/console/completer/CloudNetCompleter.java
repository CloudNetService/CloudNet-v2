package eu.cloudnetservice.cloudnet.v2.console.completer;

import org.fusesource.jansi.Ansi;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CloudNetCompleter implements Completer {

    protected Supplier<Collection<Candidate>> candidatesSupplier;
    private static final Pattern ANSI_CODE_PATTERN = Pattern.compile("\033\\[[\060-\077]*[\040-\057]*[\100-\176]");

    public CloudNetCompleter(Supplier<Collection<Candidate>> candidatesSupplier) {
        assert candidatesSupplier != null;
        this.candidatesSupplier = candidatesSupplier;
    }

    @Override
    public void complete(LineReader reader, final ParsedLine commandLine, final List<Candidate> candidates) {
        assert commandLine != null;
        assert candidates != null;
        if (this.candidatesSupplier != null) {
            final Collection<Candidate> candidateCollection = this.candidatesSupplier.get();
            if (candidateCollection != null || candidateCollection.size() > 0) {
                candidates.addAll(candidateCollection.stream().map(this::formatColor).collect(Collectors.toList()));
            }
        }
    }
    private Candidate formatColor(Candidate candidate) {
        if (CloudNetCompleter.ANSI_CODE_PATTERN.matcher(candidate.displ()).find() || CloudNetCompleter.ANSI_CODE_PATTERN.matcher(candidate.descr()).find()) {
            return candidate;
        }
        Candidate ansiCandidate;
        if (candidate.group() != null) {
            ansiCandidate = new Candidate(candidate.value(), Ansi.ansi().fg(Ansi.Color.BLUE).a(candidate.displ()).toString(), Ansi.ansi().a(
                Ansi.Attribute.INTENSITY_BOLD).fg(Ansi.Color.BLACK).bg(
                Ansi.Color.BLACK).a(candidate.group()).toString(), candidate.descr(), candidate.suffix(), candidate.key(), candidate.complete());
        } else {
            ansiCandidate = new Candidate(candidate.value(), Ansi.ansi().fg(Ansi.Color.BLUE).a(candidate.displ()).toString(),candidate.group(),candidate.descr(), candidate.suffix(),candidate.key(),candidate.complete());
        }
        return ansiCandidate;
    }
}
