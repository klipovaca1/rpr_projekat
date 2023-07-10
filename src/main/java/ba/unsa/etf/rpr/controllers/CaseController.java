package ba.unsa.etf.rpr.controllers;

import ba.unsa.etf.rpr.business.LawsuitManager;
import ba.unsa.etf.rpr.business.UserManager;
import ba.unsa.etf.rpr.domain.Lawsuit;
import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * The JavaFX controller for adding and editing court cases
 */
public class CaseController extends WindowManager {
    private final LawsuitManager lawsuitManager = LawsuitManager.getInstance();
    private final UserManager userManager = UserManager.getInstance();
    private final User user;
    private final List<User> users;
    private final List<User> judges;
    private final List<User> lawyers;
    private final List<User> prosecutors;
    public RadioButton addButton;
    public RadioButton editButton;
    public TextField idField;
    public TextField titleField;
    public TextField uinField;
    public ComboBox<String> judgeBox;
    public ComboBox<String> defendantBox;
    public ComboBox<String> lawyerBox;
    public ComboBox<String> prosecutorBox;
    public ChoiceBox<String> verdictField;
    public Button judgeButton;
    public Button defendantButton;
    public Button lawyerButton;
    public Button closeButton;
    /**
     * A constructor that initializes the user that has invoked the opening of this window and the list of all users in the database
     * @param user the user that requested the opening of this window
     * @param users the list container of all users in the database
     */
    public CaseController(User user, List<User> users) {
        this.user = user;
        this.users = users;
        judges = users.stream().filter(person -> person.getUserType().equals("Judge")).collect(toList());
        lawyers = users.stream().filter(person -> person.getUserType().equals("Lawyer")).collect(toList());
        prosecutors = users.stream().filter(person -> person.getUserType().equals("Prosecutor")).collect(toList());
    }
    /**
     * The method that gets called right before the opening of this window
     * Its purpose is to initialize all and restrict some of the JavaFX components shown in the created window
     * In case of an error an alert will pop up showing us the error in finer details
     */
    @FXML
    public void initialize() {
        if (user != null) {
            judgeBox.setValue(user.getName());
            judgeBox.setDisable(true);
        }
        idField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                idField.setText(newValue.replaceAll("\\D", ""));
            }
        });
        idField.focusedProperty().addListener(((observableValue, oldValue, newValue) -> {
            String idFieldText = idField.getText().trim();
            if (editButton.isSelected() && !idFieldText.isEmpty() && !newValue) {
                try {
                    Lawsuit lawsuit = lawsuitManager.getById(Integer.parseInt(idFieldText));
                    if (user != null && !lawsuit.getJudge().equals(user)) {
                        throw new CourtroomException("You cannot edit cases which you are not the judge of.");
                    }
                    if (!lawsuit.getVerdict().equals("Undecided")) {
                        throw new CourtroomException("Case verdict already made (case closed).");
                    }
                    titleField.setText(lawsuit.getTitle());
                    uinField.setText(String.valueOf(lawsuit.getUin()));
                    if (user == null) {
                        judgeBox.setValue(lawsuit.getJudge().getName());
                    }
                    defendantBox.setValue(lawsuit.getDefendant().getName());
                    lawyerBox.setValue(lawsuit.getLawyer().getName());
                    prosecutorBox.setValue(lawsuit.getProsecutor().getName());
                    verdictField.setValue("Undecided");
                }
                catch (CourtroomException exception) {
                    openAlert(Alert.AlertType.ERROR, "Unable to edit case|" + exception.getMessage());
                }
            }
        }));
        uinField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                uinField.setText(newValue.replaceAll("\\D", ""));
            }
        });
        judgeBox.setItems(FXCollections.observableList(judges.stream().map(User::getName).collect(toList())));
        defendantBox.setItems(FXCollections.observableList(users.stream().map(User::getName).collect(toList())));
        lawyerBox.setItems(FXCollections.observableList(lawyers.stream().map(User::getName).collect(toList())));
        prosecutorBox.setItems(FXCollections.observableList(prosecutors.stream().map(User::getName).collect(toList())));
        emptyTextFields();
    }
    /**
     * A method that sets up all JavaFX components for adding a new court case
     */
    @FXML
    public void addRadioButton() {
        idField.setEditable(false);
        prosecutorBox.setDisable(false);
        idField.setText("42");
        emptyTextFields();
    }
    /**
     * Method that sets up all JavaFX components for editing an existing court case
     */
    @FXML
    public void editRadioButton() {
        idField.setEditable(true);
        prosecutorBox.setDisable(true);
        idField.setText("");
        emptyTextFields();
    }
    /**
     * The method that is responsible for adding a new user, depending on which of the "Add" buttons was pressed
     * In case of successfully adding a new user to the database, an alert will be shown confirming the success in the addition of the user
     * In case of an error an alert will pop up showing us the error in finer details
     * @param actionEvent parameter that will be cast to a button for the purpose of assigning an appropriate user type
     */
    @FXML
    public void addUser(ActionEvent actionEvent) {
        Button pressedButton = (Button)actionEvent.getSource();
        String name;
        String userType;
        if (judgeButton.equals(pressedButton)) {
            name = judgeBox.getValue().trim();
            userType = "Judge";
        }
        else if (defendantButton.equals(pressedButton)) {
            name = defendantBox.getValue().trim();
            userType = "Defendant";
        }
        else if (lawyerButton.equals(pressedButton)) {
            name = lawyerBox.getValue().trim();
            userType = "Lawyer";
        }
        else {
            name = prosecutorBox.getValue().trim();
            userType = "Prosecutor";
        }
        try {
            User newUser = userManager.addUser(name, userType);
            switch (userType) {
                case "Judge" -> {
                    judges.add(newUser);
                    judgeBox.getItems().add(newUser.getName());
                }
                case "Lawyer" -> {
                    lawyers.add(newUser);
                    lawyerBox.getItems().add(newUser.getName());
                }
                case "Prosecutor" -> {
                    prosecutors.add(newUser);
                    prosecutorBox.getItems().add(newUser.getName());
                }
            }
            users.add(newUser);
            defendantBox.getItems().add(newUser.getName());
            openAlert(Alert.AlertType.INFORMATION, "Addition success|Succesfully added new " + userType.toLowerCase() + " with ID of " + newUser.getId() + " to database.");
        }
        catch (CourtroomException exception) {
            openAlert(Alert.AlertType.ERROR, "Addition failure|" + exception.getMessage());
        }
    }
    /**
     * The method that is responsible for adding/editing a court case, depending on the selected radio button
     * In case of no errors, an alert will be shown that will confirm the successful addition/edit of a court case with the ID of the corresponding case
     * In case of any problems, an alert will pop up that will explain the reason of the error as well as uncover some of its extra details
     */
    @FXML
    public void confirmCase() {
        try {
            if (!addButton.isSelected() && !editButton.isSelected()) {
                throw new CourtroomException("Please select one of the two radio buttons");
            }
            String idFieldText = idField.getText().trim();
            String titleFieldText = titleField.getText().trim();
            String uinFieldText = uinField.getText().trim();
            String judgeBoxText = judgeBox.getValue().trim();
            String defendantBoxText = defendantBox.getValue().trim();
            String lawyerBoxText = lawyerBox.getValue().trim();
            String prosecutorBoxText = prosecutorBox.getValue().trim();
            String verdictFieldText = verdictField.getValue();
            if (idFieldText.isEmpty()) {
                throw new CourtroomException("ID field left empty.");
            }
            if (titleFieldText.isEmpty()) {
                throw new CourtroomException("Title field left empty.");
            }
            if (uinFieldText.isEmpty()) {
                throw new CourtroomException("UIN field left empty");
            }
            if (judgeBoxText.isEmpty()) {
                throw new CourtroomException("Judge field left empty");
            }
            if (defendantBoxText.isEmpty()) {
                throw new CourtroomException("Defendant field left empty");
            }
            if (lawyerBoxText.isEmpty()) {
                throw new CourtroomException("Lawyer field left empty");
            }
            if (prosecutorBoxText.isEmpty()) {
                throw new CourtroomException("Prosecutor field left empty");
            }
            if (verdictFieldText == null) {
                throw new CourtroomException("Please pick a verdict from the drop-down list");
            }
            User judge = judges.stream().filter(person -> person.getName().equalsIgnoreCase(judgeBoxText)).findFirst().orElseThrow(() -> new CourtroomException("Judge with entered name does not exist."));
            User defendant = users.stream().filter(person -> person.getName().equalsIgnoreCase(defendantBoxText)).findFirst().orElseThrow(() -> new CourtroomException("Defendant with entered name does not exist."));
            User lawyer = lawyers.stream().filter(person -> person.getName().equalsIgnoreCase(lawyerBoxText)).findFirst().orElseThrow(() -> new CourtroomException("Lawyer with entered name does not exist."));
            User prosecutor = prosecutors.stream().filter(person -> person.getName().equalsIgnoreCase(prosecutorBoxText)).findFirst().orElseThrow(() -> new CourtroomException("Prosecutor with entered name does not exist."));
            Lawsuit lawsuit = new Lawsuit(Integer.parseInt(idFieldText), titleFieldText, Long.parseLong(uinFieldText), judge, defendant, lawyer, prosecutor, verdictFieldText);
            if (addButton.isSelected()) {
                lawsuit = lawsuitManager.addLawsuit(lawsuit);
                openAlert(Alert.AlertType.INFORMATION, "Fiddling accomplished|Successfully added new case with ID of " + lawsuit.getId());
                emptyTextFields();
            }
            else {
                lawsuitManager.updateLawsuit(lawsuit);
                openAlert(Alert.AlertType.INFORMATION, "Fiddling accomplished|Successfully updated case with ID of " + lawsuit.getId());
                emptyTextFields();
            }
        }
        catch (CourtroomException exception) {
            openAlert(Alert.AlertType.ERROR, "Encountered a problem during case fiddling|" + exception.getMessage());
        }
    }
    /**
     * This method calls its parent's closeWindow method, which does exactly what the method name suggests
     */
    @FXML
    public void closeScreen() {
        closeWindow(closeButton);
    }
    /**
     * A helper method that, when called, resets the contents of (almost) all fields and comboboxes in the shown window
     */
    private void emptyTextFields() {
        titleField.setText("");
        uinField.setText("");
        if (user == null) {
            judgeBox.setValue("");
        }
        defendantBox.setValue("");
        lawyerBox.setValue("");
        prosecutorBox.setValue("");
        verdictField.setValue(null);
    }
}