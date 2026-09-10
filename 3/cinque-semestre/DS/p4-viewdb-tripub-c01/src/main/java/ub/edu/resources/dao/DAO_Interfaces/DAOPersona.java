package ub.edu.resources.dao.DAO_Interfaces;

import ub.edu.model.Persona;
import ub.edu.resources.dao.DAO;
public interface DAOPersona extends DAO<Persona> {
    Persona findPersonaByUserNameAndPassword(String usuari, String pwd) throws Exception;
    Persona findPersonaByCorreu(String correu) throws Exception;


    }
