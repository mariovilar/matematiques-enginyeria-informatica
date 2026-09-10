package ub.edu.resources.dao.DAO_Interfaces;

import ub.edu.resources.dao.DAO;
import ub.edu.resources.dao.Parell;

import java.util.List;

public interface DAORelacioComarcaRuta_2 extends DAO<Parell<Integer,Integer>> {
    public List<Parell<Integer,Integer>> getByIds_AND_OR (Integer id_comarca, String AND_OR, Integer id_ruta);
    public List<Parell<Integer,Integer>> getById_Comarca (Integer id);
    public List<Parell<Integer,Integer>> getById_Ruta (Integer id);
        // Metodes especifics si es el cas

}
