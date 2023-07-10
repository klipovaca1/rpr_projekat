package ba.unsa.etf.rpr.dao;

import ba.unsa.etf.rpr.exceptions.CourtroomException;

import java.util.List;

/**
 * The base interface for other DAO interfaces
 * All methods throw a CourtroomException in case of SQL or other unexpected errors
 * @param <Type> generic type that enables other DAO interfaces to extend a single interface
 * @author Kemal Lipovača
 */
public interface DAO<Type> {
    /**
     * The method that retrieves an item from the database by matching IDs
     * @param id the ID of the item
     * @return the item found in the database with the matching ID or null for no match found
     */
    Type getById(int id) throws CourtroomException;
    /**
     * The method that adds an item to the database
     * @param item the item that is to be added
     * @return the item used as the parameter but with an assigned ID to it
     */
    Type add(Type item) throws CourtroomException;
    /**
     * The method that updates an existing item in the database
     * @param item the item that is to be updated
     */
    void update(Type item) throws CourtroomException;
    /**
     * The method that deletes an item from the database with the matching ID
     * @param id the ID of the item
     */
    void delete(int id) throws CourtroomException;
    /**
     * The method that retrieves all items from the database of the same type
     * @return a list container of all the items acquired
     */
    List<Type> getAll() throws CourtroomException;
}