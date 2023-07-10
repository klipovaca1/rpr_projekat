package ba.unsa.etf.rpr.business;

import ba.unsa.etf.rpr.dao.DAOFactory;
import ba.unsa.etf.rpr.domain.Lawsuit;
import ba.unsa.etf.rpr.domain.User;
import ba.unsa.etf.rpr.exceptions.CourtroomException;

import java.util.List;

/**
 * Business logic layer for managing court cases
 * The purpose of this class is to validate all court cases (or other parameters) before being sent to the database for further processing
 * Other than the things that will be mentioned in the "throws" section of every method, all methods will throw an exception if there is an SQL problem as well
 */
public class LawsuitManager {
    private static LawsuitManager instance = null;
    /**
     * Since more than one controller is going to use this class, it would be best to allow only one of them to exist at a time
     * @return an instance of this class
     */
    public static LawsuitManager getInstance() {
        if (instance == null) {
            instance = new LawsuitManager();
        }
        return instance;
    }
    /**
     * Searches for a court case with an ID alike the entered one
     * @param id the ID of the court case to retrieve from the database
     * @return the court case with a matching ID of the input ID
     * @throws CourtroomException in case there is no court case with the entered ID found
     */
    public Lawsuit getById(int id) throws CourtroomException {
        Lawsuit lawsuit = DAOFactory.getLawsuitDAO().getById(id);
        if (lawsuit == null) {
            throw new CourtroomException("Case with provided ID does not exist.");
        }
        return lawsuit;
    }
    /**
     * Adds a new court case to the database after checking for user type mismatches and duplicated user inputs
     * @param lawsuit the court case that will be added to the database if it passes all validations beforehand
     * @return the added court case with an ID assigned to it from the database
     * @throws CourtroomException in case the court case does not pass validation or its UIN is not unique
     */
    public Lawsuit addLawsuit(Lawsuit lawsuit) throws CourtroomException {
        try {
            validateUserTypes(lawsuit);
            validateCaseMembers(lawsuit);
            return DAOFactory.getLawsuitDAO().add(lawsuit);
        }
        catch (CourtroomException exception) {
            if (exception.getMessage().equals("Duplicate entry '" + lawsuit.getUin() + "' for key 'Lawsuit.uin'")) {
                throw new CourtroomException("Lawsuit with UIN of " + lawsuit.getUin() + " already exists.");
            }
            else if (exception.getMessage().contains("violated")) {
                throw new CourtroomException("Invalid verdict submitted.");
            }
            throw exception;
        }
    }
    /**
     * Corrects (updates) an existing court case in the database after checking for user type mismatches and duplicated user inputs
     * @param lawsuit the court case that will be updated if it passes all given validations
     * @throws CourtroomException in case the court case does not pass validation or its UIN is not unique
     */
    public void updateLawsuit(Lawsuit lawsuit) throws CourtroomException {
        try {
            validateUserTypes(lawsuit);
            validateCaseMembers(lawsuit);
            DAOFactory.getLawsuitDAO().update(lawsuit);
        }
        catch (CourtroomException exception) {
            if (exception.getMessage().equals("Duplicate entry '" + lawsuit.getUin() + "' for key 'Lawsuit.uin'")) {
                throw new CourtroomException("Lawsuit with UIN of " + lawsuit.getUin() + " already exists.");
            }
            throw exception;
        }
    }
    /**
     * Checks if a court case with the corresponding ID exists then deletes it under the condition it does
     * @param id the ID of the court case to delete from database
     * @throws CourtroomException in case there is no court case with the entered ID found
     */
    public void deleteLawsuit(int id) throws CourtroomException {
        getById(id);
        DAOFactory.getLawsuitDAO().delete(id);
    }
    /**
     * As the name suggests, retrieves all court cases that exists in the database
     * @return all court cases in a list container
     * @throws CourtroomException in case of SQL related problems
     */
    public List<Lawsuit> getAllLawsuits() throws CourtroomException {
        return DAOFactory.getLawsuitDAO().getAll();
    }
    /**
     * Retreives all court cases that have the user "user" participating in them
     * @param user the user with which the court cases will be searched by
     * @return all court cases that have the inputted user as a part of them - as a defendant or something even higher
     * @throws CourtroomException in case of SQL related problems
     */
    public List<Lawsuit> searchByUser(User user) throws CourtroomException {
        return DAOFactory.getLawsuitDAO().searchByUser(user);
    }
    /**
     * A helper method that checks whether court case members that are sent in were sent in their matching roles
     * @param lawsuit the court case in which the user roles are checked
     * @throws CourtroomException if at least one of them does not satisfy the given role
     */
    void validateUserTypes(Lawsuit lawsuit) throws CourtroomException {
        if (!lawsuit.getJudge().getUserType().equals("Judge")) {
            throw new CourtroomException("Provided judge is actaully not a judge - in fact, (s)he is a " + lawsuit.getLawyer().getUserType().toLowerCase() + '.');
        }
        if (!lawsuit.getLawyer().getUserType().equals("Lawyer")) {
            throw new CourtroomException("Provided lawyer is actaully not a lawyer - in fact, (s)he is a " + lawsuit.getLawyer().getUserType().toLowerCase() + '.');
        }
        if (!lawsuit.getProsecutor().getUserType().equals("Prosecutor")) {
            throw new CourtroomException("Provided prosecutor is actaully not a lawyer - in fact, (s)he is a " + lawsuit.getLawyer().getUserType().toLowerCase() + '.');
        }
    }
    /**
     * A helper method that checks whether a court case member is simultaneously a defendant and a higher role
     * @param lawsuit the court case in which the defendant is checked for multiple roles
     * @throws CourtroomException in case the defendant has two (or more) roles in the court case
     */
    void validateCaseMembers(Lawsuit lawsuit) throws CourtroomException {
        User defendant = lawsuit.getDefendant();
        if (lawsuit.getJudge().equals(defendant)) {
            throw new CourtroomException("The same person cannot be a judge and a defendant in the same case.");
        }
        if (lawsuit.getLawyer().equals(defendant)) {
            throw new CourtroomException("The same person cannot be a lawyer and a defendant in the same case.");
        }
        if (lawsuit.getProsecutor().equals(defendant)) {
            throw new CourtroomException("The same person cannot be a prosecutor and a defendant in the same case.");
        }
    }
}