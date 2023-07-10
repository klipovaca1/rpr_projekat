package ba.unsa.etf.rpr.dao;

import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;

/**
 * An interface that implements the base DAO interface and provides additional methods to be implemented
 */
public interface UserDAO extends DAO<User> {
    /**
     * The method that retrieves an item from the database by matching credentials
     * @param name the name of the user
     * @param password the password of the user
     * @return the user found in the database with matching credentials or null for no match found
     * @throws CourtroomException in case of SQL or other unexpected errors
     */
    User getByCredentials(String name, String password) throws CourtroomException;
}