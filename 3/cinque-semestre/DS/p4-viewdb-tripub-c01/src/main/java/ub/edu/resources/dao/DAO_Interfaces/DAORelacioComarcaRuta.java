package ub.edu.resources.dao.DAO_Interfaces;

import ub.edu.resources.dao.Parell;
import ub.edu.resources.dao.DAO;

import java.util.List;

public interface DAORelacioComarcaRuta extends DAO<Parell<String, String>> {

    List<String> getComarquesByRuta(String idRuta) throws Exception;
    List<String> getRutesByComarca(String idComarca) throws Exception;

}
