package github.jaffe2718.mccs.jfx.unit;

import github.jaffe2718.mccs.config.MccsConfig;
import github.jaffe2718.mccs.jfx.MccsApplication;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.fxmisc.richtext.CodeArea;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * To execute the command in the code area<br/>
 * <br/>
 * There are 5 run modes:<br/>
 * 1. Single Line: execute the command in the line where the cursor is located<br/>
 * 2. Script: execute all the commands in the code area<br/>
 * 3. Selection: execute the commands in the selected area<br/>
 * 4. Loop: execute all the commands in the code area repeatedly<br/>
 * 5. Selection Loop: execute the commands in the selected area repeatedly<br/>
 * @author Jaffe2718
 * */
public class CmdExecutor {

    /**
     * The thread to execute the mc command
     * */
    public static Thread executeThread;

    /**
     * the flag to kill the thread
     * */
    public static boolean killThread = true;  // the flag to kill the thread

    private static Process sysShellProcess = null;

    private static Thread sysOutThread = null;

    /**
     * Execute the command in the code area
     * @param codeArea the code area
     * @param runModeComboBox the run mode combo box
     * @param runButton the run button to change the icon
     * */
    public static void execute(CodeArea codeArea, ComboBox<String> runModeComboBox, Button runButton) {  // create a new thread to execute the command
        if (MinecraftClient.getInstance().player == null ||
                (executeThread!=null && executeThread.isAlive())) {
            // if the player is not in the game, or the thread is running
            if (executeThread!=null && executeThread.isAlive()) {
                killThread = true;  // set the kill flag to true
                executeThread = null;           // set the thread to null
            }
            Platform.runLater(()->runButton.setGraphic(new ImageView("assets/mccs/jfx/textures/run.png")));   // change the icon to run
            return;
        }
        executeThread = new Thread(() -> {
            Platform.runLater(()->runButton.setGraphic(new ImageView("assets/mccs/jfx/textures/kill.png")));  // change the icon to kill
            executeTask(codeArea, runModeComboBox);
            Platform.runLater(()-> runButton.setGraphic(new ImageView("assets/mccs/jfx/textures/run.png")));   // change the icon to run
        });
        killThread = false;     // set the kill flag to false, enable the thread
        executeThread.start();
    }

    /**
     * Execute the single line Minecraft command
     * @param terminal the terminal to show the result
     * @param cmdSrc the source of the command
     * */
    public static void executeMcShell(TextArea terminal, TextField cmdSrc) {
        if (MinecraftClient.getInstance().player == null) return;
        ClientPlayerEntity executor = MinecraftClient.getInstance().player;
        String cmd = cmdSrc.getText();
        boolean sent;
        if (cmd.startsWith("/")) {
            sent = executor.networkHandler.sendCommand(cmd.substring(1).stripTrailing());
        } else {
            sent = executor.networkHandler.sendCommand(cmd.stripTrailing());
        }
        terminal.appendText(executor.getName().getString()+ ": " + cmd + "\n");
        if (sent) {
            terminal.appendText(">> Command sent to the server successfully!\n");
        } else {
            terminal.appendText(">> Failed to send the command to the server!\n");
        }
        cmdSrc.clear();
    }

    /**
     * Execute the system command
     * @param terminal the terminal to show the result
     * @param cmdSrc the source of the command
     * */
    public static void executeSysCmd(@NotNull TextArea terminal, @NotNull TextField cmdSrc) {
        String cmd = cmdSrc.getText();
        terminal.appendText("\r\n>> " + cmd + "\r\n");
        if (sysShellProcess == null || !sysShellProcess.isAlive()) {       // start a system shell process without command at the background
            var sysName = System.getProperty("os.name").toLowerCase();
            try {
                if (sysName.contains("win")) {
                    sysShellProcess = Runtime.getRuntime().exec("cmd.exe");
                } else if (sysName.contains("mac")) {
                    sysShellProcess = Runtime.getRuntime().exec("bash");
                } else {
                    sysShellProcess = Runtime.getRuntime().exec("sh");
                }
            } catch (Exception e) {
                Platform.runLater(() -> terminal.appendText(">> " + e.getMessage() + "\r\n"));
            }
        }
        if (sysOutThread == null) {           // start a new thread to write system shell log
            sysOutThread = new Thread(() -> {
                BufferedReader br = null;
                BufferedReader errBr = null;
                while (true) {
                    if (br == null) {
                        br = new BufferedReader(new InputStreamReader(sysShellProcess.getInputStream()));
                    }
                    if (errBr == null) {
                        errBr = new BufferedReader(new InputStreamReader(sysShellProcess.getErrorStream()));
                    }
                    try {
                        assert sysShellProcess != null;
                        assert br.ready() && errBr.ready();
                        String line;
                        while ((line = br.readLine()) != null) {
                            String finalLine = line;
                            Platform.runLater(() -> terminal.appendText(finalLine + "\r\n"));
                        }
                        while ((line = errBr.readLine()) != null) {
                            String finalLine = line;
                            Platform.runLater(() -> terminal.appendText(finalLine + "\r\n"));
                        }
                    } catch (AssertionError | IOException e) {
                        Platform.runLater(() -> terminal.appendText(">> " + e.getMessage() + "\r\n"));
                    }
                }
            });
            sysOutThread.start();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    assert sysShellProcess != null && sysShellProcess.isAlive();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sysShellProcess.getOutputStream()));
                    writer.write(cmd);
                    writer.newLine();
                    writer.flush();
                }
                catch (Exception e) {
                    Platform.runLater(() -> terminal.appendText(">> " + e.getMessage() + "\r\n"));
                }
            }
        }).start();
        cmdSrc.clear();
    }

    /**
     * Execute the script command
     * @param codeArea the code area to provide the Minecraft commands
     * @param runModeComboBox the run mode combo box to provide the run mode: Single Line, Script, Selection, Loop, Selection Loop
     * */
    private static void executeTask(CodeArea codeArea, ComboBox<String> runModeComboBox){
        if (MinecraftClient.getInstance().player == null) return;
        ClientPlayerEntity executor = MinecraftClient.getInstance().player;
        String[] cmds;
        switch (runModeComboBox.getSelectionModel().getSelectedItem()) {
            case "Single Line" -> {  // Get the line where the cursor is located
                int line = codeArea.getCurrentParagraph();
                String cmd = removeComments(codeArea.getText(line, 0, line, codeArea.getParagraphLength(line)));
                if (cmd.isBlank()) return;
                if (cmd.startsWith("/")) {
                    executor.networkHandler.sendCommand(cmd.substring(1).stripTrailing());
                } else {
                    executor.networkHandler.sendCommand(cmd.stripTrailing());
                }
            }
            case "Script" -> {     // Get the whole text as minecraft command
                cmds = codeArea.getText().split("\\r?\\n");
                executeScriptCmd(executor, cmds);
            }
            case "Selection" -> {  // Get the selected text as minecraft command
                cmds = codeArea.getSelectedText().split("\\r?\\n");
                executeScriptCmd(executor, cmds);
            }
            case "Loop"-> { // loop execution
                cmds = codeArea.getText().split("\\r?\\n");
                executeLoopCmd(executor, cmds);
            }
            default -> {   // loop selection
                cmds = codeArea.getSelectedText().split("\\r?\\n");
                executeLoopCmd(executor, cmds);
            }
        }
    }

    /**
     * execute the loop command
     * @param executor the executor
     * @param cmds the commands
     * */
    private static void executeLoopCmd(ClientPlayerEntity executor, String[] cmds) {
        while (!killThread && MccsApplication.stage != null &&
                MccsApplication.stage.isShowing() && MinecraftClient.getInstance().player != null) {
            executeScriptCmd(executor, cmds);
        }
    }

    /**
     * execute the script Minecraft command
     * @param executor the executor
     * @param cmds the commands in the *.mcfunction script
     * */
    private static void executeScriptCmd(ClientPlayerEntity executor, String @NotNull [] cmds) {
        for (String cmd : cmds) {
            try {
                Thread.sleep(MccsConfig.scriptInterval);
                cmd = removeComments(cmd);
                if (!cmd.isEmpty()) {
                    if (cmd.startsWith("/")) {
                        executor.networkHandler.sendCommand(cmd.substring(1).stripTrailing());
                    } else {
                        executor.networkHandler.sendCommand(cmd.stripTrailing());
                    }
                }
            } catch (InterruptedException ie) {     // if the thread is killed, stop the execution
                Platform.runLater(() -> {
                    CodeArea outLogCodeArea = (CodeArea) MccsApplication.stage.getScene().getRoot().lookup("#outLogCodeArea");
                    if (outLogCodeArea == null) return;
                    for (StackTraceElement stackTraceElement : ie.getStackTrace()) {
                        outLogCodeArea.appendText(stackTraceElement.toString() + "\r\n");
                    }
                    outLogCodeArea.appendText(ie.getMessage() + "\r\n");
                });
            }
        }
    }

    /**
     * Remove the comments in the code: remove the comments after "#" and space besfore "#"
     * @param codeLine the code to be processed
     * @return the code without comments
     * */
    private static String removeComments(String codeLine) {
        // for example: "say hello # this is a comment" -> "say hello"
        return codeLine.split("#")[0].stripTrailing();
    }
}
