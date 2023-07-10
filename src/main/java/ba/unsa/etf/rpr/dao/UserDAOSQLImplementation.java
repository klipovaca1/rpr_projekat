package ba.unsa.etf.rpr.dao;

import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The class that bridges the user information stored in the database and code that uses the stored user information
 * This class represents the implementation of the UserDAO interface using MySQL
 */
public class UserDAOSQLImplementation extends AbstractDAO<User> implements UserDAO {
    private static UserDAOSQLImplementation instance = null;
    /**
     * "Users" is the name of the table in the database because "User" is a reserved keyword in MySQL
     */
    public UserDAOSQLImplementation() {
        super("Users");
    }
    /**
     * This method represents the implementation of the Singleton Design Pattern in this application
     * Since this class is going to be used in multiple different places it would be wise to allow only the existence of one instance of this class at a given time
     * @return an instance of this class
     */
    public static UserDAOSQLImplementation getInstance() {
        if (instance == null) {
            instance = new UserDAOSQLImplementation();
        }
        return instance;
    }

    @Override
    public User tableRowToObject(ResultSet resultSet) throws CourtroomException {
        User user = new User();
        try {
            user.setId(resultSet.getInt("id"));
            user.setName(resultSet.getString("name"));
            user.setPassword(resultSet.getString("password"));
            user.setUserType(resultSet.getString("user_type"));
        }
        catch (SQLException error) {
            throw new CourtroomException(error.getMessage(), error);
        }
        return user;
    }
    @Override
    public Map<String,Object> objectToTableRow(User user) {
        Map<String, Object> tableRow = new LinkedHashMap<>();
        tableRow.put("id", user.getId());
        tableRow.put("name", user.getName());
        tableRow.put("password", user.getPassword());
        tableRow.put("user_type", user.getUserType());
        return tableRow;
    }
    @Override
    public User getByCredentials(String name, String password) throws CourtroomException {
        return searchSingleResult("SELECT * FROM Users WHERE name = ? AND password = ?", new Object[]{name, password});
    }
}