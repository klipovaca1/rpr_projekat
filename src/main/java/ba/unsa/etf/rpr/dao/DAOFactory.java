package ba.unsa.etf.rpr.dao;

/**
 * This class represents the implementation of the Factory Design Pattern in this application
 */
public class DAOFactory {
    private static final LawsuitDAO lawsuitDAO = LawsuitDAOSQLImplementation.getInstance();
    private static final UserDAO userDAO = UserDAOSQLImplementation.getInstance();

    private DAOFactory() {

    }

    public static LawsuitDAO getLawsuitDAO() {
        return lawsuitDAO;
    }
    public static UserDAO getUserDAO() {
        return userDAO;
    }
}