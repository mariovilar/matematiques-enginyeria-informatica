package ub.edu.resources.service;

import ub.edu.resources.dao.DAO_Interfaces.*;
import ub.edu.resources.dao.DB.DAO_Implementations.*;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class FactoryDB implements AbstractFactoryData {

    Connection connection;


    public FactoryDB() {
        this.connection = connection_open();
    }


    @Override
    public DAOAllotjament createDAOAllotjament() {
        return new DAOAllotjamentDB(this.connection);
    }
    @Override
    public DAOAPeu createDAOAPeu() {
        return new DAOAPeuDB(this.connection);
    }
    @Override
    public DAOBicicleta createDAOBicicleta() {
        return new DAOBicicletaDB(this.connection);
    }
    @Override
    public DAOCamping createDAOCamping() {
        return new DAOCampingDB(this.connection);
    }
    @Override
    public DAOCasaRural createDAOCasaRural() {
        return new DAOCasaRuralDB(this.connection);
    }
    @Override
    public DAOComarca createDAOComarca() {
        return new DAOComarcaDB(this.connection);
    }
    @Override
    public DAOCotxe createDAOCotxe() {
        return new DAOCotxeDB(this.connection);
    }
    @Override
    public DAOEtapa createDAOEtapa() {
        return new DAOEtapaDB(this.connection);
    }
    @Override
    public DAOGrupFormatPerPersones createDAOGrupFormatPerPersones() {
        return new DAOGrup_FormatPer_PersonesDB(this.connection);
    }
    @Override
    public DAOGrup createDAOGrup() {
        return new DAOGrupDB(this.connection);
    }
    @Override
    public DAOHotel createDAOHotel() {
        return new DAOHotelDB(this.connection);
    }
    @Override
    public DAOLocalitat createDAOLocalitat() {
        return new DAOLocalitatDB(this.connection);
    }
    @Override
    public DAOOpinio createDAOOpinio() {
        return new DAOOpinioDB(this.connection);
    }
    @Override
    public DAOPersona createDAOPersona() {
        return new DAOPersonaDB(this.connection);
    }
    @Override
    public DAOPersonesRealitzaRuta createDAOPersonaRealitzaRuta() {
        return new DAOPersones_Realitza_RutaDB(this.connection);
    }
    @Override
    public DAOPuntDePas createDAOPuntDePas() {
        return new DAOPuntDePasDB(this.connection);
    }
    @Override
    public DAORefugi createDAORefugi() {
        return new DAORefugiDB(this.connection);
    }
    @Override
    public DAORelacioComarcaRuta createDAORelacioRutaComarca() {
        /*este es el del MOCK*/
        return null;
    }
    @Override
    public DAORelacioComarcaRuta_2 createDAORelacioRutaComarca_2() {
        return new DAORelacio_Comarca_RutaDB(this.connection);
    }
    @Override
    public DAOReserva createDAOReserva() {
        return new DAOReservaDB(this.connection);
    }
    @Override
    public DAORuta createDAORuta() {
        return new DAORutaDB(this.connection);
    }
    @Override
    public DAOTipusOpinio createDAOTipusOpinio() {
        return new DAOTipusOpinioDB(this.connection);
    }
    @Override
    public DAOTipusVot createDAOTipusVot() {
        return new DAOTipusVotDB(this.connection);
    }
    @Override
    public DAOTram createDAOTram() {
        return new DAOTramDB(this.connection);
    }
    @Override
    public DAOTramEtapa createDAOTramEtapa() {
        return new DAOTramEtapaDB(this.connection);
    }
    @Override
    public DAOTramTrack createDAOTramTrack() {
        return new DAOTramTrackDB(this.connection);
    }
    @Override
    public DAOTransport_PossiblePerAl_TramDB createDAOTransportPosibleTram() {
        return new DAOTransport_PossiblePerAl_TramDB(this.connection);
    }
    @Override
    public DAOTransport createDAOTransport() {
        return new DAOTransportDB(this.connection);
    }
    @Override
    public DAOTripUB createDAOTripUB() {
        return new DAOTripUBDB(this.connection);
    }
    @Override
    public DAOVot createDAOVot() {
        return new DAOVotDB(this.connection);
    }


    Connection connection_open(){
        Connection c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            //jdbc:sqlite:[URL]
            String url = "./src/main/java/ub/edu/resources/dao/DB/Data/data.sqlite";
            c = DriverManager.getConnection("jdbc:sqlite:"+url);
            c.setAutoCommit(false);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return c;
    }

    void connection_close() {
        try {
            this.connection.close();
        }catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Closed database successfully");
        return;
    }

    // No se si tenen gaire sentit
    // Álvaro -> el get puede dejarse, pero el set puede generar problemas

    public Connection getC() {
        return this.connection;
    }

}