package ub.edu.resources.dao.DAO_Interfaces;

import ub.edu.resources.dao.DAO;
import ub.edu.resources.dao.Parell;

import java.util.List;

public interface DAOGrupFormatPerPersones extends DAO<Parell<String,Integer>> {
    public List<Parell<String,Integer>> getByIds_AND_OR (String correu_persona, String AND_OR,  Integer id_grup);
    public List<Parell<String,Integer>> getByCorreu_Persona (String correu_persona);
    public List<Parell<String,Integer>> getById_Grup (Integer id);

    // Metodes especifics si es el cas
}
