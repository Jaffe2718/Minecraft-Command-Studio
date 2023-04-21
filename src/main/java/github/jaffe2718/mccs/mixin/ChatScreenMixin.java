package github.jaffe2718.mccs.mixin;

import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


/**
 * Get the chatInputSuggestor and chatField of ChatScreen.<br>
 * {@link net.minecraft.client.gui.screen.ChatScreen}<br>
 *
 * Usage:<br>
 * <br>
 * {@code ChatScreen chatScreen = ...;}<br>
 * <br>
 * {@code ChatInputSuggestor chatInputSuggestor = ((ChatScreenMixin) chatScreen).getChatInputSuggestor();}<br>
 * <br>
 * {@code TextFieldWidget chatField = ((ChatScreenMixin) chatScreen).getChatField();}<br>
 * */
@Mixin(ChatScreen.class)
public interface ChatScreenMixin {
    @Accessor
    ChatInputSuggestor getChatInputSuggestor();

    @Accessor
    TextFieldWidget getChatField();

}
