package github.jaffe2718.mccs.event;


import github.jaffe2718.mccs.client.MinecraftCommandStudioClient;
import github.jaffe2718.mccs.jfx.MccsApplication;
import github.jaffe2718.mccs.mixin.ChatScreenMixin;
import javafx.application.Platform;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.fxmisc.richtext.CodeArea;

/**
 * This class is used to handle Minecraft game events.<br>
 * It is used to handle the key event and the game message event.<br>
 * It is used to show the UI and hide the UI.<br>
 * It is used to get the command from the chat screen.<br>
 * */
public abstract class EventHandler {

    public static String lastCommand = "";
    public static ChatInputSuggestor suggestor = null;

    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(EventHandler::onStartClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(EventHandler::onEndClientTick);
        ClientReceiveMessageEvents.GAME.register(EventHandler::onGameMessage);
    }

    private static void onEndClientTick(MinecraftClient client) {
        if (client.player != null &&
                MinecraftCommandStudioClient.iKey.isPressed()) {  // if the player is in the game world and press the key I
            Platform.runLater(()->{                               // show the UI
                if (MccsApplication.stage!=null) {
                    MccsApplication.stage.show();
                }
            });
            ChatScreen chatScreen = new ChatScreen("/");  // set the chat screen to "/gamemode"
            client.setScreen(chatScreen);
        } else if (client.player==null && MccsApplication.stage!=null && MccsApplication.stage.isShowing()) {
            // if the player exit the game world, hide UI and clear the log in outLogCodeArea
            Platform.runLater(()->{
                CodeArea outLogCodeArea = (CodeArea) MccsApplication.stage.getScene().getRoot().lookup("#outLogCodeArea");
                if (outLogCodeArea!=null) {
                    outLogCodeArea.clear();
                }
            });
            Platform.runLater(MccsApplication.stage::hide);
        }
    }

    private static void onGameMessage(Text message, boolean overlay) {
        if (MccsApplication.stage != null) {
            Platform.runLater(()->{
                CodeArea outLogCodeArea = (CodeArea) MccsApplication.stage.getScene().getRoot().lookup("#outLogCodeArea");
                if (outLogCodeArea!=null) {
                    outLogCodeArea.appendText("[Info] " + message.getString() + "\r\n");
                }
            });
        }
    }

    /**
     * This method is used to check the lastCommand and create a ChatInputSuggestor.<br>
     * <br>
     * It is called when the client tick starts.<br>
     * @param client the client<code>MinecraftClient</code><br>
     * To check if {@link EventHandler#lastCommand} is not empty.
     * if it is not empty, switch the screen to get the command.
     */
    private static void onStartClientTick(MinecraftClient client) {
        if (client.player != null && !lastCommand.equals("")) {
            ChatScreen chatScreen = new ChatScreen("");
            Screen currentScreen = client.currentScreen;
            client.setScreen(chatScreen);
            suggestor = ((ChatScreenMixin)chatScreen).getChatInputSuggestor();
            suggestor.refresh();
            if (lastCommand.startsWith("/")) {
                // tap the command to the chat field one by one
                for (int i = 0; i < lastCommand.length(); i++) {
                    ((ChatScreenMixin) chatScreen).getChatField().write(""+lastCommand.charAt(i));
                }
            } else {
                ((ChatScreenMixin) chatScreen).getChatField().write("/" + lastCommand);
            }
            lastCommand = "";                                  // clear the lastCommand
            client.setScreen(currentScreen);                   // switch back to the screen before
        }
    }
}
