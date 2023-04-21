package github.jaffe2718.mccs.config;

import eu.midnightdust.lib.config.MidnightConfig;

/**
 * This class is used to store the configuration of the mod.<br>
 * */
public class MccsConfig extends MidnightConfig{

    /**
     * The API key of OpenAI.<br>
     * */
    @Entry public static String openaiApiKey = "";

    /**
     * The interval between two Minecraft commands executed (Unit: millisecond).<br>
     * */
    @Entry(min = 0) public static int scriptInterval = 45;

    /**
     * This config depend if the JavaFX UI is always on top.<br>
     * */
    @Entry public static boolean alwaysOnTop = false;
}
