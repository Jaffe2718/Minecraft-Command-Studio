package github.jaffe2718.mccs.client;

import github.jaffe2718.mccs.event.EventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

/**
 * This class is used to initialize the client.<br>
 * It is used to register the key binding.<br>
 * {@link MinecraftCommandStudioClient#iKey}
 * It is used to register the event handler.<br>
 * {@link EventHandler}
 * */
public class MinecraftCommandStudioClient implements ClientModInitializer {

    /**
     * When the player press the key I in the game world, the JavaFX UI will show up.<br>
     * */
    public static final KeyBinding iKey = new KeyBinding("key.mccs.ide", GLFW.GLFW_KEY_I, KeyBinding.GAMEPLAY_CATEGORY);

    /**
     * This method is used to register the key binding and the event handler.<br>
     * */
    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(iKey);
        EventHandler.register();
    }
}
