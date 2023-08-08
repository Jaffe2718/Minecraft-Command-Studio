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
 * Used to highlight Minecraft instruction syntax in a CodeArea.
 * Uses a regular expression to match commands, numbers, and braces.
 * example: /give @p minecraft:stone{CustomModelData:1}
 * Use Guide: MinecraftSyntaxHighlighter.applyHighlighting(yourCodeArea);
 * */
public abstract class MinecraftSyntaxHighlighter {

    private static final String KEYWORD_PATTERN = "(?m)^(/?\\w+)";                    // keyword (command name)
    private static final String NUMBER_PATTERN = "[+-]?(?<!\\w)\\d+(?:\\.\\d+)?";     // number
    private static final String PUNCTUATION_PATTERN = "[~^@<=>\",{}()\\[\\]]";        // punctuation

    private static final Pattern PATTERN = Pattern.compile(                           // match all
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            + "|(?<PUNCTUATION>" + PUNCTUATION_PATTERN + ")"
    );

    /**
     * Add the highlighter to the target CodeArea
     * @param codeArea the CodeArea to be highlighted
     * */
    public static void applySyntaxHighlighting(@NotNull CodeArea codeArea) {
        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });
        codeArea.getStylesheets().add(Objects.requireNonNull(
                MinecraftSyntaxHighlighter.class.getResource("/assets/mccs/jfx/css/minecraft-command.css")).toExternalForm());
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        Matcher matcher = PATTERN.matcher(text);
        int lastEnd = 0;
        while (matcher.find()) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("NUMBER") != null ? "number" :
                            matcher.group("PUNCTUATION") != null ? "punctuation" :
                                    null;
            // only add style if non-null, styleClass is in {"keyword", "number", "punctuation"}
            if (styleClass != null) {
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastEnd = matcher.end();
            }
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastEnd);
        return spansBuilder.create();
    }

}