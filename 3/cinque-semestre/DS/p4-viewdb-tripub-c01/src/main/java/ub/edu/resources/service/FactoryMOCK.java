package ub.edu.resources.service;

import ub.edu.resources.dao.DAO_Interfaces.*;
import ub.edu.resources.dao.DB.DAO_Implementations.DAOTransportDB;
import ub.edu.resources.dao.DB.DAO_Implementations.DAOTransport_PossiblePerAl_TramDB;
import ub.edu.resources.dao.MOCK.*;


public class FactoryMOCK implements AbstractFactoryData {

    @Override
    public DAOComarca createDAOComarca() {
        return new DAOComarcaMOCK();
    }
    @Override
    public DAORelacioComarcaRuta createDAORelacioRutaComarca() {
        return new DAORelacioComarcaRutaMOCK();
    }
    @Override
    public DAORuta createDAORuta() {
        return new DAORutaMOCK();
    }
    @Override
    public DAOPersona createDAOPersona() {
        return new DAOPersonaMOCK();
    }



    @Override
    public DAOAllotjament createDAOAllotjament() {
        return null;
    }

    @Override
    public DAOAPeu createDAOAPeu() {
        return null;
    }

    @Override
    public DAOBicicleta createDAOBicicleta() {
        return null;
    }

    @Override
    public DAOCamping createDAOCamping() {
        return null;
    }

    @Override
    public DAOCasaRural createDAOCasaRural() {
        return null;
    }

    @Override
    public DAOCotxe createDAOCotxe() {
        return null;
    }

    @Override
    public DAOEtapa createDAOEtapa() {
        return null;
    }

    @Override
    public DAOGrupFormatPerPersones createDAOGrupFormatPerPersones() {
        return null;
    }

    @Override
    public DAOGrup createDAOGrup() {
        return null;
    }

    @Override
    public DAOHotel createDAOHotel() {
        return null;
    }

    @Override
    public DAOLocalitat createDAOLocalitat() {
        return null;
    }

    @Override
    public DAOOpinio createDAOOpinio() {
        return null;
    }

    @Override
    public DAORelacioComarcaRuta_2 createDAORelacioRutaComarca_2() {
        return null;
    }

    @Override
    public DAOReserva createDAOReserva() {
        return null;
    }

    @Override
    public DAOPersonesRealitzaRuta createDAOPersonaRealitzaRuta() {
        return null;
    }

    @Override
    public DAOPuntDePas createDAOPuntDePas() {
        return null;
    }

    @Override
    public DAORefugi createDAORefugi() {
        return null;
    }

    @Override
    public DAOTipusOpinio createDAOTipusOpinio() {
        return null;
    }

    @Override
    public DAOTipusVot createDAOTipusVot() {
        return null;
    }

    @Override
    public DAOTram createDAOTram() {
        return null;
    }

    @Override
    public DAOTramEtapa createDAOTramEtapa() {
        return null;
    }

    @Override
    public DAOTramTrack createDAOTramTrack() {
        return null;
    }

    @Override
    public DAOTransport_PossiblePerAl_TramDB createDAOTransportPosibleTram() {
        return null;
    }

    @Override
    public DAOTransport createDAOTransport() {
        return null;
    }

    @Override
    public DAOTripUB createDAOTripUB() {
        return null;
    }

    @Override
    public DAOVot createDAOVot() {
        return null;
    }



}
