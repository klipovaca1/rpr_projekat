package ba.unsa.etf.rpr.business;

import ba.unsa.etf.rpr.dao.DAOFactory;
import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Business logic layer for managing users
 * The purpose of this class is to validate all users (or other parameters) before being sent to the database for further processing
 * Other than the things that will be mentioned in the "throws" section of every method, all methods will throw an exception if there is an SQL problem as well
 */
public class UserManager {
    private static UserManager instance = null;
    /**
     * Since more than one controller is going to use this class, it would be best to allow only one of them to exist at a time
     * @return an instance of this class
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    /**
     * Searches for a user whose ID matches the one corresponding to the parameter "id"
     * @param id the ID of the user to retrieve from the database
     * @return the user with an ID matching the entered ID
     * @throws CourtroomException in case no users in the database have the inputted ID tied to them
     */
    public User getById(int id) throws CourtroomException {
        User user = DAOFactory.getUserDAO().getById(id);
        if (user == null) {
            throw new CourtroomException("User with selected ID not found.");
        }
        return user;
    }
    /**
     * After creating a user by giving him a password which depends on the date of calling this method, adds a user to the database after passing all validations
     * @param name the name of the user we wish to add to the database
     * @param userType the highest role the user can have in a court case
     * @return the user with an ID acquired from the database after creation
     * @throws CourtroomException in case a user already exists with the sent parameter "name"
     */
    public User addUser(String name, String userType) throws CourtroomException {
        try {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
            String password = formatter.format(date);
            validateCredentials(name, password);
            return DAOFactory.getUserDAO().add(new User(13, name, password, userType));
        }
        catch (CourtroomException exception) {
            if (exception.getMessage().equals("Duplicate entry '" + name + "' for key 'Users.name'")) {
                throw new CourtroomException("User with name \"" + name + "\" already exists.");
            }
            else if (exception.getMessage().contains("violated")) {
                throw new CourtroomException("Invalid user type submitted.");
            }
            throw exception;
        }
    }
    /**
     * Modifies the name and/or password of the user sent through the parameter
     * @param user the user whose credentials are going to potencially be modified
     * @throws CourtroomException in case new user credentials do not pass validation
     */
    public void updateCredentials(User user) throws CourtroomException {
        try {
            validateCredentials(user.getName(), user.getPassword());
            DAOFactory.getUserDAO().update(user);
        }
        catch (CourtroomException exception) {
            if (exception.getMessage().equals("Duplicate entry '" + user.getName() + "' for key 'Users.name'")) {
                throw new CourtroomException("User with name \"" + user.getName() + "\" already exists.");
            }
            throw exception;
        }
    }
    /**
     * Checks if a user with the corresponding ID exists then deletes it under the assumption it does
     * @param id the ID of the user to delete from database
     * @return the user that was deleted from the database
     * @throws CourtroomException in case there is no user with the entered ID found or the user requested for deletion is still part of at least one case in the database
     */
    public User deleteUser(int id) throws CourtroomException {
        try {
            User user = getById(id);
            DAOFactory.getUserDAO().delete(id);
            return user;
        }
        catch (CourtroomException exception) {
            if (exception.getMessage().contains("foreign key")) {
                throw new CourtroomException("Selected user is part of at least one case - deletion of cases in which the user participated is a mandatory requirement for user deletion.");
            }
            throw exception;
        }
    }
    /**
     * As the name suggests, retrieves all users in the database
     * @return all users in a list container
     * @throws CourtroomException in case no users are found in the database
     */
    public List<User> getAllUsers() throws CourtroomException {
        List<User> users = DAOFactory.getUserDAO().getAll();
        if (users.isEmpty()) {
            throw new CourtroomException("Database is empty - no users found.");
        }
        return users;
    }
    /**
     * Checks if an admin is logging in - if not validates the received parameters and searches for the user with matching parameters in the database
     * @param name the name of the user logging in
     * @param password the password of the user logging in
     * @return the user that was logging in
     * @throws CourtroomException if parameters do not satisfy the validation process or the provided credentials are not matching any user credentials in the database
     */
    public User searchByCredentials(String name, String password) throws CourtroomException {
        try {
            if (specialCheck(name, password)) {
                return null;
            }
            else {
                validateCredentials(name, password);
                User user = DAOFactory.getUserDAO().getByCredentials(name, password);
                if (user == null) {
                    throw new CourtroomException("No matches for provided name and password in database found.");
                }
                return user;
            }
        }
        catch (CourtroomException exception) {
            throw new CourtroomException("Unsuccessful login|" + exception.getMessage());
        }
    }

    private boolean specialCheck(String name, String password) {
        return name.equalsIgnoreCase("admin") && password.equalsIgnoreCase("password");
    }
    /**
     * A helper method that validates the format of name and password of a user
     * @param name the name whose format is validated
     * @param password the password whose format is being validated
     * @throws CourtroomException in case one of the parameters do not satisfy the required format
     */
    void validateCredentials(String name, String password) throws CourtroomException {
        if (name.split(" ").length < 2) {
            throw new CourtroomException("Name must consist of at least 2 separate words.");
        }
        if (name.length() > 64) {
            throw new CourtroomException("Name mustn't be longer than 64 characters.");
        }
        if (password.length() < 8 || password.length() > 64) {
            throw new CourtroomException("Password must be between 8 and 64 characters long.");
        }
    }
}