package github.jaffe2718.mccs.jfx;


import github.jaffe2718.mccs.config.MccsConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * run this application in minecraft*/
public class MccsApplication extends Application {
    private static final String FXML_PATH = "/assets/mccs/jfx/studio-view.fxml";
    public static final String ICON_PATH = "assets/mccs/icon.png";
    public static Stage stage;


    private final EventHandler<WindowEvent> closeHandler = new EventHandler<>() {
        @Override
        public void handle(WindowEvent event) {
            if (stage.isIconified()){
                stage.setIconified(false);
            }
            event.consume();
            stage.hide();
        }
    };

    @Override
    public void start(Stage primaryStage) throws IOException {
        // System.out.println(this.getClass().getResource(FXML_PATH));
        System.setProperty("java.awt.headless", "false");    // for awt.Desktop, which is used to open file in explorer or open url in browser
        Platform.setImplicitExit(false);
        stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(MccsApplication.class.getResource(FXML_PATH));
        Scene scene = new Scene(fxmlLoader.load(), 850, 500);
        stage.getIcons().add(new Image(ICON_PATH));
        stage.setResizable(true);
        stage.setAlwaysOnTop(MccsConfig.alwaysOnTop);
        stage.setScene(scene);
        stage.setOnCloseRequest(closeHandler);
        stage.setTitle("Minecraft Command Studio");
    }

    public static void initialization() {
        launch();
    }
}
