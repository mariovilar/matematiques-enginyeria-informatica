package ub.edu.resources.dao;

import java.util.List;
import java.util.Optional;


public interface DAO<T> {

    /**
     * @param id unique identifier of the class T.
     * @return an optional with Data of T (for instance a customer)
     * if a customer with unique identifier <code>id</code>
     * exists, empty optional otherwise.
     * @throws Exception if any error occurs.
     */
    Optional<T> getById(String id) throws Exception;

    /**
     * @return all the customers as a List.
     * @throws Exception if any error occurs.
     */
    List<T> getAll() throws Exception;

    /**
     * @param t the data to be added.
     * @return true if the data is successfully added, false if the data already exists.
     * @throws Exception if any error occurs.
     */
    boolean add(T t) throws Exception;


    /**
     * @param t is the data to be updated.
     * @return true if data exists and is successfully updated, false otherwise.
     * @throws Exception if any error occurs.
     */
    boolean update(T t, String[] params) throws Exception;

    /**
     * @param t is the data  to be deleted.
     * @return true if data exists and is successfully deleted, false otherwise.
     * @throws Exception if any error occurs.
     */
    boolean delete(T t) throws Exception;
}
