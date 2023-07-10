package ba.unsa.etf.rpr.dao;

import ba.unsa.etf.rpr.domain.Lawsuit;
import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;

import java.util.List;

/**
 * An interface that implements the base DAO interface and provides additional methods to be implemented
 */
public interface LawsuitDAO extends DAO<Lawsuit> {
    /**
     * The method that retrieves all court cases from the database in which the user took participation
     * @param user the user with which the court cases will be searched by
     * @return a list container of all court cases acquired
     * @throws CourtroomException in case of SQL or other unexpected errors
     */
    List<Lawsuit> searchByUser(User user) throws CourtroomException;
}