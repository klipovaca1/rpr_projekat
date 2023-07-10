package ba.unsa.etf.rpr.controllers;

import ba.unsa.etf.rpr.business.UserManager;
import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;

/**
 * The JavaFX controller for looking up searched users and possibly deleting them
 */
public class SearchController extends WindowManager {
    private final UserManager userManager = UserManager.getInstance();
    private final User user;
    private final List<User> users;
    public TableView<User> userTable;
    public TableColumn<User,Integer> idColumn;
    public TableColumn<User,String> nameColumn;
    public TableColumn<User,String> userTypeColumn;
    public TextField idField;
    public Button confirmationButton;
    public Button closeButton;
    /**
     * A constructor that initializes the user that has invoked the opening of this window and the list of all users from the database matching the searched name
     * @param user the user that requested the opening of this window
     * @param users the list container of all users in the database acquired from the search process
     */
    public SearchController(User user, List<User> users) {
        this.user = user;
        this.users = users;
    }
    /**
     * The method that gets called right before the opening of this window
     * Its purpose is to initialize all and restrict some of the JavaFX components shown in the created window
     */
    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
        refreshUsers();
        idField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                idField.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }
    /**
     * A method that deletes a user with the entered ID after confirming the process of deletion
     * After successfully deleting a user a pop-up will be shown in the form of an alert with a confirmation about the deletion
     * In case of any errors occuring an alert will be shown explaining the error in more details
     */
    @FXML
    public void deleteUser() {
        try {
            String idFieldText = idField.getText().trim();
            if (idFieldText.isEmpty()) {
                throw new CourtroomException("Provide an ID of a user to delete the corresponding user.");
            }
            if (user != null) {
                throw new CourtroomException("Access denied - deleting users from database is permitted only for admins.");
            }
            int id = Integer.parseInt(idFieldText);
            if (users.stream().noneMatch(person -> person.getId() == id)) {
                throw new CourtroomException("Selected user isn't part of this search result");
            }
            Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to delete the user with ID of " + id, ButtonType.YES, ButtonType.NO).showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                User deletedUser = userManager.deleteUser(id);
                openAlert(Alert.AlertType.INFORMATION, "Deletion success|User " + deletedUser.getName() + " successfully deleted from database.");
                users.remove(deletedUser);
                refreshUsers();
            }
        }
        catch (CourtroomException exception) {
            openAlert(Alert.AlertType.ERROR, "Deletion failure|" + exception.getMessage());
            if (exception.getMessage().contains("Access denied")) {
                idField.setEditable(false);
                confirmationButton.setDisable(true);
            }
        }
    }
    /**
     * This method calls its parent's closeWindow method, which does exactly what the method name suggests
     */
    @FXML
    public void closeSearch() {
        closeWindow(closeButton);
    }
    /**
     * A helper method that loads and refreshes the users acquired from the search process
     */
    private void refreshUsers() {
        userTable.setItems(FXCollections.observableList(users));
        userTable.refresh();
    }
}