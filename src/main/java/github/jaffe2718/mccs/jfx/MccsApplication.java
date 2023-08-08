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
import java.io.PrintStream;

/**
 * run this application in minecraft*/
public class MccsApplication extends Application {
    private static final String FXML_PATH = "/assets/mccs/jfx/studio-view.fxml";

    /**
     * mod icon path
     * */
    public static final String ICON_PATH = "assets/mccs/icon.png";

    /**
     * JavaFX stage of mccs IDE
     * */
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

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws IOException if something goes wrong
     */
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

    /**
     * initialize the mccs IDE
     * */
    public static void initialization() {
        launch();
    }
}
