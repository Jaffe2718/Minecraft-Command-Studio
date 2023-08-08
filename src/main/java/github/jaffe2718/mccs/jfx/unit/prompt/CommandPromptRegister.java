package github.jaffe2718.mccs.jfx.unit.prompt;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import github.jaffe2718.mccs.event.EventHandler;
import github.jaffe2718.mccs.jfx.MccsApplication;
import github.jaffe2718.mccs.jfx.unit.widget.PopupFactory;
import github.jaffe2718.mccs.mixin.ChatInputSuggestorMixin;
import javafx.stage.Popup;
import org.fxmisc.richtext.CodeArea;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Used to add a command prompt to a CodeArea.<br>
 * */
public interface CommandPromptRegister {

    /**
     * The pattern of the last word of the row text
     * */
    Pattern LAST_EXPRESSION_PATTERN = Pattern.compile("[^/ \\n]*[\\da-zA-Z]+$");

    /**
     * Add a command prompt to the codeArea
     * @param target the codeArea to add the command prompt
     * */
    static void addPromptTo(@NotNull CodeArea target) {
        target.textProperty().addListener(
            (obs, oldText, newText)-> {
                if (newText.length() < oldText.length()) {       // if the text is deleted, then hide the popup
                    return;
                }
                // get the row of text that the cursor is on, from the start of the row to the cursor
                int row = target.getCurrentParagraph();
                String rowText = target.getText(row, 0, row, target.getCaretColumn());
                // if the row text ends with a space, then the user is not typing a command
                if (rowText.endsWith(" ") || rowText.endsWith("\n") || rowText.isEmpty()) {
                    return;
                }
                List<String> suggestions = getCommandSuggestions(rowText);  // get the command suggestions
                // if there are suggestions and last word is not in suggestions, then show the popup
                Matcher matcher = LAST_EXPRESSION_PATTERN.matcher(rowText);
                if (!suggestions.isEmpty() && !suggestions.contains(matcher.matches() ? matcher.group() : "")) {
                    // if suggestions is too long, only show the first 10
                    if (suggestions.size() > 10) {
                        suggestions = suggestions.subList(0, 10);
                    }
                    Popup promptPopup = PopupFactory.createPromptPopup(suggestions);
                    // show the popup at the cursor position
                    target.getCaretBounds().ifPresent(
                        bounds -> promptPopup.show(MccsApplication.stage, bounds.getMaxX(), bounds.getMaxY())
                    );
                }
            }
        );
    }

    /**
     * Get the command suggestions for the input
     * In this thread, MinecraftClient.getInstance().setScreen() can not be used,
     * this will cause the game to crash, java.lang.IllegalStateException: Rendersystem called from wrong thread
     * So just set EventHandler.lastCommand = input, and in {@link github.jaffe2718.mccs.event.EventHandler} will switch the screen
     * */
    private static List<String> getCommandSuggestions(String input) {  // unfinished
        EventHandler.lastCommand = input;
        List<Suggestion> suggestionList = new ArrayList<>();
        for (int i=0 ; i<3 ; i++) {                           // wait for the command suggestions
            if (EventHandler.suggestor != null) {
                CompletableFuture<Suggestions> pendingSuggestions = ((ChatInputSuggestorMixin) EventHandler.suggestor).getPendingSuggestions();
                if (pendingSuggestions != null) {
                    Suggestions suggestions = pendingSuggestions.join();
                    suggestionList.addAll(suggestions.getList());
                }
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return suggestionList.stream().map(Suggestion::getText).toList();
    }
}
