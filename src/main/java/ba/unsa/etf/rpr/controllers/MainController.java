package ba.unsa.etf.rpr.controllers;

import ba.unsa.etf.rpr.business.LawsuitManager;
import ba.unsa.etf.rpr.business.UserManager;
import ba.unsa.etf.rpr.domain.*;
import ba.unsa.etf.rpr.exceptions.CourtroomException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * The JavaFX controller for the overview of users' cases
 */
public class MainController extends WindowManager {
    private final UserManager userManager = UserManager.getInstance();
    private final LawsuitManager lawsuitManager = LawsuitManager.getInstance();
    private final User user;
    private List<User> users;
    public Label welcomeLabel;
    public Button changeButton;
    public Button logoutButton;
    public TextField nameField;
    public Button searchButton;
    public TableView<Lawsuit> caseTable;
    public TableColumn<Lawsuit,Integer> idColumn;
    public TableColumn<Lawsuit,String> titleColumn;
    public TableColumn<Lawsuit,Long> uinColumn;
    public TableColumn<Lawsuit,String> judgeColumn;
    public TableColumn<Lawsuit,String> defendantColumn;
    public TableColumn<Lawsuit,String> lawyerColumn;
    public TableColumn<Lawsuit,String> prosecutorColumn;
    public TableColumn<Lawsuit,String> verdictColumn;
    public Button confirmationButton;
    public Button caseButton;
    public TextField idField;
    /**
     * A constructor that initializes the user that has invoked the opening of this window
     * @param user the user that requested the opening of this window
     */
    public MainController(User user) {
        this.user = user;
    }
    /**
     * The method that gets called right before the opening of this window
     * Its purpose is to initialize all and restrict some of the JavaFX components shown in the created window
     */
    @FXML
    public void initialize() {
        if (user == null) {
            welcomeLabel.setText("Welcome Admin...");
        }
        else {
            welcomeLabel.setText("Welcome " + user.getUserType() + ' ' + user.getName() + '!');
        }
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        uinColumn.setCellValueFactory(new PropertyValueFactory<>("uin"));
        judgeColumn.setCellValueFactory((tableData) -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(tableData.getValue().getJudge().getName());
            return property;
        });
        defendantColumn.setCellValueFactory((tableData) -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(tableData.getValue().getDefendant().getName());
            return property;
        });
        lawyerColumn.setCellValueFactory((tableData) -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(tableData.getValue().getLawyer().getName());
            return property;
        });
        prosecutorColumn.setCellValueFactory((tableData) -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(tableData.getValue().getProsecutor().getName());
            return property;
        });
        verdictColumn.setCellValueFactory(new PropertyValueFactory<>("verdict"));
        refreshAll();
        idField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                idField.setText(newValue.replaceAll("\\D", ""));
            }
        });
    }
    /**
     * Clicking on the corresponding button opens up a new window where the user can change their credentials - their name and/or password
     * In the event of the current user being an admin an alert is shown telling them that they don't need to change their credentials
     * After closing the newly opened window an alert will pop off, indicating a successful or unnecessary change in their credentials
     * All errors made while reconfiguring the new data will be handled inside the new window
     */
    @FXML
    public void changeCredentials() {
        if (user == null) {
            openAlert(Alert.AlertType.INFORMATION, "Change unnecessary|No need for you to change your credentials mr. admin...");
            changeButton.setDisable(true);
        }
        else {
            String name = user.getName();
            openWindow("Courtroom credential reconfiguration", "/fxml/changeScreen.fxml", new ChangeController(user), (Stage)changeButton.getScene().getWindow(), true);
            if (user.getName().equals(name)) {
                openAlert(Alert.AlertType.INFORMATION, "Update unnecessary|No changes in credentials detected.");
            }
            else {
                openAlert(Alert.AlertType.INFORMATION, "Update successful|Successfully changed credentials.");
                refreshAll();
            }
        }
    }
    /**
     * The method that is responsible for logging the user out of the system
     * Its only purpose is to call their parent's method openWindow which simultaneously closes the current window and openes up the login window again
     */
    @FXML
    public void logoutUser() {
        openWindow("Courtroom login procedure", "/fxml/loginScreen.fxml", new LoginController(), (Stage)logoutButton.getScene().getWindow(), false);
    }
    /**
     * Clicking on the corresponding button opens up a new window where the user can view the search results only if the prompt has at least one result found (the number of found results is shown as well)
     * In the event of leaving the search field empty or no results found an error will be shown in the form of an alert telling the user what was the problem
     * All errors that could possibly occure inside the search window will be dealt with as part of that window's controller
     */
    @FXML
    public void searchUsers() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            openAlert(Alert.AlertType.INFORMATION, "Unable to search|Provide a name to start with process of searching.");
        }
        else {
            List<User> searchedUsers;
            if (!name.equals("getAll()")) {
                searchedUsers = users.stream().filter(person -> person.getName().toLowerCase().contains(name.toLowerCase())).collect(toList());
            }
            else {
                searchedUsers = users;
            }
            if (searchedUsers.size() == 0) {
                openAlert(Alert.AlertType.INFORMATION, "Search completed|Database is empty - no users found.");
            }
            else {
                openAlert(Alert.AlertType.INFORMATION, "Search completed|" + users.size() + " users found.");
                openWindow("Courtroom user search", "/fxml/searchScreen.fxml", new SearchController(user, users), (Stage)searchButton.getScene().getWindow(), true);
            }
        }
    }
    /**
     * Clicking on the corresponding button opens up a new window where the user can add or edit court cases
     * In the event of not having authorized access an error will show up in the form of an alert pop-up
     * All errors caused inside of the newly opened window will be handled at the time of occurence
     */
    @FXML
    public void addOrEditCases() {
        if (user != null && !user.getUserType().equals("Judge")) {
            openAlert(Alert.AlertType.ERROR, "Access denied|No case fiddling permissions.");
            caseButton.setDisable(true);
        }
        else {
            openWindow("Courtroom case fiddling", "/fxml/caseScreen.fxml", new CaseController(user, users), (Stage)caseButton.getScene().getWindow(), true);
            refreshAll();
        }
    }
    /**
     * A method that deletes a court case with the entered ID after confirming the process of deletion
     * After successfully deleting a court case an alert will be shown informing the user about the deletion
     * In case of any errors occuring a pop-up window will be shown giving more details about the error itself
     */
    @FXML
    public void deleteCase() {
        try {
            String idFieldText = idField.getText().trim();
            if (idFieldText.isEmpty()) {
                throw new CourtroomException("Provide an ID of a case to delete the corresponding case.");
            }
            if (user != null) {
                throw new CourtroomException("Access denied - deleting cases from database is permitted only for admins.");
            }
            int id = Integer.parseInt(idFieldText);
            Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you wish to delete the case with ID of " + id, ButtonType.YES, ButtonType.NO).showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                lawsuitManager.deleteLawsuit(Integer.parseInt(idFieldText));
                openAlert(Alert.AlertType.INFORMATION, "Deletion success|Provided case successfully removed from database.");
                refreshAll();
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
     * A helper method that reacquires all users and all required cases to be shown
     * It usually gets called after modifications have been made, be it to the current user, other users or some (if not all) court cases
     * In case of an error occuring the program is to be shut down immmediately because only unexpected errors can arise in this method
     */
    private void refreshAll() {
        try {
            if (user != null) {
                caseTable.setItems(FXCollections.observableList(lawsuitManager.searchByUser(user)));
            }
            else {
                caseTable.setItems(FXCollections.observableList(lawsuitManager.getAllLawsuits()));
            }
            caseTable.refresh();
            users = userManager.getAllUsers();
        }
        catch (CourtroomException exception) {
            if (!exception.getMessage().equals("Database is empty - no users found.")) {
                openAlert(Alert.AlertType.ERROR, "Unexpected error occured|" + exception.getMessage());
                System.exit(-1);
            }
            users = new ArrayList<>();
        }
    }
}