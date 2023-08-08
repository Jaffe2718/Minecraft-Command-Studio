# Minecraft Command Studio
![Stars](https://img.shields.io/github/stars/Jaffe2718/Minecraft-Command-Studio?style=flat-square)
![Forks](https://img.shields.io/github/forks/Jaffe2718/Minecraft-Command-Studio?style=flat-square)
![Issues](https://img.shields.io/github/issues/Jaffe2718/Minecraft-Command-Studio?style=flat-square)
![Licence](https://img.shields.io/github/license/Jaffe2718/Minecraft-Command-Studio?style=flat-square)

by Jaffe2718

## Introduction
Minecraft Command Studio is a Fabric mod that used to assist you in writing and debugging Minecraft commands.
When the user presses the key `I` in the game world, a GUI will appear that allows you to write and execute commands.
The GUI is developed by [JavaFX](https://openjfx.io/), used to be an IDE for Minecraft commands.
This IDE has functions such as code highlighting, code prompts, game archive project file management, Minecraft instruction debugging and running, etc.
Therefore, In the IDE, you can write commands, execute commands, and debug commands.

## User Guide

### Features
- Code highlighting
- Code prompts
- Game archive project file management
- Minecraft command debugging and running

### Installation
Please download the [Fabric API](https://modrinth.com/mod/fabric-api) and [Mod Menu](https://modrinth.com/mod/modmenu) and [MidnightLib](https://modrinth.com/mod/midnightlib) for your Minecraft version.
Then, download the mod from the [release page](https://github.com/Jaffe2718/Minecraft-Command-Studio/releases) and put it in the `mods` folder.

### Usage
When the user presses the key `I` in the game world, a GUI will appear that allows you to write and execute commands.
The GUI is developed by [JavaFX](https://openjfx.io/), used to be an IDE for Minecraft commands.
You can use the IDE to write commands, execute commands, and debug commands.
You can customize the keymap instead of using the default keymap `I`.

## Developer Guide

### Setup
1. Install [Java SE Development Kit 17](https://www.oracle.com/java/technologies/downloads/) and [IntelliJ IDEA](https://www.jetbrains.com/idea/download/).
2. Clone the repository.
```shell
git clone https://github.com/Jaffe2718/Minecraft-Command-Studio.git
```
3. Open the project in IntelliJ IDEA and wait for the dependencies to be downloaded.

### View JavaDoc
1. Open the project in IntelliJ IDEA.
2. Click `Tools` -> `Generate JavaDoc...` -> `OK`.
3. Open the `index.html` in the browser.

### Build
1. run `gradlew build` in the project directory.
2. if you want to build a jar file for different operating systems, you should edit the `build.gradle` file.
```groovy
// this is the default dependencies task in build.gradle, you can edit it to build a jar file for different operating systems
dependencies {
    // To change the versions see the gradle.properties file
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    mappings "net.fabricmc:yarn:${project.yarn_mappings}:v2"
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"

        // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"
    // other mods
    modApi("com.terraformersmc:modmenu:${modmenu_version}") { exclude(module: "fabric-api") }
    modImplementation "maven.modrinth:midnightlib:${midnightlib_version}"
    implementation "org.fxmisc.richtext:richtextfx:${project.richtextfx_version}"


    // other dependencies
    // different os use different javafx binaries
    if (System.properties['os.name'].toLowerCase().contains('win')) {  // windows
        include runtimeOnly("org.openjfx:javafx-base:${project.javafx_version}:win")
        include runtimeOnly("org.openjfx:javafx-controls:${project.javafx_version}:win")
        include runtimeOnly("org.openjfx:javafx-fxml:${project.javafx_version}:win")
        include runtimeOnly("org.openjfx:javafx-graphics:${project.javafx_version}:win")
    }
    else if (System.properties['os.name'].toLowerCase().contains('mac')) {  // mac
        include runtimeOnly("org.openjfx:javafx-base:${project.javafx_version}:mac")
        include runtimeOnly("org.openjfx:javafx-controls:${project.javafx_version}:mac")
        include runtimeOnly("org.openjfx:javafx-fxml:${project.javafx_version}:mac")
        include runtimeOnly("org.openjfx:javafx-graphics:${project.javafx_version}:mac")
    }
    else if (System.properties['os.name'].toLowerCase().contains('linux')) { // linux
        include runtimeOnly("org.openjfx:javafx-base:${project.javafx_version}:linux")
        include runtimeOnly("org.openjfx:javafx-controls:${project.javafx_version}:linux")
        include runtimeOnly("org.openjfx:javafx-fxml:${project.javafx_version}:linux")
        include runtimeOnly("org.openjfx:javafx-graphics:${project.javafx_version}:linux")
    }

    include runtimeOnly("org.fxmisc.flowless:flowless:${project.flowless_version}")
    include runtimeOnly("org.fxmisc.richtext:richtextfx:${project.richtextfx_version}")
    include runtimeOnly("org.fxmisc.undo:undofx:${project.undofx_version}")
    include runtimeOnly("org.fxmisc.wellbehaved:wellbehavedfx:${project.wellbehavedfx_version}")
    include runtimeOnly("org.reactfx:reactfx:${project.reactfx_version}")
}
```
