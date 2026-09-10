package ub.edu.resources.dao.DAO_Interfaces;

import ub.edu.resources.dao.DAO;
import ub.edu.resources.dao.Parell;

import java.util.List;

public interface DAOTransportPossiblePerAlTram extends DAO<Parell<Integer,Integer>> {
    public List<Parell<Integer,Integer>> getByIds_AND_OR (Integer id_transport, String AND_OR, Integer id_tram);
    public List<Parell<Integer,Integer>> getById_Transport (Integer id);
    public List<Parell<Integer,Integer>> getById_Tram (Integer id);

    // Metodes especifics si es el cas
}
