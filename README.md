# Minecraft Command Studio
![Stars](https://img.shields.io/github/stars/Jaffe2718/Minecraft-Command-Studio?style=flat-square)
![Forks](https://img.shields.io/github/forks/Jaffe2718/Minecraft-Command-Studio?style=flat-square)
![Issues](https://img.shields.io/github/issues/Jaffe2718/Minecraft-Command-Studio?style=flat-square)
![License](https://img.shields.io/github/license/Jaffe2718/Minecraft-Command-Studio?style=flat-square)

by Jaffe2718

## Introduction
Minecraft Command Studio is a Fabric mod that used to assist you in writing and debugging Minecraft commands.
When the user presses the key `I` in the game world, a GUI will appear that allows you to write and execute commands.
The GUI is developed by [JavaFX](https://openjfx.io/), used to be an IDE for Minecraft commands.
This IDE has functions such as code highlighting, code prompts, AI code generation (testing function, requires OpenAI API Key), ChatGPT assistance (testing function, requires OpenAI API Key), game archive project file management, Minecraft instruction debugging and running, etc.
Therefore, In the IDE, you can write commands, execute commands, and debug commands.

## Installation
Please download the [Fabric API](https://modrinth.com/mod/fabric-api) and [Mod Menu](https://modrinth.com/mod/modmenu) and [MidnightLib](https://modrinth.com/mod/midnightlib) for your Minecraft version.
Then, download the mod from the [release page](https://github.com/Jaffe2718/Minecraft-Command-Studio/releases) and put it in the `mods` folder.

## Usage
When the user presses the key `I` in the game world, a GUI will appear that allows you to write and execute commands.
The GUI is developed by [JavaFX](https://openjfx.io/), used to be an IDE for Minecraft commands.
You can use the IDE to write commands, execute commands, and debug commands.

## Help Me
1. About OpenAI Issues
In my country or region, OpenAI Services are not available, so I can't test the AI code generation and ChatGPT assistance functions.
My idea is that only when the user presses the combination key `Alt+F3`, the request will be sent to OpenAI to obtain the code step gun function. At present, this function only has the code that cannot be implemented to send a request to OpenAI.
This method is at `github.jaffe2718.mccs.jfx.unit.widget.PopupFactory` interface, named `createAIPopup(String content)`, which is not completed yet.
I need to generate a pop-up window, and the pop-up window is a Vbox, which has a Label, the content of the Label is the code generated by OpenAI to complete the code, when the user clicks the Label in the pop-up window or presses the tab or enter key, the content of the Label will be inserted into the current cursor, just like GitHub Copilot.
It should be noted that when the user presses the combination key `Alt+F3`, only the `github.jaffe2718.mccs.jfx.unit.prompt.AISuggestionsRegister`'s `isShowing` field will be checked. If it is `false`, a request will be sent to OpenAI, and then the `isShowing` field will be set to `true`. When the user presses the combination key `Alt+F3`, if the `isShowing` field is `true`, no request will be sent to OpenAI.
And the value of this field depends on whether the AI pop-up window is displayed. If it is displayed, it is `true`. If it is not displayed, it is `false`. When the AI pop-up window is closed, the `isShowing` field will be set to `false` immediately, to avoid the user pressing the combination key `Alt+F3` and sending a request to OpenAI continuously, causing the OpenAI API Key to be wasted.
For the OpenAI API Key, of course, it is provided by the user. In the `github.jaffe2718.mccs.config.MccsConfig` class, there is a `openAIKey` field, which is used to store the user's OpenAI API Key.
The AI pop-up window will be bound to the specified `CodeArea` through the `addAISuggestionsTo(CodeArea target)` method of the `github.jaffe2718.mccs.jfx.unit.prompt.AISuggestionsRegister` class.
If you can help me to complete this function, I will be very grateful.

2. About ChatGPT Issues
And there is a ChatGPT assistance window problem. In the src/main/resources/assets/mccs/jfx/studio-view.fxml, you can see that the functions here are not implemented, because I can't use OpenAI's services.
So, if anyone can help me to develop and test these functions, I will be very grateful.

3. About Command Suggestions Issues
The principle of command suggestions is to instantiate a Minecraft ChatScreen, then enter the command on this ChatScreen, and set the current screen to this ChatScreen, then get the command prompt of this ChatScreen.
After getting the command prompt, set the current screen to the original screen, and then return the command prompt as a list. But now this function seems to be a bit sensitive, sometimes the command prompt will not come out. Can anyone help me fix this problem?

4. About Minecraft Version Support Issues
Due to my limited time, only Minecraft 1.19.4 version is currently supported. If anyone is willing to help me support more Minecraft versions, I will be very grateful.

5. Please help me by sending pull request to me
Please make sure that your pull request code can run, and the compiled mod can run normally in the game.
If you reference the third-party libraries of Java and these libraries are not mods, remember to add your dependencies in the `build.gradle` file, and add the configuration to compile these libraries in the `jar` task. Otherwise, when compiling into a mod jar file, the game will crash due to the lack of dependent libraries in the game.
When you send a pull request to me, remember to add your name in the `authors` field in the `fabric.mod.json` file.
Pull request is welcome, and I will be very grateful.