package github.jaffe2718.mccs.jfx.unit.font;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * To be used to highlight the message log in outLogCodeArea<br>
 * <br>
 * There are two kinds of message log: <br>
 * 1. The game massage, begin with [Info], the "[Info]" is green color <br>
 * 2. Java exception, except the game massage are all Java exception, red color <br>
 * <br>
 * Don't extend this class<br>
 * Just use the static methods<br>
 * */
public abstract class MessageLogHighlighter {

    private static final Pattern INFO = Pattern.compile("\\[Info] .*?");
    // Errors are not begin with "[Info]"

    /**
     * Add the highlighter to the target CodeArea
     * @param target the CodeArea to be highlighted
     * */
    public static void applyHighlighting(@NotNull CodeArea target) {
        target.textProperty().addListener((obs, oldText, newText) -> {
            // System.out.println("oldText: " + oldText);
            target.setStyleSpans(0, computeHighlighting(newText));
        });
        target.getStylesheets().add(Objects.requireNonNull(
                MessageLogHighlighter.class.getResource("/assets/mccs/jfx/css/mccs-log.css")).toExternalForm());
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        Matcher matcher = INFO.matcher(text);
        int lastEnd = 0;
        while (matcher.find()) {
            String styleClass = matcher.group() != null ? "info" : null;
            // except the game massage are all Java exception, red color
            if (styleClass != null) {
                spansBuilder.add(Collections.singleton("error"), matcher.start() - lastEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastEnd = matcher.end();
            }
        }
        spansBuilder.add(Collections.singleton("error"), text.length() - lastEnd);            // add the rest of the text
        // unfinished
        return spansBuilder.create();
    }
}
