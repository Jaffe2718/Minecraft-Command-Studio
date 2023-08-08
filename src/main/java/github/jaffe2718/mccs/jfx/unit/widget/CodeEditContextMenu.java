package github.jaffe2718.mccs.jfx.unit.widget;

import github.jaffe2718.mccs.MinecraftCommandStudio;
import github.jaffe2718.mccs.jfx.MccsApplication;
import github.jaffe2718.mccs.jfx.unit.CmdExecutor;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.fxmisc.richtext.CodeArea;

import java.awt.Desktop;
import java.io.File;

/**
 * The context menu for the code area
 * */
public class CodeEditContextMenu extends ContextMenu {

    /**
     * The code area
     * */
    public CodeArea codeArea;

    /**
     * The constructor
     * @param codeArea the target code area
     * */
    public CodeEditContextMenu(CodeArea codeArea) {
        super();
        this.codeArea = codeArea;
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        MenuItem undo = new MenuItem("Undo");
        MenuItem redo = new MenuItem("Redo");
        MenuItem selectAll = new MenuItem("Select All");
        MenuItem run = new MenuItem("Run", new ImageView("assets/mccs/jfx/textures/run.png"));
        MenuItem save = new MenuItem("Save");
        MenuItem saveAs = new MenuItem("Save As");
        MenuItem openInExplorer = new MenuItem("Open In Explorer");

        cut.setOnAction(e -> codeArea.cut());
        copy.setOnAction(e -> codeArea.copy());
        paste.setOnAction(e -> codeArea.paste());
        undo.setOnAction(e -> codeArea.undo());
        redo.setOnAction(e -> codeArea.redo());
        selectAll.setOnAction(e -> codeArea.selectAll());
        run.setOnAction(this::runTask);
        save.setOnAction(this::saveTask);
        saveAs.setOnAction(this::saveAsTask);
        openInExplorer.setOnAction(this::openInExplorerTask);

        this.getItems().addAll(cut, copy, paste, undo, redo, selectAll, run, save, saveAs, openInExplorer);
    }

    private void runTask(ActionEvent event) {
        // find runModeComboBox
        ComboBox<String> runModeComboBox = (ComboBox<String>) MccsApplication.stage.getScene().getRoot().lookup("#runModeComboBox");
        // find runButton
        Button runButton = (Button) MccsApplication.stage.getScene().getRoot().lookup("#runButton");
        // execute
        if (runModeComboBox != null && runButton != null) {
            CmdExecutor.execute(this.codeArea, runModeComboBox, runButton);
        }
    }

    private void saveTask(ActionEvent event) {
        //try to locate the file
        Tab currentTab = ((TabPane)MccsApplication.stage.getScene().getRoot().lookup("#codeTabPane")).getSelectionModel().getSelectedItem();
        if (currentTab == null) {
            return;
        }
        String filePath = currentTab.getUserData().toString();
        File file = new File(filePath);
        if (!file.exists()) {                        // if the file does not exist, then save as a new file
            this.saveAsTask(event);
            return;
        }                                            // if the file exists, then save it
        String text = this.codeArea.getText();       // read the text from the code area
        try {                                        // overwrite the file
            FileUtils.writeStringToFile(file, text, "UTF-8");
        } catch (Exception e) {                      // if failed, then print the error message to the outLogCodeArea
            CodeArea outLogCodeArea = (CodeArea) MccsApplication.stage.getScene().getRoot().lookup("#outLogCodeArea");
            if (outLogCodeArea != null) {
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    outLogCodeArea.appendText(stackTraceElement.toString() + "\r\n");
                }
                outLogCodeArea.appendText("Failed to save the file: " + e.getMessage() + "\r\n");
            }
        }
    }

    private void saveAsTask(ActionEvent event) {
        // create a new dialog to choose the path where to save the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.setInitialDirectory(new File(MinecraftCommandStudio.RUNTIME_PATH.toString()));
        // the saved file can be end with .* or .mcfunction
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Minecraft Function", "*.mcfunction")
        );
        // show the dialog
        File saveFile = fileChooser.showSaveDialog(MccsApplication.stage);
        if (saveFile == null) {
            return;
        }
        String text = this.codeArea.getText();  // get the text from the code area
        try {                              // write the text to the file
            FileUtils.writeStringToFile(saveFile, text, "UTF-8");
        } catch (Exception e) {           // if failed, then print the error message to the outLogCodeArea
            CodeArea outLogCodeArea = (CodeArea) MccsApplication.stage.getScene().getRoot().lookup("#outLogCodeArea");
            if (outLogCodeArea != null) {
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    outLogCodeArea.appendText(stackTraceElement.toString() + "\r\n");
                }
                outLogCodeArea.appendText("Failed to save the file: " + e.getMessage() + "\r\n");
            }
        }
    }

    // open the file in the explorer
    private void openInExplorerTask(ActionEvent event) {
        // get the current tab
        Tab currentTab = ((TabPane)MccsApplication.stage.getScene().getRoot().lookup("#codeTabPane")).getSelectionModel().getSelectedItem();
        if (currentTab == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Minecraft Command Studio");
            // set icon
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(MccsApplication.ICON_PATH));
            alert.setHeaderText("Failed to open the file in the explorer");
            alert.setContentText("The current tab is null!");
            alert.showAndWait();
            return;
        }
        // create a File object to represent the file
        File currentFile = new File(currentTab.getUserData().toString());
        if (!currentFile.exists()) {         // new file edit tab, the file does not exist
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Minecraft Command Studio");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(MccsApplication.ICON_PATH));
            alert.setHeaderText("Failed to open the file in the explorer");
            alert.setContentText("The file does not exist!");
            alert.showAndWait();
            return;
        }
        // open the file in the explorer
        try {
            Desktop.getDesktop().open(currentFile.getParentFile());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Minecraft Command Studio");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(MccsApplication.ICON_PATH));
            alert.setHeaderText("Failed to open the file in the explorer");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            // print the error message to the outLogCodeArea
            CodeArea outLogCodeArea = (CodeArea) MccsApplication.stage.getScene().getRoot().lookup("#outLogCodeArea");
            if (outLogCodeArea != null) {
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    outLogCodeArea.appendText(stackTraceElement.toString() + "\r\n");
                }
                outLogCodeArea.appendText("Failed to open the file in the explorer: " + e.getMessage() + "\r\n");
            }
        }
    }

}
