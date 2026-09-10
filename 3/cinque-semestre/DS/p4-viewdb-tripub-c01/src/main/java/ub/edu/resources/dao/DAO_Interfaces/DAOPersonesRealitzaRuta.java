package ub.edu.resources.dao.DAO_Interfaces;

import ub.edu.resources.dao.DAO;
import ub.edu.resources.dao.Parell;

import java.util.List;

public interface DAOPersonesRealitzaRuta extends DAO<Parell<Integer,String>> {
    public List<Parell<Integer,String>> getByIds_AND_OR (Integer id_ruta, String AND_OR, String correu_persona);
    public List<Parell<Integer,String>> getById_Ruta (Integer id);
    public List<Parell<Integer,String>> getByCorreu_Persona (String correu_persona);

    // Metodes especifics si es el cas
}
