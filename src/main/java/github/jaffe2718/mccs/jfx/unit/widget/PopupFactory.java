package github.jaffe2718.mccs.jfx.unit.widget;

import github.jaffe2718.mccs.jfx.MccsApplication;
import github.jaffe2718.mccs.jfx.unit.prompt.AISuggestionsRegister;
import github.jaffe2718.mccs.jfx.unit.prompt.CommandPromptRegister;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import org.fxmisc.richtext.CodeArea;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used to create a popup
 * This interface cannot to be instantiated
 * */
public interface PopupFactory {

    /**
     * Create a find popup<br><br>
     * the layout of the popup is like this:  the fist row's length is 20px, the second row's length is 200px<br>
     * | Label("Regex") | TextField                             |      // the first row, height is 20px<br>
     * | Label()        | HBox{Button("Find") | Button("Exit")} |      // the second row, height is 20px<br>
     * <br>
     * This popup will be shown when user click the "Find" button in the toolbar "Edit"
     * It will find the regex in the current tab of the codeTabPane
     * */
    static Popup createFindPopup(){
        Popup findPopup = new Popup();
        GridPane gridPane = new GridPane();       // 2x2 grid
//        gridPane.setHgap(10);
//        gridPane.setVgap(10);
        Label regexLabel = new Label("Regex");
        regexLabel.setPrefWidth(80);
        TextField regexTextField = new TextField();
        regexTextField.setPrefWidth(200);
        regexTextField.idProperty().set("regexFindTextField");    // set id for css
        Button findButton = new Button("Find");
        findButton.onActionProperty().set((ActionEvent event) -> {  // find the regex in the codeArea
            if (regexTextField.getText().isEmpty()) {
                Alert emptyAlert = AlertFactory.createAlert(Alert.AlertType.WARNING, "Empty Regex", "The regex is empty", "Please input the regex!");
                emptyAlert.showAndWait();
            } else {
                // get current tab in the codeTabPane
                TabPane codeTabPane = (TabPane) findPopup.getOwnerWindow().getScene().lookup("#codeTabPane");
                try {
                    CodeArea codeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().get(0);
                    // find the regex in the codeArea, start from the cursor position
                    Pattern pattern = Pattern.compile(regexTextField.getText());
                    int start = codeArea.getCaretPosition();
                    int end = codeArea.getText().length();
                    Matcher matcher = pattern.matcher(codeArea.getText(start, end));
                    if (matcher.find()) {  // select the matched text
                        // update the caret position
                        codeArea.moveTo(start + matcher.end());
                        // select the matched text
                        codeArea.selectRange(start + matcher.start(), start + matcher.end());
                    } else {  // no matched anymore
                        Alert noMatchAlert = AlertFactory.createAlert(Alert.AlertType.WARNING, "No Match", "No match found", "Nothing matched in the code area!");
                        noMatchAlert.showAndWait();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });
        Button exitButton = new Button("Exit");
        exitButton.onActionProperty().set((ActionEvent event) -> {
            if (findPopup.isShowing()) {
                findPopup.hide();
            }
        });
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(findButton, exitButton);  // 200px
        buttonBox.prefWidthProperty().bind(findPopup.widthProperty().subtract(20));
        // set buttonBox: {-fx-alignment: center-right, -fx-spacing: 10px, -fx-padding: 0px 0px 0px 0px}
        buttonBox.setStyle("-fx-alignment: center-right; -fx-spacing: 10; -fx-padding: 10");

        gridPane.add(regexLabel, 0, 0);
        gridPane.add(regexTextField, 1, 0);
        gridPane.add(buttonBox, 1, 1);

        gridPane.setPrefSize(300, 60);
        gridPane.setStyle("-fx-background-color: #E0E0E0");
        findPopup.getContent().add(gridPane);
        return findPopup;
    }

    /**
     * Create a replacement popup<br><br>
     * | Label("Regex") | TextField                                                     |      // the first row, height is 20px<br>
     * | Label("Replace") | TextField                                                   |      // the second row, height is 20px<br>
     * | Null        | HBox{Button("Next") | Button("Replace") | Button("Replace All") | Button("Exit")} |      // the third row, height is 20px<br>
     * <br>
     * This popup will be shown when user click the "Replace" button in the toolbar "Edit"<br>
     * It will be used to replace the text matched the regex<br>
     * */
    static Popup createReplacePopup() {
        Popup replacePopup = new Popup();
        GridPane gridPane = new GridPane();    // 2x3 grid
        Label regexLabel = new Label("Regex");
        regexLabel.setPrefWidth(80);
        TextField regexTextField = new TextField();
        regexTextField.setPrefWidth(200);
        regexTextField.idProperty().set("regexReplaceTextField");    // set id for css
        Label replaceLabel = new Label("Replace");
        replaceLabel.setPrefWidth(80);
        TextField replaceTextField = new TextField();
        replaceTextField.setPrefWidth(200);
        replaceTextField.idProperty().set("replaceTextField");    // set id for css
        Button nextButton = new Button("Next");
        nextButton.onActionProperty().set((ActionEvent event) -> {
            // select the text matched from the cursor position to the end of the codeArea
            if (regexTextField.getText().isEmpty()) {
                // show a warning alert
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Regex is empty");
                alert.setContentText("Please input a regex");
                alert.showAndWait();
            } else {
                // get current tab in the codeTabPane
                TabPane codeTabPane = (TabPane) replacePopup.getOwnerWindow().getScene().lookup("#codeTabPane");
                try {
                    CodeArea codeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().get(0);
                    // find the regex in the codeArea, start from the cursor position
                    Pattern pattern = Pattern.compile(regexTextField.getText());
                    int start = codeArea.getCaretPosition();
                    int end = codeArea.getText().length();
                    Matcher matcher = pattern.matcher(codeArea.getText(start, end));
                    if (matcher.find()) {
                        // update the caret position
                        codeArea.moveTo(start + matcher.end());
                        // select the matched text
                        codeArea.selectRange(start + matcher.start(), start + matcher.end());
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        Button replaceButton = new Button("Replace");
        replaceButton.onActionProperty().set(
                (ActionEvent event1) -> {
                    // replace the selected text with the text in the replaceTextField
                    TabPane codeTabPane = (TabPane) replacePopup.getOwnerWindow().getScene().lookup("#codeTabPane");
                    CodeArea codeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().get(0);
                    if (codeArea.getSelectedText().isEmpty()) {
                        // show a warning alert
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText("No text is selected");
                        alert.setContentText("Please select a text");
                        alert.showAndWait();
                    } else {
                        codeArea.replaceSelection(replaceTextField.getText());
                    }
                }
        );
        Button replaceAllButton = new Button("Replace All");
        replaceAllButton.onActionProperty().set(
                (ActionEvent event1) -> {
                    // replace all the text matched with the text in the replaceTextField
                    TabPane codeTabPane = (TabPane) replacePopup.getOwnerWindow().getScene().lookup("#codeTabPane");
                    CodeArea codeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().get(0);
                    Pattern pattern = Pattern.compile(regexTextField.getText());
                    Matcher matcher = pattern.matcher(codeArea.getText());
                    codeArea.replaceText(matcher.replaceAll(replaceTextField.getText()));
                }
        );
        Button exitButton = new Button("Exit");
        exitButton.onActionProperty().set((ActionEvent event) -> {
            if (replacePopup.isShowing()) {
                replacePopup.hide();
            }
        });
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(nextButton, replaceButton, replaceAllButton, exitButton);  // 200px
        buttonBox.prefWidthProperty().bind(replacePopup.widthProperty().subtract(20));
        // set buttonBox: {-fx-alignment: center-right, -fx-spacing: 10px, -fx-padding: 0px 0px 0px 0px}
        buttonBox.setStyle("-fx-alignment: center-right; -fx-spacing: 10; -fx-padding: 10");
        gridPane.add(regexLabel, 0, 0);
        gridPane.add(regexTextField, 1, 0);
        gridPane.add(replaceLabel, 0, 1);
        gridPane.add(replaceTextField, 1, 1);
        gridPane.add(buttonBox, 1, 2);
        gridPane.setPrefSize(300, 60);
        gridPane.setStyle("-fx-background-color: #E0E0E0");
        replacePopup.getContent().add(gridPane);
        return replacePopup;
    }

    /**
     * Create a prompt popup<br><br>
     * This popup will be shown when the text in the codeArea is changed.<br>
     * Match the last word(maybe an unfinished word or number) in the codeArea by using regex, the last word means the word before the cursor without any space.<br>
     * When the user clicks the prompt, the prompt will be inserted into the codeArea, the last word will be replaced by the prompt which is clicked.
     *
     * @param promptList a list of String, each String is a prompt
     * @return a prompt popup to be shown when the text in the codeArea is changed
     * */
    static Popup createPromptPopup(List<String> promptList) {
        Popup promptPopup = new Popup();
        promptPopup.setAutoFix(true);
        promptPopup.setAutoHide(true);
        promptPopup.setHideOnEscape(true);
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: #E0E0E0");
        vBox.setUserData(0);      // selected index
        // vBox.setPrefSize(300, 60);
        for (int i = 0; i < promptList.size(); i++) {
            String prompt = promptList.get(i);
            Label label = new Label(prompt);
            label.setPrefWidth(150);
            label.setPrefHeight(20);
            label.setOnMouseClicked((MouseEvent event) -> {   // register the mouse click event
                // get current tab in the codeTabPane
                TabPane codeTabPane = (TabPane) MccsApplication.stage.getScene().lookup("#codeTabPane");
                CodeArea codeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().get(0);
                // get the last word(maybe an unfinished word or number, float, symbol, except space) in the codeArea
                // match the last word in the codeArea from the cursor position to the start of the row of the cursor
                Matcher matcher = CommandPromptRegister.LAST_EXPRESSION_PATTERN.matcher(codeArea.getText(0, codeArea.getCaretPosition()));
                if (matcher.find()) {
                    // replace the last word with the prompt
                    codeArea.replaceText(matcher.start(), matcher.end(), label.getText() + "");
                }
                // hide the promptPopup after the prompt is inserted into the codeArea
                promptPopup.hide();
            });
            if (i==(Integer)vBox.getUserData()) {     //selected index
                label.setTextFill(Color.color(0, 0.4, 0));
            }
            label.setStyle("-fx-background-color: #E0E0E0");
            vBox.getChildren().add(label);
        }
        promptPopup.getContent().add(vBox);
        // register the key press event
        promptPopup.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {         // enter or tab
                // get current tab in the codeTabPane
                TabPane codeTabPane = (TabPane) MccsApplication.stage.getScene().lookup("#codeTabPane");
                CodeArea codeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().get(0);
                // get the last word(maybe an unfinished word or number, float, symbol, except space) in the codeArea
                // match the last word in the codeArea from the cursor position to the start of the row of the cursor
                Matcher matcher = CommandPromptRegister.LAST_EXPRESSION_PATTERN.matcher(codeArea.getText(0, codeArea.getCaretPosition()));
                if (matcher.find()) {
                    // replace the last word with the prompt
                    codeArea.replaceText(matcher.start(),
                            matcher.end(),
                            ((Label) vBox.getChildren().get(vBox.getUserData() instanceof Integer int_? int_ : 0))
                                    .getText());
                    // then unable the \n or \t inserted into the codeArea
                    event.consume();
                }
                promptPopup.hide();  // hide the promptPopup after the prompt is inserted into the codeArea
            } else if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {    // up or down key pressed
                vBox.setUserData((Integer) vBox.getUserData() + (event.getCode() == KeyCode.UP ? -1 : 1));
                if ((Integer) vBox.getUserData() < 0) {
                    vBox.setUserData(0);
                } else if ((Integer) vBox.getUserData() >= vBox.getChildren().size()) {
                    vBox.setUserData(vBox.getChildren().size() - 1);
                }
                for (int i = 0; i < vBox.getChildren().size(); i++) {
                    Label label = (Label) vBox.getChildren().get(i);
                    if (i == (Integer) vBox.getUserData()) {
                        label.setTextFill(Color.color(0, 0.4, 0));
                    } else {
                        label.setTextFill(Color.color(0, 0, 0));
                    }
                }
                event.consume();
            } else {
                // if the key pressed is not enter or tab, hide the promptPopup, maybe the escape key is pressed or continue to type something
                promptPopup.hide();
                // a new promptPopup will be created when the text in the codeArea is changed, but this promptPopup will be hidden immediately
            }
        });
        promptPopup.setAutoHide(true);
        promptPopup.setHideOnEscape(true);
        return promptPopup;
    }

    /**
     * Create prompt popup object by generating the prompt by OpenAI API<br><br>
     * Sent the String {@link PopupFactory#createAIPopup(String content)} to the OpenAI API,
     * and ask for the prompt to complete the unfinished command.<br>
     * This popup will be shown when the user presses "Alt + F3"<br>
     * Once the popup is escaped, set {@link AISuggestionsRegister#isShowing} to false<br>
     * @param content the unfinished command
     * @return a prompt popup to be shown when the user presses "Alt + F3"<br>
     * The prompt is generated by OpenAI API
     * <br><br>
     * <br><br>
     * HELP ME: Can any one help me to finish this function?<br>
     * Use OpenAI Official API to get the prompt to complete the unfinished command. <br>
     * om.theokanning.openai-gpt3-java:api:0.12.0 is used to call the OpenAI API.<br>
     * The function is to call the OpenAI API to get the prompt to complete the unfinished command.
     * GPT-3/GPT-3.5-turbo/GPT-4/CodeX are all supported.
     * */
    static Popup createAIPopup(String content) {
        Popup aiPopup = new Popup();
        aiPopup.setAutoFix(true);
        aiPopup.setAutoHide(true);
        aiPopup.setHideOnEscape(true);
        aiPopup.onHidingProperty().addListener((observable, oldValue, newValue) -> {
            AISuggestionsRegister.isShowing = false;
        });
        aiPopup.onHiddenProperty().addListener((observable, oldValue, newValue) -> {
            AISuggestionsRegister.isShowing = false;
        });
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: #E0E0E0");

        return aiPopup;
    }
}
