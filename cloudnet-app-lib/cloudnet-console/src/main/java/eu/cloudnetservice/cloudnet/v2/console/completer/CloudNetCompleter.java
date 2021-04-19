package eu.cloudnetservice.cloudnet.v2.console.completer;

import eu.cloudnetservice.cloudnet.v2.logging.color.AnsiColorReplacer;
import eu.cloudnetservice.cloudnet.v2.logging.color.ChatColor;
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

    private boolean showDescription = true;

    protected Supplier<Collection<Candidate>> candidatesSupplier;
    private static final Pattern ANSI_CODE_PATTERN = Pattern.compile("\033\\[[\060-\077]*[\040-\057]*[\100-\176]");
    private String color = "§3";
    private String groupColor = "§8";

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
            if (candidateCollection != null && candidateCollection.size() > 0) {
                candidates.addAll(candidateCollection.stream().map(this::replaceColor).map(this::formatColor).map(this::replaceColor).collect(Collectors.toList()));
            }
        }
    }

    private Candidate replaceColor(Candidate candidate) {
        return new Candidate(candidate.value(),
                             ChatColor.STRIP_COLOR_PATTERN.matcher(candidate.displ()).find() ? AnsiColorReplacer.replaceAnsiWithoutReset(
                                 candidate.displ()) : candidate.displ(),
                             candidate.group() != null ? ChatColor.STRIP_COLOR_PATTERN.matcher(candidate.group()).find() ? AnsiColorReplacer
                                 .replaceAnsiWithoutReset(candidate.group()) : candidate.group() : null,
                             candidate.descr() != null ? ChatColor.STRIP_COLOR_PATTERN.matcher(candidate.descr()).find() ? AnsiColorReplacer
                                 .replaceAnsiWithoutReset(candidate.descr()) : candidate.descr() : null,
                             candidate.suffix() != null ? ChatColor.STRIP_COLOR_PATTERN.matcher(candidate.suffix())
                                                                                       .find() ? AnsiColorReplacer.replaceAnsiWithoutReset(
                                 candidate.suffix()) : candidate.suffix() : null,
                             candidate.key() != null ? ChatColor.STRIP_COLOR_PATTERN.matcher(candidate.key())
                                                                                    .find() ? AnsiColorReplacer.replaceAnsiWithoutReset(
                                 candidate.key()) : candidate.key() : null,
                             candidate.complete());
    }

    private Candidate formatColor(Candidate candidate) {
        if (CloudNetCompleter.ANSI_CODE_PATTERN.matcher(candidate.displ()).find()) {
            return candidate;
        }
        Candidate ansiCandidate;
        if (candidate.group() != null) {
            ansiCandidate = new Candidate(candidate.value(), color + candidate.displ(),
                                          groupColor + candidate.group(),
                                          showDescription ? candidate.descr() : null,
                                          candidate.suffix(),
                                          candidate.key(),
                                          candidate.complete());
        } else {
            ansiCandidate = new Candidate(candidate.value(),
                                          color + candidate.displ(),
                                          candidate.group(),
                                          showDescription ? candidate.descr() : null,
                                          candidate.suffix(),
                                          candidate.key(),
                                          candidate.complete());
        }
        return ansiCandidate;
    }

    public void setShowDescription(final boolean showDescription) {
        this.showDescription = showDescription;
    }

    public boolean isShowDescription() {
        return showDescription;
    }

    public void setGroupColor(final String groupColor) {
        this.groupColor = groupColor;
    }

    public void setColor(final String color) {
        this.color = color;
    }
}
