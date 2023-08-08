package github.jaffe2718.mccs.jfx;

import github.jaffe2718.mccs.MinecraftCommandStudio;
import github.jaffe2718.mccs.jfx.unit.CmdExecutor;
import github.jaffe2718.mccs.jfx.unit.font.MessageLogHighlighter;
import github.jaffe2718.mccs.jfx.unit.font.MinecraftSyntaxHighlighter;
import github.jaffe2718.mccs.jfx.unit.PathNodeItem;
import github.jaffe2718.mccs.jfx.unit.prompt.CommandPromptRegister;
import github.jaffe2718.mccs.jfx.unit.widget.AlertFactory;
import github.jaffe2718.mccs.jfx.unit.widget.CodeEditContextMenu;
import github.jaffe2718.mccs.jfx.unit.widget.PopupFactory;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * the controller of the mccs IDE
 * */
public class MccsController implements Initializable {

    /**
     * the popup of find
     * */
    public Popup findPopup = PopupFactory.createFindPopup();

    /**
     * the popup of replace
     * */
    public Popup replacePopup = PopupFactory.createReplacePopup();

    //-----------------------------------------------------------//
    /***/
    @FXML public BorderPane root;                           // Main Window
    /***/
    @FXML   public MenuBar mainMenuBar;                     // Menu Bar
    /***/
    @FXML   public ToolBar topToolBar;
    /***/
    @FXML       public ComboBox<String> runModeComboBox;
    /***/
    @FXML       public Button runButton;
    /***/
    @FXML   public TabPane leftTabPane;
    /***/
    @FXML       public Tab fileExplorerTab;
    /***/
    @FXML           public TreeView<PathNodeItem> fileTreeView;
    /***/
    @FXML               public ContextMenu fileTreeContextMenu;
    /***/
    @FXML                   public MenuItem openMenuItem;
    /***/
    @FXML                   public Menu newMenu;
    /***/
    @FXML                       public MenuItem newFileMenuItem;
    /***/
    @FXML                       public MenuItem newDirectoryMenuItem;
    /***/
    @FXML                   public Menu refactorMenu;
    /***/
    @FXML                       public MenuItem renameMenuItem;
    /***/
    @FXML                       public MenuItem copyToMenuItem;
    /***/
    @FXML                       public MenuItem moveToMenuItem;
    /***/
    @FXML                   public MenuItem deleteMenuItem;
    /***/
    @FXML                   public MenuItem openInExplorerMenuItem;
    /***/
    @FXML   public TabPane rightTabPane;
    /***/
    @FXML           public VBox mcCmdTabVBox;
    /***/
    @FXML           public VBox sysCmdTabVBox;
    /***/
    @FXML           public VBox toolsTabVBox;
    /***/
    @FXML   public TabPane codeTabPane;
    /***/
    @FXML   public VBox bottomVBox;
    /***/
    @FXML      public ToolBar outToolBar;
    /***/
    @FXML           public Button buttonHideShow;
    /***/
    @FXML      public CodeArea outLogCodeArea;



    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainMenuBar.prefWidthProperty().bind(root.getTop().layoutXProperty());
        rightTabPane.prefHeightProperty().bind(root.getRight().layoutYProperty());
        leftTabPane.prefHeightProperty().bind(root.getRight().layoutYProperty());
        codeTabPane.prefHeightProperty().bind(root.getCenter().layoutYProperty());
        codeTabPane.prefWidthProperty().bind(root.getCenter().layoutXProperty());
        codeTabPane.tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.ALL_TABS);
        buttonHideShow.setText("Show");
        bottomVBox.prefHeightProperty().set(25);
        runButton.setGraphic(new ImageView("assets/mccs/jfx/textures/run.png"));
        rightTabPane.setPrefWidth(30);           // Collapse the right sidebar
        for (Tab tab: rightTabPane.getTabs()) {  // set the state HIDDEN for all tabs
            tab.setUserData("HIDDEN");
        }
        leftTabPane.setPrefWidth(30);            // Collapse the left sidebar
        MessageLogHighlighter.applyHighlighting(outLogCodeArea);
        // gen tree root
        PathNodeItem rootPath = new PathNodeItem(MinecraftCommandStudio.RUNTIME_PATH.toString());
        TreeItem<PathNodeItem> rootTreeNode = new TreeItem<>(rootPath);
        rootTreeNode.setGraphic(rootPath.icon);
        fileTreeView.setRoot(rootTreeNode);
        AnchorPane.setTopAnchor(fileTreeView, 0.0);
        AnchorPane.setBottomAnchor(fileTreeView, 0.0);
        AnchorPane.setLeftAnchor(fileTreeView, 0.0);
        AnchorPane.setRightAnchor(fileTreeView, 0.0);
        fileTreeView.setVisible(true);
        runModeComboBox.getItems().addAll("Single Line", "Script", "Selection", "Loop", "Selection Loop");
        runModeComboBox.getSelectionModel().select(0);
        this.initToolsTab();
        this.initMcCmdTab();
        this.initSysConsoleTab();
    }

    /**
     * Init the tools tab<br>
     * Add the tools to the tab<br>
     *  Minecraft Wiki<br>
     *  Minecraft Official Website<br>
     *  GamerGeeks<br>
     * */
    private void initToolsTab() {
        Label toolsLabel = new Label("Tools");
        toolsLabel.setPrefWidth(250);
        toolsLabel.setStyle("-fx-text-fill: #00BFFF; -fx-font-weight: bold; -fx-font-size: 40px;");
        toolsLabel.setAlignment(Pos.CENTER);
        toolsLabel.setTextAlignment(TextAlignment.CENTER);
        Button mcNetButton = new Button("Minecraft Official Website");
        mcNetButton.setPrefWidth(250);
        mcNetButton.setGraphic(new ImageView("assets/mccs/jfx/textures/mc_net.png"));
        mcNetButton.setGraphicTextGap(10);
        mcNetButton.setAlignment(Pos.CENTER_LEFT);
        mcNetButton.setTextAlignment(TextAlignment.RIGHT);
        mcNetButton.setContentDisplay(ContentDisplay.LEFT);
        mcNetButton.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.minecraft.net/"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        Button mcWikiButton = new Button("Minecraft Wiki");
        mcWikiButton.setPrefWidth(250);
        mcWikiButton.setGraphic(new ImageView("assets/mccs/jfx/textures/mc_wiki.png"));
        mcWikiButton.setGraphicTextGap(10);
        mcWikiButton.setAlignment(Pos.CENTER_LEFT);
        mcWikiButton.setTextAlignment(TextAlignment.RIGHT);
        mcWikiButton.setContentDisplay(ContentDisplay.LEFT);
        mcWikiButton.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://minecraft.fandom.com/wiki/Minecraft_Wiki"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        Button gamerGeeksButton = new Button("GAMEGEAKS");
        gamerGeeksButton.setPrefWidth(250);
        gamerGeeksButton.setGraphic(new ImageView("assets/mccs/jfx/textures/gamergeeks.png"));
        gamerGeeksButton.setGraphicTextGap(10);
        gamerGeeksButton.setAlignment(Pos.CENTER_LEFT);
        gamerGeeksButton.setTextAlignment(TextAlignment.RIGHT);
        gamerGeeksButton.setContentDisplay(ContentDisplay.LEFT);
        gamerGeeksButton.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.gamergeeks.net/apps/minecraft"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        toolsTabVBox.getChildren().addAll(toolsLabel, mcNetButton, mcWikiButton, gamerGeeksButton);
    }

    /**
     * Init the mc cmd tab<br>
     * Add the mc cmd to the tab<br>
     * */
    private void initMcCmdTab() {
        // add a text area to the tab
        TextArea mcCmdTabTextArea = new TextArea();
        mcCmdTabTextArea.setPrefWidth(250);
        mcCmdTabTextArea.setPrefHeight(250);
        mcCmdTabTextArea.widthProperty().addListener((observable, oldValue, newValue) -> mcCmdTabTextArea.setPrefWidth(newValue.doubleValue()));
        mcCmdTabTextArea.heightProperty().addListener((observable, oldValue, newValue) -> mcCmdTabTextArea.setPrefHeight(newValue.doubleValue() + 80));
        mcCmdTabTextArea.editableProperty().set(false);
        mcCmdTabTextArea.setWrapText(true);
        mcCmdTabTextArea.setStyle("-fx-background-color: #F0F0F0;");
        mcCmdTabVBox.getChildren().add(mcCmdTabTextArea);
        // add a textField to the tab as input
        TextField mcCmdTabTextField = new TextField();
        mcCmdTabTextField.setPrefWidth(250);
        mcCmdTabTextField.widthProperty().addListener((observable, oldValue, newValue) -> mcCmdTabTextField.setPrefWidth(newValue.doubleValue()));
        mcCmdTabTextField.setOnAction(event -> {
            CmdExecutor.executeMcShell(mcCmdTabTextArea, mcCmdTabTextField);
            event.consume();
        });
        // set prompt text
        mcCmdTabTextField.setPromptText("Input the Minecraft command here & press Enter to execute");
        mcCmdTabVBox.getChildren().add(mcCmdTabTextField);
        // do not focus on the tap mcCmdTabTextField for the first time
        mcCmdTabTextField.setFocusTraversable(false);
    }

    /**
     * Init the system console tab<br>
     * Add the system console to the tab<br>
     * */
    private void initSysConsoleTab() {
        // add a text area to the tab
        TextArea sysConsoleTabTextArea = new TextArea();
        sysConsoleTabTextArea.setPrefWidth(250);
        sysConsoleTabTextArea.setPrefHeight(250);
        sysConsoleTabTextArea.widthProperty().addListener((observable, oldValue, newValue) -> sysConsoleTabTextArea.setPrefWidth(newValue.doubleValue()));
        sysConsoleTabTextArea.heightProperty().addListener((observable, oldValue, newValue) -> sysConsoleTabTextArea.setPrefHeight(newValue.doubleValue() + 80));
        sysConsoleTabTextArea.editableProperty().set(false);
        sysConsoleTabTextArea.setWrapText(true);
        sysConsoleTabTextArea.setStyle("-fx-background-color: #F0F0F0;");
        sysCmdTabVBox.getChildren().add(sysConsoleTabTextArea);
        // add a textField to the tab as input
        TextField sysConsoleTabTextField = new TextField();
        sysConsoleTabTextField.setPrefWidth(250);
        sysConsoleTabTextField.widthProperty().addListener((observable, oldValue, newValue) -> sysConsoleTabTextField.setPrefWidth(newValue.doubleValue()));
        sysConsoleTabTextField.setPromptText("Input the system command here & press Enter to execute");
        sysConsoleTabTextField.setOnAction(event -> {
            CmdExecutor.executeSysCmd(sysConsoleTabTextArea, sysConsoleTabTextField);
            event.consume();
        });
        sysCmdTabVBox.getChildren().add(sysConsoleTabTextField);
    }

    /**
     * view the file tree
     * @param mouseEvent the mouse event
     * */
    public void getOnMouseClickFileTreeView(@NotNull MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {      //Left click
            if (mouseEvent.getClickCount() == 1) {   // single click, show the file tree
                TreeItem<PathNodeItem> currentTreeNode = this.fileTreeView.getSelectionModel().getSelectedItem();
                PathNodeItem currentPathNode = currentTreeNode.getValue();
                if (currentPathNode.isDirectory()) {            // Node is directory
                    currentTreeNode.getChildren().clear();      // reset the children node
                    File [] childrenFiles = currentPathNode.getFile().listFiles();  // get children file in it
                    if (childrenFiles != null) {       // no children file
                        for (File child: childrenFiles) {
                            PathNodeItem childrenPath = new PathNodeItem(child.getAbsolutePath());
                            TreeItem<PathNodeItem> childrenTreeNode = new TreeItem<>(childrenPath);
                            childrenTreeNode.setGraphic(childrenPath.icon);
                            currentTreeNode.getChildren().add(childrenTreeNode);
                        }
                    }
                }
            } else {            // double click, open a new tab to edit the file
                TreeItem<PathNodeItem> currentTreeNode = this.fileTreeView.getSelectionModel().getSelectedItem();
                PathNodeItem currentPathNode = currentTreeNode.getValue();
                if (!currentPathNode.isDirectory()) {                                            // is file node
                    CodeArea cmdCodeArea = new CodeArea();                                       // add code area
                    cmdCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(cmdCodeArea));  // add line number
                    cmdCodeArea.setContextMenu(new CodeEditContextMenu(cmdCodeArea));            // add context menu
                    MinecraftSyntaxHighlighter.applySyntaxHighlighting(cmdCodeArea);             // apply syntax highlighter
                    cmdCodeArea.replaceText(0, 0, readFileToString(currentPathNode.getFile().getAbsolutePath()));  // read file to code area
                    CommandPromptRegister.addPromptTo(cmdCodeArea);                              // add command prompt
                    AnchorPane anchorPane = new AnchorPane(cmdCodeArea);
                    anchorPane.prefWidthProperty().bind(codeTabPane.layoutXProperty());
                    AnchorPane.setBottomAnchor(cmdCodeArea, 0.0);
                    AnchorPane.setTopAnchor(cmdCodeArea, 0.0);
                    AnchorPane.setLeftAnchor(cmdCodeArea, 0.0);
                    AnchorPane.setRightAnchor(cmdCodeArea, 0.0);
                    Tab fileTab = new Tab(currentPathNode.getName(), anchorPane);                // create a new tab
                    fileTab.setUserData(currentPathNode.getFile().getAbsolutePath());
                    fileTab.setOnCloseRequest(this::onCloseFileTab);
                    codeTabPane.getTabs().add(fileTab);
                    codeTabPane.getSelectionModel().select(fileTab);
                }
            }
        }
    }

    /**
     * show/hide the log area
     * @param mouseEvent the mouse event
     */
    public void onMouseClickedButtonHideShow(@NotNull MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (buttonHideShow.getText().equals("Show")) {
                buttonHideShow.setText("Hide");
                bottomVBox.prefHeightProperty().set(160);
            } else {
                buttonHideShow.setText("Show");
                bottomVBox.prefHeightProperty().set(25);
            }
        }
    }

    private @Nullable String readFileToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        long filelength = file.length();
        byte[] filecontent = new byte[(int) filelength];
        FileInputStream in=null;
        try {
            in = new FileInputStream(file);
            in.read(filecontent);
            return new String(filecontent, encoding);
        } catch (Exception e) {
            Alert errorAlert = AlertFactory.createAlert(Alert.AlertType.ERROR,
                    "Minecraft Command Studio",
                    "Error: Failed to read file!",
                    e.getMessage(),
                    ButtonType.OK);
            AlertFactory.addExceptionStackTrace(errorAlert, e);
            errorAlert.showAndWait();
            return null;
        } finally {
            try {
                assert in != null;
                in.close();
            } catch (IOException e) {
                MinecraftCommandStudio.LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * close file tab event
     * ask if save the file, the user can choose "yes", "no", "cancel"
     * the source file is exist
     */
    private void onCloseFileTab(Event event) {
        Tab self = ((Tab) event.getTarget());
        String filePath = self.getUserData().toString();
        Alert saveFileAlert = AlertFactory.createAlert(Alert.AlertType.CONFIRMATION,
                "Minecraft Command Studio",
                "Do you want to save the file?",
                filePath,
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        saveFileAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // first: read the code area from tab > anchor pane > code area
                String codeAreaText = ((CodeArea) ((AnchorPane) self.getContent()).getChildren().get(0)).getText();
                // second: write the code area to file
                try {
                    File file = new File(filePath);
                    file.createNewFile();
                    FileWriter fw = new FileWriter(file);
                    fw.write(codeAreaText);
                    fw.close();
                } catch (IOException e) {
                    MinecraftCommandStudio.LOGGER.error(e.getMessage());
                }
            } else if (response == ButtonType.CANCEL){
                event.consume();  // cancel the close event
            }  // if the user choose "no", do nothing
        });

    }

    /**
     * Release or take back the left tab pane
     * @param mouseEvent the mouse event
     * */
    public void getLeftTabPaneOnClicked(@NotNull MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (leftTabPane.getPrefWidth()>30) {
                leftTabPane.setPrefWidth(30);
            } else {
                leftTabPane.setPrefWidth(300);
            }
        }
    }

    /**
     * Right Tab Pane on Clicked<br/>
     * show/hide/switch tab
     * @param mouseEvent the mouse event
     * */
    public void getRightTabPaneOnClicked(@NotNull MouseEvent mouseEvent) {
        // if the user click the right tab pane at the gap between tabs or the tab pane itself or mouse button is not primary, do nothing
        // only the user click the right 30px width area of the tab, the tab will be expanded or collapsed
        if (!mouseEvent.getButton().equals(MouseButton.PRIMARY) || mouseEvent.getX()<rightTabPane.getWidth()-30) {
            return;
        }
        // get the select tab from rightTabPane
        Tab currentTab = rightTabPane.getSelectionModel().getSelectedItem(); // get the clicked tab from rightTabPan
        if (currentTab!=null) {
            if (!currentTab.getUserData().toString().equals("SHOWN")) {  // switch HIDDEN to SHOWN
                for (Tab tab: rightTabPane.getTabs()) {
                    if (!tab.equals(currentTab)) {            // set other tabs to HIDDEN
                        tab.setUserData("HIDDEN");
                    }
                }
                currentTab.setUserData("SHOWN");              // set current tab state to SHOWN
            } else {                                     // switch HIDDEN to SHOWN
                currentTab.setUserData("HIDDEN");
            }
            if (currentTab.getUserData().toString().equals("SHOWN")) {
                rightTabPane.setPrefWidth(400);
            } else {
                rightTabPane.setPrefWidth(30);
            }
        }
    }

    /**
     * When the user click the "Run" button, execute the minecraft command in the code area or terminate the thread
     * @param event the action event
     * */
    public void getRunButtonOnAction(ActionEvent event) {
        // get the code area from tab > anchor pane > code area
        if (CmdExecutor.executeThread != null && CmdExecutor.executeThread.isAlive()) {   // if the thread is alive, kill it
            CmdExecutor.killThread = true;  // kill the thread
            runButton.setGraphic(new ImageView("assets/mccs/jfx/textures/run.png"));
            return;
        }
        try {                           // if the thread is not alive, execute the command
            outLogCodeArea.clear();     // clear the log area
            CodeArea codeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().get(0);
            CmdExecutor.execute(codeArea, runModeComboBox, runButton);
        } catch (Exception e) {
            MinecraftCommandStudio.LOGGER.error(e.getMessage());
            Alert noScriptAlert = AlertFactory.createAlert(Alert.AlertType.ERROR,
                    "Minecraft Command Studio",
                    "Error: No Script Selected",
                    "The Minecraft command script file is not currently open!",
                    ButtonType.OK);
            outLogCodeArea.appendText(e.getMessage() + "\r\n");
            for (StackTraceElement ele: e.getStackTrace()) {
                outLogCodeArea.appendText("\t" + ele.toString() + "\r\n");
            }
            noScriptAlert.showAndWait();
        }
        event.consume();
    }

    /**
     * Open file menu item event
     * open a file and create a new tab to edit the file when click the "Open" menu item
     * expand the tree node when click the directory node
     * @param event the action event
     * */
    public void getOnOpenMenuItemAction(Event event) { // create a new tab to edit the file
        TreeItem<PathNodeItem> currentTreeNode = this.fileTreeView.getSelectionModel().getSelectedItem();
        PathNodeItem currentPathNode = currentTreeNode.getValue();
        if (currentPathNode.isDirectory()) {                                             // is directory node, unfold it
            if (currentTreeNode.isLeaf()) {                                              // is leaf node, read the directory
                File[] files = currentPathNode.getFile().listFiles();
                if (files != null) {
                    for (File file : files) {
                        PathNodeItem pathNodeItem = new PathNodeItem(file.getAbsolutePath());
                        TreeItem<PathNodeItem> treeNode = new TreeItem<>(pathNodeItem);
                        treeNode.setGraphic(pathNodeItem.icon);                          // set icon
                        currentTreeNode.getChildren().add(treeNode);
                    }
                }
            }
            currentTreeNode.setExpanded(!currentTreeNode.isExpanded());
        } else {                                                                         // is file node
            CodeArea cmdCodeArea = new CodeArea();                                       // add code area
            cmdCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(cmdCodeArea));  // add line number
            cmdCodeArea.setContextMenu(new CodeEditContextMenu(cmdCodeArea));            // add context menu
            MinecraftSyntaxHighlighter.applySyntaxHighlighting(cmdCodeArea);             // apply syntax highlighter
            cmdCodeArea.replaceText(0, 0, readFileToString(currentPathNode.getFile().getAbsolutePath()));  // read file to code area
            CommandPromptRegister.addPromptTo(cmdCodeArea);
            AnchorPane anchorPane = new AnchorPane(cmdCodeArea);
            anchorPane.prefWidthProperty().bind(codeTabPane.layoutXProperty());
            AnchorPane.setBottomAnchor(cmdCodeArea, 0.0);
            AnchorPane.setTopAnchor(cmdCodeArea, 0.0);
            AnchorPane.setLeftAnchor(cmdCodeArea, 0.0);
            AnchorPane.setRightAnchor(cmdCodeArea, 0.0);
            Tab fileTab = new Tab(currentPathNode.getName(), anchorPane);                // create a new tab
            fileTab.setUserData(currentPathNode.getFile().getAbsolutePath());
            fileTab.setOnCloseRequest(this::onCloseFileTab);
            codeTabPane.getTabs().add(fileTab);
            codeTabPane.getSelectionModel().select(fileTab);
            event.consume();
        }
    }

    /**
     * show a dialog to create a new file and create a new tab to edit the file and create a new node in the tree view for the new file
     * @param event the event
     * */
    public void getOnNewFileMenuItemAction(@NotNull ActionEvent event) {
        TextInputDialog newFileDialog = new TextInputDialog();
        // set icon
        Stage newFileDialogStage = (Stage) newFileDialog.getDialogPane().getScene().getWindow();
        newFileDialogStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
        // set UI
        newFileDialog.setTitle("Minecraft Command Studio");
        newFileDialog.setHeaderText("Create a new file");
        newFileDialog.setContentText("Please enter the file name:");
        // set function & show dialog
        newFileDialog.showAndWait().ifPresent(fileName -> {
            TreeItem<PathNodeItem> currentTreeNode = this.fileTreeView.getSelectionModel().getSelectedItem();
            PathNodeItem currentPathNode = currentTreeNode.getValue();
            // if the current node is a directory, create a new file in the directory, else create a new file in the parent directory
            File newFile;
            newFile = currentPathNode.isDirectory() ?
                    new File(currentPathNode.getFile().getAbsolutePath() + File.separator + fileName) :
                    new File(currentPathNode.getFile().getParent() + File.separator + fileName);
            try {
                if (!newFile.createNewFile()) {                                              // file already exist
                    Alert fileExistAlert = AlertFactory.createAlert(Alert.AlertType.ERROR,
                            "Minecraft Command Studio",
                            "Error: File Already Exist",
                            String.format("The file \"%s\" already exist!", newFile.getAbsolutePath()),
                            ButtonType.OK);
                    fileExistAlert.showAndWait();
                    return;
                }
                CodeArea cmdCodeArea = new CodeArea();                                       // add code area
                cmdCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(cmdCodeArea));  // add line number
                cmdCodeArea.setContextMenu(new CodeEditContextMenu(cmdCodeArea));            // add context menu
                MinecraftSyntaxHighlighter.applySyntaxHighlighting(cmdCodeArea);             // apply syntax highlighter
                CommandPromptRegister.addPromptTo(cmdCodeArea);                              // add command prompt
                AnchorPane anchorPane = new AnchorPane(cmdCodeArea);
                anchorPane.prefWidthProperty().bind(codeTabPane.layoutXProperty());
                AnchorPane.setBottomAnchor(cmdCodeArea, 0.0);
                AnchorPane.setTopAnchor(cmdCodeArea, 0.0);
                AnchorPane.setLeftAnchor(cmdCodeArea, 0.0);
                AnchorPane.setRightAnchor(cmdCodeArea, 0.0);
                Tab fileTab = new Tab(fileName, anchorPane);                // create a new tab
                fileTab.setUserData(newFile.getAbsolutePath());
                fileTab.setOnCloseRequest(this::onCloseFileTab);
                codeTabPane.getTabs().add(fileTab);
                codeTabPane.getSelectionModel().select(fileTab);
                // create a new node in the tree view for the new file
                PathNodeItem newFileNodeItem = new PathNodeItem(newFile.getAbsolutePath());
                TreeItem<PathNodeItem> newFileNode = new TreeItem<>(newFileNodeItem);
                newFileNode.setGraphic(newFileNodeItem.icon);              // set icon
                if (currentPathNode.isDirectory()) {         // new file is in current directory
                    currentTreeNode.getChildren().add(newFileNode);
                } else {       // new file is in the same directory to the current file
                    currentTreeNode.getParent().getChildren().add(newFileNode);
                }
            } catch (IOException e) {
                MinecraftCommandStudio.LOGGER.error(e.getMessage());
            }
        });
        event.consume();
    }

    /**
     * show a dialog to create a new directory and create a new node in the tree view for the new directory
     * @param event the event
     * */
    public void getOnNewDirectoryItemAction(@NotNull ActionEvent event) {
        TextInputDialog newDirectoryDialog = new TextInputDialog();
        // set icon
        Stage newDirectoryDialogStage = (Stage) newDirectoryDialog.getDialogPane().getScene().getWindow();
        newDirectoryDialogStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
        // set UI
        newDirectoryDialog.setTitle("Minecraft Command Studio");
        newDirectoryDialog.setHeaderText("Create a new directory");
        newDirectoryDialog.setContentText("Please enter the directory name:");
        // set function & show dialog
        newDirectoryDialog.showAndWait().ifPresent(directoryName -> {
            TreeItem<PathNodeItem> currentTreeNode = this.fileTreeView.getSelectionModel().getSelectedItem();
            PathNodeItem currentPathNode = currentTreeNode.getValue();
            // if the current node is a directory, create a new directory in the directory, else create a new directory in the parent directory
            File newDirectory;
            newDirectory = currentPathNode.isDirectory() ?
                    new File(currentPathNode.getFile().getAbsolutePath() + File.separator + directoryName) :
                    new File(currentPathNode.getFile().getParent() + File.separator + directoryName);
            if (!newDirectory.mkdir()) {                                              // directory already exist
                Alert directoryFailAlert = AlertFactory.createAlert(Alert.AlertType.ERROR,
                        "Minecraft Command Studio",
                        "Error: Failed to Create Directory",
                        String.format("The directory \"%s\" already exist or the name \"%s\" is invalid!", newDirectory.getAbsolutePath(), directoryName),
                        ButtonType.OK);
                directoryFailAlert.showAndWait();
                return;                 // return to the caller
            }
            // create a new node in the tree view for the new directory
            PathNodeItem newDirectoryNodeItem = new PathNodeItem(newDirectory.getAbsolutePath());
            TreeItem<PathNodeItem> newDirectoryNode = new TreeItem<>(newDirectoryNodeItem);
            newDirectoryNode.setGraphic(newDirectoryNodeItem.icon);              // set icon
            if (currentPathNode.isDirectory()) {         // new directory is in current directory
                currentTreeNode.getChildren().add(newDirectoryNode);
            } else {                                     // new directory is in the same directory to the current file
                currentTreeNode.getParent().getChildren().add(newDirectoryNode);
            }
        });
        event.consume();
    }


    /**
     * open the file in the explorer
     * @param event the event
     * */
    public void getOpenInExplorerMenuItemAction(ActionEvent event) {
        TreeItem<PathNodeItem> currentTreeNode = this.fileTreeView.getSelectionModel().getSelectedItem();  // get the selected node
        PathNodeItem currentPathNode = currentTreeNode.getValue();      // get the selected node's value
        if (currentPathNode.isDirectory()) {    // if the current node is a directory, open the directory, else open the parent directory
            try {
                Desktop.getDesktop().open(currentPathNode.getFile());
            } catch (IOException e) {
                MinecraftCommandStudio.LOGGER.error(e.getMessage());
            }
        } else {                                // if the current node is a file, open the parent directory
            try {
                Desktop.getDesktop().open(currentPathNode.getFile().getParentFile());
            } catch (IOException e) {
                MinecraftCommandStudio.LOGGER.error(e.getMessage());
            }
        }
        event.consume();
    }

    /**
     * delete the file or directory
     * make sure the user want to delete the file or directory by showing a dialog first
     * if the user click "OK", delete the file or directory
     * after deleting the file or directory, remove the node from the tree view
     * @param event the event
     * */
    public void getOnDeleteMenuItemAction(@NotNull ActionEvent event) {
        TreeItem<PathNodeItem> currentTreeNode = this.fileTreeView.getSelectionModel().getSelectedItem();  // get the selected node
        PathNodeItem currentPathNode = currentTreeNode.getValue();      // get the selected node's value
        Alert deleteAlert = AlertFactory.createAlert(Alert.AlertType.CONFIRMATION,
                "Minecraft Command Studio",
                "Ready to Delete?",
                String.format("Are you sure you want to delete \"%s\"?", currentPathNode.getFile().getAbsolutePath()),
                ButtonType.OK, ButtonType.CANCEL);
        // set function & show dialog
        deleteAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (currentPathNode.isDirectory()) {    // if the current node is a directory, delete the directory, else delete the file
                    try {
                        FileUtils.deleteDirectory(currentPathNode.getFile());
                    } catch (IOException e) {
                        // if failed to delete the directory (For example, the file is in use by another process), show a dialog and write the error message to the outLogCodeArea
                        Alert deleteFailedAlert = AlertFactory.createAlert(Alert.AlertType.ERROR,
                                "Minecraft Command Studio",
                                "Error: Delete Failed",
                                String.format("Failed to delete \"%s\"!", currentPathNode.getFile().getAbsolutePath()),
                                ButtonType.OK);
                        AlertFactory.addExceptionStackTrace(deleteFailedAlert, e);
                        deleteFailedAlert.showAndWait();
                        // log the error message and stack trace to the outLogCodeArea
                        outLogCodeArea.appendText(e.getMessage() + "\r\n");
                        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                            outLogCodeArea.appendText("\t" + stackTraceElement.toString() + "\r\n");
                        }
                        return;                 // return to the caller
                    }
                } else {                                // if the current node is a file, delete the file
                    currentPathNode.getFile().delete();
                }
                currentTreeNode.getParent().getChildren().remove(currentTreeNode);  // remove the node from the tree view
            }
        });
        event.consume();
    }

    /**
     * Rename the file and update the tree view
     * @param event the event
     * */
    public void getOnRenameMenuItemAction(@NotNull ActionEvent event) {
        TreeItem<PathNodeItem> currentTreeNode = this.fileTreeView.getSelectionModel().getSelectedItem();  // get the selected node
        PathNodeItem currentPathNode = currentTreeNode.getValue();      // get the selected node's value
        TextInputDialog renameDialog = new TextInputDialog(currentPathNode.getFile().getName());
        // set icon
        Stage renameDialogStage = (Stage) renameDialog.getDialogPane().getScene().getWindow();
        renameDialogStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
        // set UI
        renameDialog.setTitle("Minecraft Command Studio");
        renameDialog.setHeaderText("Rename");
        renameDialog.setContentText("Please enter the new name:");
        // set function & show dialog
        renameDialog.showAndWait().ifPresent(newName -> {
            File newFile = new File(currentPathNode.getFile().getParent() + File.separator + newName);
            if (currentPathNode.getFile().renameTo(newFile)) {     // rename the file
                currentPathNode.setFile(newFile);                  // rename the tree node
                currentTreeNode.setValue(currentPathNode);
                currentTreeNode.setGraphic(currentPathNode.icon);  // set icon
                // re-expand the parent node
                currentTreeNode.getParent().setExpanded(false);
                currentTreeNode.getParent().setExpanded(true);
            } else {                                              // if failed to rename the file, show a dialog
                Alert renameFailedAlert = AlertFactory.createAlert(Alert.AlertType.ERROR,
                        "Minecraft Command Studio",
                        "Error: Rename Failed",
                        String.format("Failed to rename \"%s\"!", currentPathNode.getFile().getAbsolutePath()),
                        ButtonType.OK);
                renameFailedAlert.showAndWait();
            }
        });
        event.consume();
    }

    /**
     * copy the file or directory
     * show a dialog to let the user choose the destination directory
     * @param event the event
     * */
    public void getOnCopyToMenuItemAction(ActionEvent event) {
        TreeItem<PathNodeItem> currentTreeNode = this.fileTreeView.getSelectionModel().getSelectedItem();  // get the selected node
        PathNodeItem currentPathNode = currentTreeNode.getValue();      // get the selected node's value
        DirectoryChooser copyToDirectoryChooser = new DirectoryChooser();
        // set UI
        copyToDirectoryChooser.setTitle("Minecraft Command Studio");
        copyToDirectoryChooser.setInitialDirectory(new File(MinecraftCommandStudio.RUNTIME_PATH.toString()));
        // set function & show dialog
        File destinationDirectory = copyToDirectoryChooser.showDialog(this.fileTreeView.getScene().getWindow());  // get the destination directory
        if (destinationDirectory != null && destinationDirectory.isDirectory()) {    // if the destination directory is valid
            if (currentPathNode.isDirectory()) {    // if the current node is a directory, copy the directory, else copy the file
                try {
                    FileUtils.copyDirectory(currentPathNode.getFile(), new File(destinationDirectory.getPath() + File.separator + currentPathNode.getFile().getName()));
                } catch (IOException e) {
                    // if failed to copy the directory (For example, the file is in use by another process), show a dialog and write the error message to the outLogCodeArea
                    Alert copyFailedAlert = new Alert(Alert.AlertType.ERROR);
                    // set icon
                    Stage copyFailedStage = (Stage) copyFailedAlert.getDialogPane().getScene().getWindow();
                    copyFailedStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
                    // set UI
                    copyFailedAlert.setTitle("Minecraft Command Studio");
                    copyFailedAlert.setHeaderText("Error: Copy Failed");
                    copyFailedAlert.setContentText(String.format("Failed to copy \"%s\"!\r\n" + e.getMessage(), currentPathNode.getFile().getName()));
                    copyFailedAlert.showAndWait();
                    // log the error message and stack trace to the outLogCodeArea
                    outLogCodeArea.appendText(e.getMessage() + "\r\n");
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        outLogCodeArea.appendText("\t" + stackTraceElement.toString() + "\r\n");
                    }
                    return;                 // return to the caller
                }
            } else {                                // if the current node is a file, copy the file
                try {
                    FileUtils.copyFile(currentPathNode.getFile(), new File(destinationDirectory.getPath() + File.separator + currentPathNode.getFile().getName()));
                } catch (IOException e) {
                    // if failed to copy the file (For example, the file is in use by another process), show a dialog and write the error message to the outLogCodeArea
                    Alert copyFailedAlert = new Alert(Alert.AlertType.ERROR);
                    // set icon
                    Stage copyFailedStage = (Stage) copyFailedAlert.getDialogPane().getScene().getWindow();
                    copyFailedStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
                    // set UI
                    copyFailedAlert.setTitle("Minecraft Command Studio");
                    copyFailedAlert.setHeaderText("Error: Copy Failed");
                    copyFailedAlert.setContentText(String.format("Failed to copy \"%s\"!\r\n" + e.getMessage(), currentPathNode.getFile().getName()));
                    copyFailedAlert.showAndWait();
                    // log the error message and stack trace to the outLogCodeArea
                    outLogCodeArea.appendText(e.getMessage() + "\r\n");
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        outLogCodeArea.appendText("\t" + stackTraceElement.toString() + "\r\n");
                    }
                    return;                 // return to the caller
                }
            }
            // show a dialog to tell the user the copy is successful, button type: OK
            Alert copySuccessAlert = new Alert(Alert.AlertType.INFORMATION);
            // set icon
            Stage copySuccessStage = (Stage) copySuccessAlert.getDialogPane().getScene().getWindow();
            copySuccessStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
            // set UI
            copySuccessAlert.setTitle("Minecraft Command Studio");
            copySuccessAlert.setHeaderText("Copy Success");
            copySuccessAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // enable long text wrapping
            copySuccessAlert.setContentText(String.format("Successfully copied \"%s\" to \"%s\"!", currentPathNode.getFile().getName(), destinationDirectory.getPath()));
            copySuccessAlert.showAndWait();
        }
        event.consume();
    }

    /**
     * move the file or directory <br>
     * show a dialog to let the user choose the destination directory
     * if the destination directory is the same as the source directory, do nothing;
     * if the destination directory is a subdirectory of the source directory, show a dialog and do nothing;
     * if the destination directory is a parent directory of the source directory, show a dialog and do nothing; <br>
     * else move the file or directory with following steps: <br>
     * 1. move the file or directory to the destination directory; <br>
     * 2. refresh the parent node of the source directory or file; <br>
     * @param event the event
     * */
    public void getOnMoveToMenuItemAction(ActionEvent event) {
        TreeItem<PathNodeItem> currentTreeNode = this.fileTreeView.getSelectionModel().getSelectedItem();  // get the selected node
        PathNodeItem currentPathNode = currentTreeNode.getValue();      // get the selected node's value
        DirectoryChooser moveToDirectoryChooser = new DirectoryChooser();
        // set UI
        moveToDirectoryChooser.setTitle("Minecraft Command Studio");
        moveToDirectoryChooser.setInitialDirectory(new File(MinecraftCommandStudio.RUNTIME_PATH.toString()));
        // set function & show dialog
        File destinationDirectory = moveToDirectoryChooser.showDialog(this.fileTreeView.getScene().getWindow());  // get the destination directory
        if (destinationDirectory != null && destinationDirectory.isDirectory()) {    // if the destination directory is valid
            if (currentPathNode.getFile().getPath().equals(destinationDirectory.getPath())) {  // if the destination directory is the same as the source directory, do nothing
                return;
            } else if (destinationDirectory.getPath().contains(currentPathNode.getFile().getPath())) {  // if the destination directory is a subdirectory of the source directory, show a dialog and do nothing
                Alert moveToFailedAlert = new Alert(Alert.AlertType.ERROR);
                // set icon
                Stage moveToFailedStage = (Stage) moveToFailedAlert.getDialogPane().getScene().getWindow();
                moveToFailedStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
                // set UI
                moveToFailedAlert.setTitle("Minecraft Command Studio");
                moveToFailedAlert.setHeaderText("Error: Move Failed");
                moveToFailedAlert.setContentText(String.format("Failed to move \"%s\"!\rThe destination directory is a subdirectory of the source directory!", currentPathNode.getFile().getName()));
                moveToFailedAlert.showAndWait();
                return;
            } else if (currentPathNode.getFile().getPath().contains(destinationDirectory.getPath())) {  // if the destination directory is a parent directory of the source directory, show a dialog and do nothing
                Alert moveToFailedAlert = new Alert(Alert.AlertType.ERROR);
                // set icon
                Stage moveToFailedStage = (Stage) moveToFailedAlert.getDialogPane().getScene().getWindow();
                moveToFailedStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
                // set UI
                moveToFailedAlert.setTitle("Minecraft Command Studio");
                moveToFailedAlert.setHeaderText("Error: Move Failed");
                moveToFailedAlert.setContentText(String.format("Failed to move \"%s\"!\rThe destination directory is a parent directory of the source directory!", currentPathNode.getFile().getName()));
                moveToFailedAlert.showAndWait();
                return;
            } else {    // else move the file or directory with following steps:
                // 1. move the file or directory to the destination directory;
                try {
                    if (currentPathNode.getFile().isDirectory()) {   // if the file is a directory, move the directory
                        FileUtils.moveDirectoryToDirectory(currentPathNode.getFile(), destinationDirectory, true);
                    } else {                                         // else move the file
                        FileUtils.moveFileToDirectory(currentPathNode.getFile(), destinationDirectory, true);
                    }
                } catch (IOException e) {
                    // if failed to move the file or directory (For example, the file is in use by another process), show a dialog and write the error message to the outLogCodeArea
                    Alert moveToFailedAlert = new Alert(Alert.AlertType.ERROR);
                    // set icon
                    Stage moveToFailedStage = (Stage) moveToFailedAlert.getDialogPane().getScene().getWindow();
                    moveToFailedStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
                    // set UI
                    moveToFailedAlert.setTitle("Minecraft Command Studio");
                    moveToFailedAlert.setHeaderText("Error: Move Failed");
                    moveToFailedAlert.setContentText(String.format("Failed to move \"%s\"!\r%s", currentPathNode.getFile().getName(), e.getMessage()));
                    moveToFailedAlert.showAndWait();
                    // log the error message and stack trace to the outLogCodeArea
                    outLogCodeArea.appendText(e.getMessage() + "\r\n");
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        outLogCodeArea.appendText("\t" + stackTraceElement.toString() + "\r\n");
                    }
                    return;                 // return to the caller
                }
                // 2. refresh the parent node of the source directory or file;
                currentTreeNode.getParent().getChildren().remove(currentTreeNode);  // remove the node from the tree view
                currentTreeNode.getParent().setExpanded(false);  // collapse the parent node
                currentTreeNode.getParent().setExpanded(true);   // re-expand the parent node
            }
        }
        event.consume();
    }

    /**
     * when the user clicks the "New File" menu item, create a new file and add it to the tabpane
     * create a new codearea and add it to the tabpane, as a cache for the new file
     * @param event the event
     * */
    public void getOnMainNewFileAction(@NotNull ActionEvent event) {
        // create a new CodeArea
        CodeArea cmdCodeArea = new CodeArea();                                        // create a new codearea
        cmdCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(cmdCodeArea));   // set the codearea's paragraph graphic factory
        cmdCodeArea.setContextMenu(new CodeEditContextMenu(cmdCodeArea));                                // set the codearea's context menu
        MinecraftSyntaxHighlighter.applySyntaxHighlighting(cmdCodeArea);              // apply syntax highlighting to the codearea
        CommandPromptRegister.addPromptTo(cmdCodeArea);                               // add command prompt to the codearea
        // create a new AnchorPane
        AnchorPane newAnchorPane = new AnchorPane(cmdCodeArea);                       // create a new anchorpane
        newAnchorPane.prefWidthProperty().bind(codeTabPane.layoutXProperty());
        AnchorPane.setBottomAnchor(cmdCodeArea, 0.0);
        AnchorPane.setTopAnchor(cmdCodeArea, 0.0);
        AnchorPane.setLeftAnchor(cmdCodeArea, 0.0);
        AnchorPane.setRightAnchor(cmdCodeArea, 0.0);
        // create a new tab
        Tab newTab = new Tab();
        newTab.setText("New Script *");                                               // set the tab's text
        newTab.setUserData("New Script *");                                           // set the tab's user data
        newTab.setClosable(true);                                                     // set the tab's closable property
        newTab.setContent(newAnchorPane);                                             // set the tab's content
        newTab.setOnCloseRequest(event1 -> {                                          // set the tab's on close request event handler (when the user clicks the "X" button)
            // show a dialog to ask the user whether to save the file
            Alert saveFileAlert = new Alert(Alert.AlertType.CONFIRMATION);
            // set icon
            Stage saveFileStage = (Stage) saveFileAlert.getDialogPane().getScene().getWindow();
            saveFileStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
            // set UI
            saveFileAlert.setTitle("Minecraft Command Studio");
            saveFileAlert.setHeaderText("Save File");
            saveFileAlert.setContentText("Do you want to save the file?");
            saveFileAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            Optional<ButtonType> result = saveFileAlert.showAndWait();
            if (result.get() == ButtonType.YES) {  // if the user clicks the "Yes" button, save the file
                // show a file chooser to let the user choose the file to save
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save File As");
                fileChooser.setInitialDirectory(new File(MinecraftCommandStudio.RUNTIME_PATH.toString()));
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Minecraft Function", "*.mcfunction"),
                        new FileChooser.ExtensionFilter("Text File", "*.txt"),
                        new FileChooser.ExtensionFilter("All Files", "*.*"));
                File file = fileChooser.showSaveDialog(MccsApplication.stage);
                // if the user chooses a file, check whether the path is valid, and save as the file
                if (file != null && file.getParentFile().isDirectory()) {
                    // create an empty file and save the codearea's text to the file
                    try {
                        file.createNewFile();
                        FileUtils.writeStringToFile(file, cmdCodeArea.getText(), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        // if failed to save the file, show a dialog and write the error message to the outLogCodeArea
                        Alert saveFileFailedAlert = new Alert(Alert.AlertType.ERROR);
                        // set icon
                        Stage saveFileFailedStage = (Stage) saveFileFailedAlert.getDialogPane().getScene().getWindow();
                        saveFileFailedStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
                        // set UI
                        saveFileFailedAlert.setTitle("Minecraft Command Studio");
                        saveFileFailedAlert.setHeaderText("Error: Save File Failed");
                        saveFileFailedAlert.setContentText(String.format("Failed to save the file \"%s\"!\r%s", file.getName(), e.getMessage()));
                        // log the error message and stack trace to the alert
                        Label errorLabel = new Label();                                                           // create a new error label to display the error message and stack trace
                        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                            errorLabel.setText(errorLabel.getText() + stackTraceElement.toString() + "\r\n");     // append the stack trace to the error label
                        }
                        errorLabel.setText(errorLabel.getText() + e.getMessage() + "\r\n");
                        ScrollPane scrollPane = new ScrollPane(errorLabel);                                       // create a new scrollpane to wrap the error label
                        scrollPane.setFitToWidth(true);
                        scrollPane.setMaxHeight(300);
                        saveFileFailedAlert.getDialogPane().setExpandableContent(scrollPane);                     // set the alert's expandable content
                        saveFileFailedAlert.showAndWait();
                    }
                }
            }
        });
        codeTabPane.getTabs().add(newTab);                                            // add a new tab to the tabpane
        codeTabPane.getSelectionModel().select(newTab);                               // select the new tab
        event.consume();                                                              // consume the event
    }

    /**
     * when the user clicks the "New Directory" menu item, show a dialog to choose the path to create the new directory
     * @param event the action event
     * */
    public void getOnMainNewDirAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Create a New Directory: Choose the path to create the new directory into");
        directoryChooser.setInitialDirectory(new File(MinecraftCommandStudio.RUNTIME_PATH.toString()));
        // show the directory chooser
        File saveDir = directoryChooser.showDialog(MccsApplication.stage);
        // if the user chooses a directory, check whether the path is valid, then show a dialog to let the user input the directory name
        if (saveDir != null && saveDir.isDirectory()) {
            TextInputDialog textInputDialog = new TextInputDialog();
            // set icon
            Stage textInputStage = (Stage) textInputDialog.getDialogPane().getScene().getWindow();
            textInputStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
            // set UI, button {Yes, Cancel}
            textInputDialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            textInputDialog.setTitle("Minecraft Command Studio");
            textInputDialog.setHeaderText("Create a New Directory");
            textInputDialog.setContentText("Please input the directory name:");
            // show the dialog
            Optional<String> result = textInputDialog.showAndWait();
            // if the user clicks the "Yes" button, create the directory
            if (result.isPresent()) {
                // create the directory
                String dirName = result.get();
                // check whether the directory name is valid, it cannot be empty or contain any of the following characters: \ / : * ? " < > |
                if (dirName.isEmpty() || dirName.contains("\\") || dirName.contains("/") || dirName.contains(":") || dirName.contains("*") || dirName.contains("?") || dirName.contains("\"") || dirName.contains("<") || dirName.contains(">") || dirName.contains("|")) {
                    // if the directory name is invalid, show a dialog
                    Alert invalidDirNameAlert = new Alert(Alert.AlertType.ERROR);
                    // set icon
                    Stage invalidDirNameStage = (Stage) invalidDirNameAlert.getDialogPane().getScene().getWindow();
                    invalidDirNameStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
                    // set UI
                    invalidDirNameAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);  // auto height
                    invalidDirNameAlert.setTitle("Minecraft Command Studio");
                    invalidDirNameAlert.setHeaderText("Error: Invalid Directory Name");
                    invalidDirNameAlert.setContentText("The directory name cannot be empty or contain any of the following characters: \\ / : * ? \" < > |");
                    invalidDirNameAlert.showAndWait();
                    return;
                }  // if the directory name is valid, create the directory
                File newDir = new File(saveDir, dirName);
                if (!newDir.exists()) {
                    newDir.mkdirs();
                }
            }
        }
        event.consume();  // consume the event
    }

    /**
     * when the user clicks the "Open" menu item, show a file chooser to let the user choose the file to open
     * if the user chooses a file, check whether the path is valid, then open the file in a new tab
     * else if the user cancels the file chooser, do nothing
     * @param event the action event
     * */
    public void getOnMainOpenAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(new File(MinecraftCommandStudio.RUNTIME_PATH.toString()));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Minecraft Function", "*.mcfunction"),
                new FileChooser.ExtensionFilter("Text File", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File openFile = fileChooser.showOpenDialog(MccsApplication.stage);
        // if the user chooses a file, check whether the path is valid, then open the file in a new tab
        if (openFile != null && openFile.exists()) {
            // create a new tab
            Tab newTab = new Tab(openFile.getName());
            newTab.setClosable(true);
            newTab.setUserData(openFile.getAbsolutePath());  // set the tab's user data to the file's absolute path
            // create a new code area
            CodeArea cmdCodeArea = new CodeArea();
            cmdCodeArea.setParagraphGraphicFactory(LineNumberFactory.get(cmdCodeArea));  // set the code to show line numbers
            MinecraftSyntaxHighlighter.applySyntaxHighlighting(cmdCodeArea);             // set the code area's syntax highlighting
            cmdCodeArea.setContextMenu(new CodeEditContextMenu(cmdCodeArea));            // set the code area's context menu
            try {  // set the code area's text
                cmdCodeArea.replaceText(FileUtils.readFileToString(openFile, StandardCharsets.UTF_8));
            } catch (IOException e) {
                // if failed to open the file, show a dialog and write the error message to the outLogCodeArea
                Alert openFileFailedAlert = new Alert(Alert.AlertType.ERROR);
                // set icon
                Stage openFileFailedStage = (Stage) openFileFailedAlert.getDialogPane().getScene().getWindow();
                openFileFailedStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
                // set UI
                openFileFailedAlert.setTitle("Minecraft Command Studio");
                openFileFailedAlert.setHeaderText("Error: Open File Failed");
                openFileFailedAlert.setContentText(String.format("Failed to open the file \"%s\"!\r%s", openFile.getName(), e.getMessage()));
                // log the error message and stack trace to the alert
                Label errorLabel = new Label();                                                           // create a new error label to display the error message and stack trace
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    errorLabel.setText(errorLabel.getText() + stackTraceElement.toString() + "\r\r");    // add the stack trace to the error label
                }
                ScrollPane scrollPane = new ScrollPane(errorLabel);                                        // create a new scroll pane to display the error label
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
                openFileFailedAlert.getDialogPane().setExpandableContent(scrollPane);                      // add the scroll pane to the alert
                openFileFailedAlert.showAndWait();
                // log the error message and stack trace to the outLogCodeArea
                outLogCodeArea.appendText(String.format("Failed to open the file \"%s\"!\r%s\r\r", openFile.getName(), e.getMessage()));
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    outLogCodeArea.appendText("\t" + stackTraceElement.toString() + "\r\r");
                }
                return;
            }
            CommandPromptRegister.addPromptTo(cmdCodeArea);           // add the command prompt to the code area
            AnchorPane anchorPane = new AnchorPane(cmdCodeArea);
            AnchorPane.setTopAnchor(cmdCodeArea, 0.0);
            AnchorPane.setBottomAnchor(cmdCodeArea, 0.0);
            AnchorPane.setLeftAnchor(cmdCodeArea, 0.0);
            AnchorPane.setRightAnchor(cmdCodeArea, 0.0);
            newTab.setContent(anchorPane);
            newTab.setOnCloseRequest(event1 -> { // show an alert to ask the user whether to save the file
                Alert saveAlert = AlertFactory.createAlert(Alert.AlertType.CONFIRMATION,
                        "Minecraft Command Studio",
                        "Save File",
                        String.format("Do you want to save the changes you made to \"%s\"?", openFile.getName()),
                        ButtonType.OK, ButtonType.NO, ButtonType.CANCEL);
                Optional<ButtonType> result = saveAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // if the user clicks the "OK" button, save the file
                    try {
                        FileUtils.writeStringToFile(openFile, cmdCodeArea.getText(), StandardCharsets.UTF_8);
                    } catch (IOException e) { // if failed to save the file, show a dialog and write the error message to the outLogCodeArea
                        Alert saveFileFailedAlert = AlertFactory.createAlert(Alert.AlertType.ERROR,
                                "Minecraft Command Studio",
                                "Error: Save File Failed",
                                String.format("Failed to save the file \"%s\"!\r%s", openFile.getName(), e.getMessage()),
                                ButtonType.OK);
                        // log the error message and stack trace to the alert
                        AlertFactory.addExceptionStackTrace(saveFileFailedAlert, e);
                        saveFileFailedAlert.showAndWait();
                        // log the error message and stack trace to the outLogCodeArea
                        outLogCodeArea.appendText(String.format("Failed to save the file \"%s\"!\r%s\r\r", openFile.getName(), e.getMessage()));
                        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                            outLogCodeArea.appendText("\t" + stackTraceElement.toString() + "\r\r");
                        }
                    }
                } else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                    // if the user clicks the "Cancel" button, cancel the close request
                    event1.consume();
                }   // if the user clicks the "No" button, do nothing and the tab will be closed directly
            });
            codeTabPane.getTabs().add(newTab);                 // add the tab to the tab pane
            codeTabPane.getSelectionModel().select(newTab);   // select the new tab
        }
        event.consume();
    }

    /**
     * save the current tab's code to the file
     * @param event the event
     * */
    public void getOnMainSaveAction(ActionEvent event) {
        // check if the current tab has code area
        Tab currentTab = codeTabPane.getSelectionModel().getSelectedItem();
        CodeArea cmdCodeArea;
        try {
            cmdCodeArea = (CodeArea) ((AnchorPane) currentTab.getContent()).getChildren().get(0);
        } catch (ClassCastException e) {
            Alert noCodeAreaAlert = AlertFactory.createAlert(Alert.AlertType.ERROR,
                    "Minecraft Command Studio",
                    "Error: No Code Area",
                    "There is no code area in the current tab!",
                    ButtonType.OK);
            noCodeAreaAlert.showAndWait();
            return;
        }
        if (cmdCodeArea != null) {  // check if the currentTab has target file
            if (new File(currentTab.getUserData().toString()).exists()) {
                // if the currentTab has target file, save the file
                try {
                    FileUtils.writeStringToFile(new File(currentTab.getUserData().toString()), cmdCodeArea.getText(), StandardCharsets.UTF_8);
                } catch (IOException e) { // if failed to save the file, show a dialog and write the error message to the outLogCodeArea
                    Alert saveFileFailedAlert = new Alert(Alert.AlertType.ERROR);
                    // set icon
                    Stage saveFileFailedStage = (Stage) saveFileFailedAlert.getDialogPane().getScene().getWindow();
                    saveFileFailedStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
                    // set UI
                    saveFileFailedAlert.setTitle("Minecraft Command Studio");
                    saveFileFailedAlert.setHeaderText("Error: Save File Failed");
                    saveFileFailedAlert.setContentText(String.format("Failed to save the file \"%s\"!\r%s", new File(currentTab.getUserData().toString()).getName(), e.getMessage()));
                    // log the error message and stack trace to the alert
                    Label errorLabel = new Label();                                                           // create a new error label to display the error message and stack trace
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        errorLabel.setText(errorLabel.getText() + stackTraceElement.toString() + "\r\r");    // add the stack trace to the error label
                    }
                    ScrollPane scrollPane = new ScrollPane(errorLabel);                                        // create a new scroll pane to display the error label
                    scrollPane.setFitToWidth(true);
                    scrollPane.setFitToHeight(true);
                    saveFileFailedAlert.getDialogPane().setExpandableContent(scrollPane);                      // add the scroll pane to the alert
                    saveFileFailedAlert.showAndWait();
                    // log the error message and stack trace to the outLogCodeArea
                    outLogCodeArea.appendText(String.format("Failed to save the file \"%s\"!\r%s\r\r", new File(currentTab.getUserData().toString()).getName(), e.getMessage()));
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        outLogCodeArea.appendText("\t" + stackTraceElement.toString() + "\r\r");
                    }
                }
            } else {  // if the currentTab has no target file, show a file chooser to choose the save file
                getOnMainSaveAsAction(event);
            }
        }
    }

    /**
     * save the current tab's code as a new file
     * @param event the event
     * */
    public void getOnMainSaveAsAction(ActionEvent event) {
        // check if the current tab has code area
        AnchorPane currentTabContent = (AnchorPane) codeTabPane.getSelectionModel().getSelectedItem().getContent();
        CodeArea cmdCodeArea;
        try {
            cmdCodeArea = (CodeArea) currentTabContent.getChildren().get(0);
        } catch (ClassCastException e) {
            Alert noCodeAreaAlert = AlertFactory.createAlert(Alert.AlertType.ERROR,
                    "Minecraft Command Studio", "Error: No Code Area", "There is no code area in the current tab!");
            AlertFactory.addExceptionStackTrace(noCodeAreaAlert, e);
            noCodeAreaAlert.showAndWait();
            return;
        }
        if (cmdCodeArea != null) {   // show a file chooser to choose the save file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Minecraft Function", "*.mcfunction"),
                    new FileChooser.ExtensionFilter("Text File", "*.txt"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File saveFile = fileChooser.showSaveDialog(MccsApplication.stage);
            if (saveFile != null) {
                try {
                    FileUtils.writeStringToFile(saveFile, cmdCodeArea.getText(), StandardCharsets.UTF_8);
                } catch (IOException e) { // if failed to save the file, show a dialog and write the error message to the outLogCodeArea
                    Alert saveFileFailedAlert = new Alert(Alert.AlertType.ERROR);
                    // set icon
                    Stage saveFileFailedStage = (Stage) saveFileFailedAlert.getDialogPane().getScene().getWindow();
                    saveFileFailedStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
                    // set UI
                    saveFileFailedAlert.setTitle("Minecraft Command Studio");
                    saveFileFailedAlert.setHeaderText("Error: Save File Failed");
                    saveFileFailedAlert.setContentText(String.format("Failed to save the file \"%s\"!\r%s", saveFile.getName(), e.getMessage()));
                    // log the error message and stack trace to the alert
                    Label errorLabel = new Label();                                                           // create a new error label to display the error message and stack trace
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        errorLabel.setText(errorLabel.getText() + stackTraceElement.toString() + "\r\r");    // add the stack trace to the error label
                    }
                    ScrollPane scrollPane = new ScrollPane(errorLabel);                                        // create a new scroll pane to display the error label
                    scrollPane.setFitToWidth(true);
                    scrollPane.setFitToHeight(true);
                    saveFileFailedAlert.getDialogPane().setExpandableContent(scrollPane);                      // add the scroll pane to the alert
                    saveFileFailedAlert.showAndWait();
                    // log the error message and stack trace to the outLogCodeArea
                    outLogCodeArea.appendText(String.format("Failed to save the file \"%s\"!\r%s\r\r", saveFile.getName(), e.getMessage()));
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        outLogCodeArea.appendText("\t" + stackTraceElement.toString() + "\r\r");
                    }
                }
            }
        }
        event.consume();
    }

    /**
     * close the current tab
     * @param event the event
     * */
    public void getOnMainCloseAction(ActionEvent event) {
        // check if the current tab has code area
        try {
            CodeArea cmdCodeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel().getSelectedItem().getContent()).getChildren().get(0);
            assert cmdCodeArea != null;
        } catch (Exception e) {
            Alert noCodeAreaAlert = AlertFactory.createAlert(Alert.AlertType.ERROR,
                    "Minecraft Command Studio", "Error: No Code Area", "There is no code area in the current tab!");
            AlertFactory.addExceptionStackTrace(noCodeAreaAlert, e);
            noCodeAreaAlert.showAndWait();
            return;
        }
        // show an alert to ask the user whether to save the file
        Alert saveFileAlert = AlertFactory.createAlert(Alert.AlertType.CONFIRMATION,
                "Minecraft Command Studio", "Save File", "Do you want to save the file?",
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        Optional<ButtonType> saveFileResult = saveFileAlert.showAndWait();
        if (saveFileResult.isPresent()) {
            if (saveFileResult.get() == ButtonType.YES) {  // if the user choose to save the file, call the save action and close the tab
                getOnMainSaveAction(event);
                codeTabPane.getTabs().remove(codeTabPane.getSelectionModel().getSelectedItem());
            } else if (saveFileResult.get() == ButtonType.NO) {  // if the user choose not to save the file, close the tab
                codeTabPane.getTabs().remove(codeTabPane.getSelectionModel().getSelectedItem());
            }  // if the user choose to cancel, do nothing
        }
    }

    /**
     * exit
     * @param event the event
     * */
    public void getOnMainExitAction(ActionEvent event) {
        if (MccsApplication.stage.isShowing()) {
            MccsApplication.stage.hide();
        }
        event.consume();
    }

    /**
     * select all
     * @param event the event
     * */
    public void editSelectAll(ActionEvent event) {
        CodeArea cmdCodeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel()
                .getSelectedItem().getContent()).getChildren().get(0);
        if (cmdCodeArea != null) {
            cmdCodeArea.selectAll();
        }
        event.consume();
    }

    /**
     * copy selected text
     * @param event the event
     * */
    public void editCopy(ActionEvent event) {
        CodeArea cmdCodeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel()
                .getSelectedItem()
                .getContent())
                .getChildren().get(0);
        if (cmdCodeArea != null) {
            cmdCodeArea.copy();
        }
        event.consume();
    }

    /**
     * cut selected text
     * @param event the event
     * */
    public void editCut(ActionEvent event) {
        CodeArea cmdCodeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel()
                .getSelectedItem()
                .getContent())
                .getChildren().get(0);
        if (cmdCodeArea != null) {
            cmdCodeArea.cut();
        }
        event.consume();
    }

    /**
     * paste text
     * @param event the event
     * */
    public void editPaste(ActionEvent event) {
        CodeArea cmdCodeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel()
                .getSelectedItem()
                .getContent())
                .getChildren().get(0);
        if (cmdCodeArea != null) {
            cmdCodeArea.paste();
        }
        event.consume();
    }

    /**
     * find text
     * @param event the event
     * */
    public void editFind(ActionEvent event) {
        if (!findPopup.isShowing()) {
            // get the current code area
            CodeArea cmdCodeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel()
                    .getSelectedItem()
                    .getContent())
                    .getChildren().get(0);
            // show the find popup at the right top corner of the code area
            findPopup.show(MccsApplication.stage,
                    cmdCodeArea.localToScreen(cmdCodeArea.getBoundsInLocal()).getMaxX() - 300,
                    cmdCodeArea.localToScreen(cmdCodeArea.getBoundsInLocal()).getMinY());
        }
        event.consume();
    }

    /**
     * replace text
     * @param event the event
     * */
    public void editReplace(ActionEvent event) {
        if (!replacePopup.isShowing()) {
            // get the current code area
            CodeArea cmdCodeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel()
                    .getSelectedItem()
                    .getContent())
                    .getChildren().get(0);
            // show the replace popup at the right top corner of the code area
            replacePopup.show(MccsApplication.stage,
                    cmdCodeArea.localToScreen(cmdCodeArea.getBoundsInLocal()).getMaxX() - 300,
                    cmdCodeArea.localToScreen(cmdCodeArea.getBoundsInLocal()).getMinY());
        }
        event.consume();
    }

    /**
     * undo
     * @param event the event
     * */
    public void editUndo(ActionEvent event) {     // undo the last operation
        CodeArea cmdCodeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel()
                .getSelectedItem()
                .getContent())
                .getChildren().get(0);
        if (cmdCodeArea != null) {
            cmdCodeArea.undo();
        }
        event.consume();
    }

    /**
     * redo
     * @param event the event
     * */
    public void editRedo(ActionEvent event) {
        CodeArea cmdCodeArea = (CodeArea) ((AnchorPane) codeTabPane.getSelectionModel()
                .getSelectedItem()
                .getContent())
                .getChildren().get(0);
        if (cmdCodeArea != null) {
            cmdCodeArea.redo();
        }
        event.consume();
    }

    /**
     * show the about dialog
     * @param event the event
     * */
    public void onMainAboutAction(ActionEvent event) {
        Alert aboutAlert = AlertFactory.createAlert(Alert.AlertType.INFORMATION,
                "Minecraft Command Studio", "About Minecraft Command Studio", """
                        Mod Name: Minecraft Command Studio\r
                        Author: Jaffe2718\r
                        License: GNU General Public License v3.0\r
                        """);
        HBox aboutHBox = new HBox();         // add a HBox to the alert
        aboutHBox.setSpacing(20);
        aboutHBox.setAlignment(Pos.CENTER);
        // add buttons with icon to the HBox      |GitHub| |Bilibili|
        Button githubButton = new Button();
        githubButton.setGraphic(new ImageView(new Image("assets/mccs/jfx/textures/github.png")));
        githubButton.setStyle("-fx-background-color: #F4F4F4");
        githubButton.setOnAction(    // open the GitHub repository when the button is clicked
            event1 -> {
                    try {
                        Desktop.getDesktop().browse(new URI("https://github.com/Jaffe2718/Minecraft-Command-Studio"));
                    } catch (IOException | URISyntaxException ioException) {
                        ioException.printStackTrace();
                    }
                }
        );
        Button bilibiliButton = new Button();
        bilibiliButton.setGraphic(new ImageView(new Image("assets/mccs/jfx/textures/bilibili.png")));
        bilibiliButton.setStyle("-fx-background-color: #F4F4F4");
        bilibiliButton.setOnAction(  // open the Bilibili page when the button is clicked
            event1 -> {
                    try {
                        Desktop.getDesktop().browse(new URI("https://space.bilibili.com/1671742926"));
                    } catch (IOException | URISyntaxException ioException) {
                        ioException.printStackTrace();
                    }
                }
        );
        aboutHBox.getChildren().addAll(githubButton, bilibiliButton);
        aboutAlert.getDialogPane().setExpandableContent(aboutHBox);
        aboutAlert.getDialogPane().setExpanded(true);                // auto expand the alert
        aboutAlert.showAndWait();
        event.consume();
    }
}
