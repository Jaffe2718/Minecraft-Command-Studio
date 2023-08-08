package github.jaffe2718.mccs;

import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.lib.util.PlatformFunctions;
import github.jaffe2718.mccs.config.MccsConfig;
import github.jaffe2718.mccs.jfx.MccsApplication;
import net.fabricmc.api.ModInitializer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.nio.file.Path;

/**Main Class*/
public class MinecraftCommandStudio implements ModInitializer {

    /**
     * The mod ID of Minecraft Command Studio.<br>
     * */
    public static final String MOD_ID = "mccs";

    /**
     * The logger of Minecraft Command Studio.<br>
     * */
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * This is the directory of current Minecraft Core.<br>
     * */
    public static final Path RUNTIME_PATH = PlatformFunctions.getConfigDirectory().getParent().toAbsolutePath();

    /**
     * This is the thread that will run the JavaFX application. It can avoid blocking the main thread.<br>
     * */
    public static Thread uiFxThread = new Thread(MccsApplication::initialization);

    @Override
    public void onInitialize() {

        LOGGER.info("Minecraft Command Studio is initializing!");
        MidnightConfig.init(MOD_ID, MccsConfig.class);
        uiFxThread.start();
    }
}
