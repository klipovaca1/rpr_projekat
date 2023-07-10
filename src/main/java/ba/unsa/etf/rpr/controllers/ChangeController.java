package ba.unsa.etf.rpr.controllers;

import ba.unsa.etf.rpr.business.UserManager;
import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * The JavaFX controller for modifying user's credentials
 */
public class ChangeController extends WindowManager {
    private final UserManager userManager = UserManager.getInstance();
    private final User user;
    private String oldName;
    private String oldPassword;
    private String newName;
    private String newPassword;
    private String confirmationPassword;
    public TextField oldNameField;
    public TextField newNameField;
    public TextField oldPasswordField;
    public PasswordField newPasswordField;
    public PasswordField confirmationPasswordField;
    public Button confirmButton;
    public Button cancelButton;
    /**
     * A constructor that initializes the user that has invoked the opening of this window
     * @param user the user that requested the opening of this window
     */
    public ChangeController(User user) {
        this.user = user;
    }
    /**
     * The method that gets called right before the opening of this window
     * Its only purpose is to initialize the JavaFX text field component for the user's current name
     */
    @FXML
    public void initialize() {
        oldNameField.setText(user.getName());
    }
    /**
     * After clicking on the confirm button in this window the validation process starts, after which the user's credentials undergo a change
     * In case of an error an alert will pop up showing us the error and some of its details
     */
    @FXML
    public void changeCredentials() {
        try {
            validateFields();
            try {
                user.setName(newName);
                user.setPassword(newPassword);
                userManager.updateCredentials(user);
                closeWindow(confirmButton);
            }
            catch (CourtroomException exception) {
                user.setName(oldName);
                user.setPassword(oldPassword);
                throw exception;
            }
        }
        catch (CourtroomException exception) {
            openAlert(Alert.AlertType.ERROR, "Field problems|" + exception.getMessage());
        }
    }
    /**
     * This method calls its parent's closeWindow method, which does exactly what the method name suggests
     */
    @FXML
    public void closeChange() {
        closeWindow(cancelButton);
    }
    /**
     * A helper method whose sole purpose is to acquire the values in the text fields of this controller's window
     * Its existence is justified with the avoidance of code repetition in other methods
     */
    private void loadFields() {
        oldName = oldNameField.getText().trim();
        oldPassword = oldPasswordField.getText().trim();
        newName = newNameField.getText().trim();
        newPassword = newPasswordField.getText().trim();
        confirmationPassword = confirmationPasswordField.getText().trim();
    }
    /**
     * A helper method that validates the values of the text fields currently present on the screen
     * @throws CourtroomException in case of empty or mismatched inputs of text fields
     */
    private void validateFields() throws CourtroomException {
        loadFields();
        if (oldName.isEmpty()) {
            throw new CourtroomException("Old name field left empty.");
        }
        if (oldPassword.isEmpty()) {
            throw new CourtroomException("Old password field left empty.");
        }
        if (!user.getName().equals(oldName) || !user.getPassword().equals(oldPassword)) {
            throw new CourtroomException("Incorrect input of old username or password!");
        }
        if (newName.isEmpty()) {
            if (newPassword.isEmpty()) {
                if (confirmationPassword.isEmpty()) {
                    throw new CourtroomException("All new fields are left empty.");
                }
                else {
                    throw new CourtroomException("New password field left empty - unentered password cannot be confirmed.");
                }
            }
            newName = oldName;
        }
        else if (newPassword.isEmpty() && !confirmationPassword.isEmpty() || !newPassword.isEmpty() && confirmationPassword.isEmpty()) {
            throw new CourtroomException("New and confirmation password fields must be left empty if you don't wish to change password.");
        }
        if (newPassword.isEmpty()) {
            newPassword = oldPassword;
        }
        else if (!newPassword.equals(confirmationPassword)) {
            throw new CourtroomException("New password not confirmed correctly.");
        }
    }
}