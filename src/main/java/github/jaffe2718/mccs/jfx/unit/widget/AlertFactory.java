package github.jaffe2718.mccs.jfx.unit.widget;

import github.jaffe2718.mccs.jfx.MccsApplication;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Used to create an alert with the given parameters<br>
 * Don't implement this interface<br>
 * Just use the static methods<br>
 * */
public interface AlertFactory {

    /**
     * Create an alert with the given parameters
     * @param type the type of alert <br>
     * @param title the title of the alert (can be null) <br>
     * @param header the header of the alert <br>
     * @param content the content of the alert <br>
     * @param buttons the buttons of the alert, default is OK <br>
     * @return the alert <br>
     * */
    @NotNull
    static Alert createAlert(Alert.AlertType type, @Nullable String title, String header, String content, @Nullable ButtonType... buttons) {
        Alert alert;
        if (buttons != null) {
            alert = new Alert(type, content, buttons);
        } else {
            alert = new Alert(type, content);
        }
        if (title != null) {
            alert.setTitle(title);
        }
        // set icon
        Stage sanvStage = (Stage) alert.getDialogPane().getScene().getWindow();
        sanvStage.getIcons().add(new Image(MccsApplication.ICON_PATH));
        alert.setHeaderText(header);
        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        return alert;
    }

    /**
     * To add the stack trace of an exception the target alert<br>
     *
     * @param target the target alert<br>
     *               the alert type must be {@link Alert.AlertType#ERROR}<br>
     *               otherwise, do nothing<br>
     *
     * @param e the exception<br>
     *          the stack trace of the exception will be added to the target alert<br>
     *          the stack trace will be shown in a scroll pane<br>
     *          the text color will be red<br>
     *          the text will be formatted like this:<br>
     *          <code>
     *          e.getMessage() + "\r\n" + e.getStackTrace().toString() + "\r\n"
     *          </code>
     * */
    static void addExceptionStackTrace(Alert target, Exception e) {
        if (target.getAlertType() != Alert.AlertType.ERROR) {
            return;
        }
        String errorMessage = e.getMessage() + "\r\n";
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            errorMessage += "\t" + stackTraceElement.toString() + "\r\n";
        }
        Label errorLabel = new Label(errorMessage);
        // red color
        errorLabel.setStyle("-fx-text-fill: #ff0000");
        ScrollPane errorScrollPane = new ScrollPane(errorLabel);
        errorScrollPane.setFitToWidth(true);
        errorScrollPane.setMaxHeight(300);
        target.getDialogPane().setExpandableContent(errorScrollPane);
    }
}
