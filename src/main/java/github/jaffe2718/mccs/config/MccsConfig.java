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
     * The model used to generate the prompt.<br>
     * */
    @Entry public static AiModel aiModel = AiModel.CODE_DAVINCI_002;

    /**
     * The maximum number of command suggestions generated by the AI.<br>
     * */
    @Entry(min = 1, max = 5, isSlider = true) public static int aiMaxSuggestions = 1;

    /**
     * The interval between two Minecraft commands executed (Unit: millisecond).<br>
     * */
    @Entry(min = 0) public static int scriptInterval = 45;

    /**
     * This config depend if the JavaFX UI is always on top.<br>
     * */
    @Entry public static boolean alwaysOnTop = false;


    public enum AiModel {

        TEXT_CURIE_001("text-curie-001"),
        TEXT_BABBAGE_001("text-babbage-001"),
        DAVINCI("davinci"),
        CURIE("curie"),
        BABBAGE("babbage"),
        ADA("ada"),
        GPT_35_TURBO("gpt-3.5-turbo"),
        GPT_35_TURBO_0301("gpt-3.5-turbo-0301"),
        TEXT_DAVINCI_003("text-davinci-003"),
        TEXT_DAVINCI_002("text-davinci-002"),
        CODE_DAVINCI_002("code-davinci-002"),
        GPT_4("gpt-4"),
        GPT_4_0314("gpt-4-0314"),
        GPT_4_32K("gpt-4-32k"),
        GPT_4_32K_0314("gpt-4-32k-0314");

        public final String name;

        AiModel(String name) {
            this.name = name;
        }
    }

}
