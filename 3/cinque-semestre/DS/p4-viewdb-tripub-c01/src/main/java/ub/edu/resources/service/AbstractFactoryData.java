package ub.edu.resources.service;

import ub.edu.resources.dao.DAO_Interfaces.*;
import ub.edu.resources.dao.DB.DAO_Implementations.DAOTransport_PossiblePerAl_TramDB;

public interface AbstractFactoryData {
     DAOAllotjament createDAOAllotjament();
     DAOAPeu createDAOAPeu();
     DAOBicicleta createDAOBicicleta();
     DAOCamping createDAOCamping();
     DAOCasaRural createDAOCasaRural();
     DAOComarca createDAOComarca();
     DAOCotxe createDAOCotxe();
     DAOEtapa createDAOEtapa();
     DAOGrupFormatPerPersones createDAOGrupFormatPerPersones();
     DAOGrup createDAOGrup();
     DAOHotel createDAOHotel();
     DAOLocalitat createDAOLocalitat();
     DAOOpinio createDAOOpinio();
     DAOPersona createDAOPersona();
     DAOPersonesRealitzaRuta createDAOPersonaRealitzaRuta();
     DAOPuntDePas createDAOPuntDePas();
     DAORefugi createDAORefugi();
     DAORelacioComarcaRuta createDAORelacioRutaComarca();
     DAORelacioComarcaRuta_2 createDAORelacioRutaComarca_2();
     DAOReserva createDAOReserva();
     DAORuta createDAORuta();
     DAOTipusOpinio createDAOTipusOpinio();
     DAOTipusVot createDAOTipusVot();
     DAOTram createDAOTram();
     DAOTramEtapa createDAOTramEtapa();
     DAOTramTrack createDAOTramTrack();
     DAOTransport_PossiblePerAl_TramDB createDAOTransportPosibleTram();
     DAOTransport createDAOTransport();
     DAOTripUB createDAOTripUB();
     DAOVot createDAOVot();
}
