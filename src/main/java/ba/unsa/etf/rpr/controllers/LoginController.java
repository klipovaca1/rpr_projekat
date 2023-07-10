package ba.unsa.etf.rpr.controllers;

import ba.unsa.etf.rpr.business.UserManager;
import ba.unsa.etf.rpr.exceptions.CourtroomException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * The JavaFX controller for logging in users to the application
 */
public class LoginController extends WindowManager {
    public TextField nameField;
    public PasswordField passwordField;
    public Button loginButton;
    /**
     * The method that is responsible for logging the user into the system
     * Its only purpose is to call their parent's method openWindow which simultaneously closes the current window and openes up the main application window
     * In case of any errors opens up an alert that shows us the cause of the error and some more details about it
     */
    @FXML
    public void loginUser() {
        try {
            openWindow("Courtroom case overview", "/fxml/mainScreen.fxml", new MainController(new UserManager().searchByCredentials(nameField.getText().trim(), passwordField.getText().trim())), (Stage)loginButton.getScene().getWindow(), false);
        }
        catch (CourtroomException exception) {
            openAlert(Alert.AlertType.ERROR, exception.getMessage());
        }
    }
}