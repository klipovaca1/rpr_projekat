package ba.unsa.etf.rpr.dao;

import ba.unsa.etf.rpr.domain.Idable;
import ba.unsa.etf.rpr.exceptions.CourtroomException;

import java.sql.*;
import java.util.*;

/**
 * The base class for other implementations of their respective DAO interfaces
 * Most methods throw a CourtroomException in case of SQL or other unexpected errors
 * @param <Type> generic type that enables other DAO interface implementations to extend a single abstract class
 * @author Kemal Lipovača
 */
public abstract class AbstractDAO<Type extends Idable> implements DAO<Type> {
    private static Connection connection = null;
    private final String tableName;
    /**
     * The constructor initializes a string that is going to be used for various kinds of SQL queries
     * This constructor also creates the connection between the used device and the database with all stored information
     * @param tableName the name of the table that is going to be accessed in MySQL
     */
    public AbstractDAO(String tableName) {
        this.tableName = tableName;
        createConnection();
    }
    /**
     * In case of connection lost whereever the connection to the database is to be used this method is called to regain the connection to the database again
     * @return the connection to the database
     */
    public Connection getConnection() {
        createConnection();
        return connection;
    }
    /**
     * This method is used to transform an item from a MySQL table row form to the appropriate Java Object form
     * @param resultSet the item as a MySQL table row
     * @return the item as a Java Object
     */
    public abstract Type tableRowToObject(ResultSet resultSet) throws CourtroomException;
    /**
     * This method is used to transform an item from a Java Object form to the appropriate MySQL table row form
     * Linked hash maps are used to preserve the order of columns in the table row for the prepared statements
     * @param item the item as a Java Object
     * @return the item as a MySQL table row
     */
    public abstract Map<String,Object> objectToTableRow(Type item);
    public Type getById(int id) throws CourtroomException {
        return searchSingleResult("SELECT * FROM " + tableName + " WHERE id = ?", new Object[]{id});
    }
    public Type add(Type item) throws CourtroomException {
        Map<String,Object> tableRow = objectToTableRow(item);
        Map.Entry<String,String> insertColumns = getInsertColumns(tableRow);
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ").append(tableName).append(" (").append(insertColumns.getKey()).append(") ").append("VALUES (").append(insertColumns.getValue()).append(")");
        try {
            PreparedStatement statement = prepareStatement(query.toString(), tableRow);
            try (statement) {
                statement.executeUpdate();
                ResultSet resultSet = statement.getGeneratedKeys();
                resultSet.next();
                item.setId(resultSet.getInt(1));
            }
        }
        catch (SQLException exception) {
            throw new CourtroomException(exception.getMessage(), exception);
        }
        return item;
    }
    public void update(Type item) throws CourtroomException {
        Map<String,Object> tableRow = objectToTableRow(item);
        String updateColumns = getUpdateColumns(tableRow);
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(tableName).append(" SET ").append(updateColumns).append(" WHERE id = ?");
        try {
            PreparedStatement statement = prepareStatement(query.toString(), tableRow);
            statement.setObject(tableRow.size(), item.getId());
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException exception) {
            throw new CourtroomException(exception.getMessage(), exception);
        }
    }
    public void delete(int id) throws CourtroomException {
        String query = "DELETE FROM " + tableName + " WHERE id = ?";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            statement.setObject(1, id);
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException exception) {
            throw new CourtroomException(exception.getMessage(), exception);
        }
    }
    public List<Type> getAll() throws CourtroomException {
        return searchResult("SELECT * FROM " + tableName, null);
    }
    /**
     * A helper method that attempts to create a connection with the database using the information provided in the applications.properties file
     * This method closes the connection at application shutdown
     */
    private static void createConnection() {
        try {
            Properties properties = new Properties();
            properties.load(ClassLoader.getSystemResource("properties/application.properties").openStream());
            String url = properties.getProperty("database.connection_string");
            String username = properties.getProperty("database.username");
            String password = properties.getProperty("database.password");
            connection = DriverManager.getConnection(url, username, password);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        finally {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    connection.close();
                }
                catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }));
        }
    }
    /**
     * A helper method that is used to prepare an executeUpdate statement based off of the query and table row columns
     * @param query the query that is going to be sent
     * @param tableRow the table row whose columns will be used in the statement
     * @return the statement fully prepared for executing
     */
    private PreparedStatement prepareStatement(String query, Map<String,Object> tableRow) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        int counter = 0;
        for (Map.Entry<String,Object> entry : tableRow.entrySet()) {
            if (counter != 0) {
                statement.setObject(counter, entry.getValue());
            }
            counter = counter + 1;
        }
        return statement;
    }
    /**
     * A helper method that prepares the needed columns and questionmarks for the add statement
     * @param tableRow the table row on which the result will be based on
     * @return a pair of strings which represent the statement columns and appropriate number of questionmarks
     */
    private Map.Entry<String,String> getInsertColumns(Map<String,Object> tableRow) {
        StringBuilder columnNames = new StringBuilder();
        StringBuilder questionmarks = new StringBuilder();
        int counter = 0;
        for (Map.Entry<String, Object> entry : tableRow.entrySet()) {
            if (counter != 0) {
                columnNames.append(entry.getKey());
                questionmarks.append("?");
                if (counter != tableRow.size() - 1) {
                    columnNames.append(", ");
                    questionmarks.append(", ");
                }
            }
            counter = counter + 1;
        }
        return new AbstractMap.SimpleEntry<>(columnNames.toString(), questionmarks.toString());
    }
    /**
     * A helper method that prepares the columns that will be used in the update statement
     * @param tableRow the table row on which the result will be based on
     * @return a string which represents the columns that will be updated
     */
    private String getUpdateColumns(Map<String,Object> tableRow) {
        StringBuilder semiQuery = new StringBuilder();
        int counter = 0;
        for (Map.Entry<String, Object> entry: tableRow.entrySet()) {
            if (counter != 0) {
                semiQuery.append(entry.getKey()).append(" = ?");
                if (counter != tableRow.size() - 1) {
                    semiQuery.append(", ");
                }
            }
            counter = counter + 1;
        }
        return semiQuery.toString();
    }
    /**
     * A method that utilizes the more general searchResult method for retrieving a unique search result
     * @param query the query that will be executed
     * @param parameters an array of parameter values used to fill up the statement
     * @return an item that is the result of the given query or null for no match found
     */
    public Type searchSingleResult(String query, Object[] parameters) throws CourtroomException {
        List<Type> result = searchResult(query, parameters);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }
    /**
     * A method that retrieves all eligible items depending on the query limitations
     * @param query the query that will be executed
     * @param parameters an array of parameter values used to fill up the statement
     * @return a list container of all items that fulfill the required query restrictions
     */
    public List<Type> searchResult(String query, Object[] parameters) throws CourtroomException {
        List<Type> resultList = new ArrayList<>();
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i = i + 1) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }
            try {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    resultList.add(tableRowToObject(resultSet));
                }
            }
            finally {
                statement.close();
            }
        }
        catch (SQLException exception) {
            throw new CourtroomException(exception.getMessage(), exception);
        }
        return resultList;
    }
}