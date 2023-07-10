package ba.unsa.etf.rpr.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

/**
 * The backbone JavaFX controller of this graphical user interface (GUI)
 * This controller is used for opening windows with their required JavaFX components as well as closing them
 * In case of any errors occuring while using the application the alert method is to be invoked from this class
 * Considering the above-mentioned all other controllers should extend this controller in order for the application to work properly
 * @author Kemal Lipovača
 */
public class WindowManager {
    /**
     * The method responsible for opening new windows in addition to closing the previous one or using it as a parent for the new one
     * @param title the title of the new window
     * @param file the file location of the fxml code used to design the window
     * @param controller the controller responsible for implementing JavaFX components from the corresponding fxml file
     * @param previousStage the previously opened stage
     * @param modality the parameter that determines the modality of the soon-to-be opened stage
     */
    public static void openWindow(String title, String file, Object controller, Stage previousStage, boolean modality) {
        try {
            FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource(file));
            loader.setController(controller);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            if (modality) {
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null));
                stage.showAndWait();
            }
            else {
                if (previousStage != null) {
                    previousStage.close();
                }
                stage.show();
            }
        }
        catch (IOException exception) {
            openAlert(Alert.AlertType.ERROR, "Unable to open requested window|" + exception.getMessage());
            System.exit(-1);
        }
    }
    /**
     * The method responsible for closing (mostly modal) windows
     * @param node the JavaFX component from which the stage (window) is derived used for closing the window
     */
    public static void closeWindow(Node node) {
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
    }
    /**
     * The method responsible for informing the user about a failed or a successful event
     * @param alertType the type of alert to be opened, also defines the title of the alert window
     * @param message a string that is consisted of two parts which are separated into the header text and content text of the alert
     */
    public static void openAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        switch (alertType) {
            case ERROR -> alert.setTitle("Error");
            case INFORMATION -> alert.setTitle("Helpful message");
        }
        alert.setHeaderText(message.substring(0, message.indexOf("|")));
        alert.setContentText(message.substring(message.indexOf("|") + 1));
        alert.initModality(Modality.WINDOW_MODAL);
        long numberOfWindows = Stage.getWindows().stream().filter(Window::isShowing).count();
        alert.initOwner(Stage.getWindows().stream().filter(Window::isShowing).skip(numberOfWindows - 1).findFirst().orElse(null));
        alert.showAndWait();
    }
}