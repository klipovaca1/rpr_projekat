package ba.unsa.etf.rpr;

import ba.unsa.etf.rpr.controllers.LoginController;
import ba.unsa.etf.rpr.controllers.WindowManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * A full representation of the created courtroom application through the use of the Graphical User Interface (GUI)
 * @author Kemal Lipovača
 */
public class AppFX extends Application {
    @Override
    public void start(Stage primaryStage) {
        WindowManager.openWindow("Courtroom login procedure", "/fxml/loginScreen.fxml", new LoginController(), null, false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}