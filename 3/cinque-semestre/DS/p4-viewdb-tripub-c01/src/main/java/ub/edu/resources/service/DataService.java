package ub.edu.resources.service;

import ub.edu.model.*;
import ub.edu.resources.dao.*;
import ub.edu.resources.dao.DAO_Interfaces.*;
import ub.edu.resources.dao.DB.DAO_Implementations.DAOTransport_PossiblePerAl_TramDB;

import java.util.List;

public class DataService {

    private DAOAllotjament daoAllotjament;
    private DAOAPeu daoaPeu;
    private DAOBicicleta daoBicicleta;
    private DAOCamping daoCamping;
    private DAOCasaRural daoCasaRural;
    private DAOComarca daoComarca;
    private DAOCotxe daoCotxe;
    private DAOEtapa daoEtapa;
    private DAOGrupFormatPerPersones daoGrupFormatPerPersones;
    private DAOGrup daoGrup;
    private DAOHotel daoHotel;
    private DAOLocalitat daoLocalitat;
    private DAOOpinio daoOpinio;
    private DAOPersona daoPersona;
    private DAOPersonesRealitzaRuta daoPersonesRealitzaRuta;
    private DAOPuntDePas daoPuntDePas;
    private DAORefugi daoRefugi;
    private DAORelacioComarcaRuta daoRelacioRutaComarca;
    private DAORelacioComarcaRuta_2 daoRelacioRutaComarca2;
    private DAOReserva daoReserva;
    private DAORuta daoRuta;
    private DAOTipusOpinio daoTipusOpinio; //datos integrados en Opinio
    private DAOTipusVot daoTipusVot; //datos integrados en Vot
    private DAOTram daoTram;
    private DAOTramEtapa daoTramEtapa;
    private DAOTramTrack daoTramTrack;
    private DAOTransportPossiblePerAlTram daoTransportPosibleTram;
    private DAOTransport daoTransport;
    private DAOTripUB daoTripUB;
    private DAOVot daoVot;



    public DataService(AbstractFactoryData factory) {

        this.daoAllotjament = factory.createDAOAllotjament();
        this.daoaPeu = factory.createDAOAPeu();
        this.daoBicicleta = factory.createDAOBicicleta();
        this.daoCamping = factory.createDAOCamping();
        this.daoCasaRural = factory.createDAOCasaRural();
        this.daoComarca = factory.createDAOComarca();
        this.daoCotxe = factory.createDAOCotxe();
        this.daoEtapa = factory.createDAOEtapa();
        this.daoGrupFormatPerPersones = factory.createDAOGrupFormatPerPersones();
        this.daoGrup = factory.createDAOGrup();
        this.daoHotel = factory.createDAOHotel();
        this.daoLocalitat = factory.createDAOLocalitat();
        this.daoOpinio = factory.createDAOOpinio();
        this.daoPersona = factory.createDAOPersona();
        this.daoPersonesRealitzaRuta = factory.createDAOPersonaRealitzaRuta();
        this.daoPuntDePas = factory.createDAOPuntDePas();
        this.daoRefugi = factory.createDAORefugi();
        this.daoRelacioRutaComarca = factory.createDAORelacioRutaComarca();
        this.daoRelacioRutaComarca2 = factory.createDAORelacioRutaComarca_2();
        this.daoReserva = factory.createDAOReserva();
        this.daoRuta = factory.createDAORuta();
        this.daoTipusOpinio = factory.createDAOTipusOpinio();
        this.daoTipusVot = factory.createDAOTipusVot();
        this.daoTram = factory.createDAOTram();
        this.daoTramEtapa = factory.createDAOTramEtapa();
        this.daoTramTrack = factory.createDAOTramTrack();
        this.daoTransportPosibleTram = factory.createDAOTransportPosibleTram();
        this.daoTransport = factory.createDAOTransport();
        this.daoTripUB = factory.createDAOTripUB();
        this.daoVot = factory.createDAOVot();
    }

    //TODO: reminder -> if you want to write another, pls respect the order to faster read
    //the methods that dont have a model class, are at the bottom of the file

    //Allotjament
    public List<Allotjament> getAllAllotjaments() throws Exception {
        return this.daoAllotjament.getAll();
    }
    public Allotjament getAllotjamentById(Integer id) throws Exception {
        return this.daoAllotjament.getById(id.toString()).get();
    }
    public boolean addAllotjament(Allotjament allotjament) throws Exception {
        return this.daoAllotjament.add(allotjament);
    }
    public boolean deleteAllotjament(Allotjament allotjament) throws Exception {
        return this.daoAllotjament.delete(allotjament);
    }

    //APeu
    public List<aPeu> getAllAPeus() throws Exception {
        return this.daoaPeu.getAll();
    }
    public aPeu getAPeuById(Integer id) throws Exception {
        return this.daoaPeu.getById(id.toString()).get();
    }
    public boolean addAPeu(aPeu apeu) throws Exception {
        return this.daoaPeu.add(apeu);
    }
    public boolean deleteAPeu(aPeu apeu) throws Exception {
        return this.daoaPeu.delete(apeu);
    }

    //Bicicleta
    public List<Bicicleta> getAllBicicletas() throws Exception {
        return this.daoBicicleta.getAll();
    }
    public Bicicleta getBicicletaById(Integer id) throws Exception {
        return this.daoBicicleta.getById(id.toString()).get();
    }
    public boolean addBicicleta(Bicicleta bicicleta) throws Exception {
        return this.daoBicicleta.add(bicicleta);
    }
    public boolean deleteBicicleta(Bicicleta bicicleta) throws Exception {
        return this.daoBicicleta.delete(bicicleta);
    }

    //Camping
    public List<Camping> getAllCampings() throws Exception {
        return this.daoCamping.getAll();
    }
    public Camping getCampingById(Integer id) throws Exception {
        return this.daoCamping.getById(id.toString()).get();
    }
    public boolean addCamping(Camping camping) throws Exception {
        return this.daoCamping.add(camping);
    }
    public boolean deleteCamping(Camping camping) throws Exception {
        return this.daoCamping.delete(camping);
    }

    //CasaRural
    public List<CasaRural> getAllCasaRurals() throws Exception {
        return this.daoCasaRural.getAll();
    }
    public CasaRural getCasaRuralById(Integer id) throws Exception {
        return this.daoCasaRural.getById(id.toString()).get();
    }
    public boolean addCasaRural(CasaRural casaRural) throws Exception {
        return this.daoCasaRural.add(casaRural);
    }
    public boolean deleteCasaRural(CasaRural casaRural) throws Exception {
        return this.daoCasaRural.delete(casaRural);
    }

    //Comarca
    public List<Comarca> getAllComarcas() throws Exception {
        return this.daoComarca.getAll();
    }
    public Comarca getComarcaById(Integer id) throws Exception {
        return this.daoComarca.getById(id.toString()).get();
    }
    public boolean addComarca(Comarca comarca) throws Exception {
        return this.daoComarca.add(comarca);
    }
    public boolean deleteComarca(Comarca comarca) throws Exception {
        return this.daoComarca.delete(comarca);
    }

    //Cotxe
    public List<Cotxe> getAllCotxes() throws Exception {
        return this.daoCotxe.getAll();
    }
    public Cotxe getCotxeById(Integer id) throws Exception {
        return this.daoCotxe.getById(id.toString()).get();
    }
    public boolean addCotxe(Cotxe cotxe) throws Exception {
        return this.daoCotxe.add(cotxe);
    }
    public boolean deleteCotxe(Cotxe cotxe) throws Exception {
        return this.daoCotxe.delete(cotxe);
    }

    //Etapa
    public List<Etapa> getAllEtapas() throws Exception {
        return this.daoEtapa.getAll();
    }
    public Etapa getEtapaById(Integer id) throws Exception {
        return this.daoEtapa.getById(id.toString()).get();
    }
    public boolean addEtapa(Etapa etapa) throws Exception {
        return this.daoEtapa.add(etapa);
    }
    public boolean deleteEtapa(Etapa etapa) throws Exception {
        return this.daoEtapa.delete(etapa);
    }

    //GrupFormatPersones -> Parell<String,Integer>
    public List<Parell<String,Integer>> getAllGrupFormatPersones() throws Exception {
        return this.daoGrupFormatPerPersones.getAll();
    }
    /* NO TIENE ID
    public Parell<String,Integer> getGrupFormatPersonesById(Integer id) throws Exception {
        return this.daoGrupFormatPerPersones.getById(id.toString()).get();
    }
    */
    public boolean addGrupFormatPersones(Parell<String,Integer> gfp) throws Exception {
        return this.daoGrupFormatPerPersones.add(gfp);
    }
    public boolean deleteGrupFormatPersones(Parell<String,Integer> gfp) throws Exception {
        return this.daoGrupFormatPerPersones.delete(gfp);
    }
    public List<Parell<String, Integer>> getGrupsPerPersonaByIdsAND_OR(String correu_persona, String AND_OR, Integer id_grup ) {
        return this.daoGrupFormatPerPersones.getByIds_AND_OR(correu_persona, AND_OR, id_grup);
    }
    public List<Parell<String, Integer>> getGrupsPerPersonaByCorreuPersona(String correu) {
        return this.daoGrupFormatPerPersones.getByCorreu_Persona(correu);
    }
    public List<Parell<String, Integer>> getGrupsPerPersonaByIdGrup(Integer id) {
        return this.daoGrupFormatPerPersones.getById_Grup(id);
    }

    //Grup
    public List<Grup> getAllGrups() throws Exception {
        return this.daoGrup.getAll();
    }
    public Grup getGrupById(Integer id) throws Exception {
        return this.daoGrup.getById(id.toString()).get();
    }
    public boolean addGrup(Grup grup) throws Exception {
        return this.daoGrup.add(grup);
    }
    public boolean deleteGrup(Grup grup) throws Exception {
        return this.daoGrup.delete(grup);
    }

    //Hotel
    public List<Hotel> getAllHotels() throws Exception {
        return this.daoHotel.getAll();
    }
    public Hotel getHotelById(Integer id) throws Exception {
        return this.daoHotel.getById(id.toString()).get();
    }
    public boolean addHotel(Hotel hotel) throws Exception {
        return this.daoHotel.add(hotel);
    }
    public boolean deleteHotel(Hotel hotel) throws Exception {
        return this.daoHotel.delete(hotel);
    }

    //Localitat
    public List<Localitat> getAllLocalitats() throws Exception {
        return this.daoLocalitat.getAll();
    }
    public Localitat getLocalitatById(Integer id) throws Exception {
        return this.daoLocalitat.getById(id.toString()).get();
    }
    public boolean addLocalitat(Localitat localitat) throws Exception {
        return this.daoLocalitat.add(localitat);
    }
    public boolean deleteLocalitat(Localitat localitat) throws Exception {
        return this.daoLocalitat.delete(localitat);
    }

    //Opinio
    public List<Opinio> getAllOpinions() throws Exception {
        return this.daoOpinio.getAll();
    }
    public Opinio getOpinioById(Integer id) throws Exception {
        return this.daoOpinio.getById(id.toString()).get();
    }
    public boolean addOpinio(Opinio opinio) throws Exception {
        return this.daoOpinio.add(opinio);
    }
    public boolean deleteOpinio(Opinio opinio) throws Exception {
        return this.daoOpinio.delete(opinio);
    }

    //Persona
    public List<Persona> getAllPersonas() throws Exception {
        return this.daoPersona.getAll();
    }
    /* NO TIENE ID -> SU ID ES EL CORREU
    public Persona getPersonaById(Integer id) throws Exception {
        return this.daoPersona.getById(id.toString()).get();
    }*/
    public boolean addPersona(Persona persona) throws Exception {
        return this.daoPersona.add(persona);
    }
    public boolean deletePersona(Persona persona) throws Exception {
        return this.daoPersona.delete(persona);
    }
    public Persona getPersonaByCorreu(String correu) throws Exception {
        return this.daoPersona.findPersonaByCorreu(correu);
    }
    public Persona getPersonaByUserNameAndPassword(String nom, String pwd) throws Exception {
        return this.daoPersona.findPersonaByUserNameAndPassword(nom, pwd);
    }

    //PersonaRealitzaRuta -> Parell<Integer,String>
    public List<Parell<Integer,String>> getAllPersonasRealitzaRutas() throws Exception {
        return this.daoPersonesRealitzaRuta.getAll();
    }
    /* NO TIENE ID -> SU ID ES EL CORREU
    public Parell<Integer,String> getPersonaRealitzaRutaById(Integer id) throws Exception {
        return this.daoPersonesRealitzaRuta.getById(id.toString()).get();
    }*/
    public boolean addPersonaRealitzaRuta(Parell<Integer,String> prr) throws Exception {
        return this.daoPersonesRealitzaRuta.add(prr);
    }
    public boolean deletePersonaRealitzaRuta(Parell<Integer,String> prr) throws Exception {
        return this.daoPersonesRealitzaRuta.delete(prr);
    }
    public List<Parell<Integer,String>> getPersonaRealitzaByIds_AND_OR (Integer id_ruta, String AND_OR, String correu_persona){
        return this.daoPersonesRealitzaRuta.getByIds_AND_OR(id_ruta,AND_OR,correu_persona);
    }
    public List<Parell<Integer,String>> getPersonaRealitzaById_Ruta (Integer id){
        return this.daoPersonesRealitzaRuta.getById_Ruta(id);
    }
    public List<Parell<Integer,String>> getPersonaRealitzaByCorreu_Persona (String correu_persona){
        return this.daoPersonesRealitzaRuta.getByCorreu_Persona(correu_persona);
    }

    //PuntDePas
    public List<PuntDePas> getAllPuntDePas() throws Exception {
        return this.daoPuntDePas.getAll();
    }
    public PuntDePas getPuntDePasById(Integer id) throws Exception {
        return this.daoPuntDePas.getById(id.toString()).get();
    }
    public boolean addPuntDePas(PuntDePas puntDePas) throws Exception {
        return this.daoPuntDePas.add(puntDePas);
    }
    public boolean deletePuntDePas(PuntDePas puntDePas) throws Exception {
        return this.daoPuntDePas.delete(puntDePas);
    }

    //Refugi
    public List<Refugi> getAllRefugis() throws Exception {
        return this.daoRefugi.getAll();
    }
    public Refugi getRefugiById(Integer id) throws Exception {
        return this.daoRefugi.getById(id.toString()).get();
    }
    public boolean addRefugi(Refugi refugi) throws Exception {
        return this.daoRefugi.add(refugi);
    }
    public boolean deleteRefugi(Refugi refugi) throws Exception {
        return this.daoRefugi.delete(refugi);
    }

    //RelacioComarcaRuta(2) -> Parell<Integer,Integer>
    public List<Parell<Integer,Integer>> getAllRelacioComarcasRutas() throws Exception {
        return this.daoRelacioRutaComarca2.getAll();
    }
    /* NO TINE UN ID
    public Parell<Integer,Integer> getRelacioComarcaRutaById(Integer id) throws Exception {
        return this.daoRelacioRutaComarca2.getById(id.toString()).get();
    }
    */
    public boolean addRelacioComarcaRuta(Parell<Integer,Integer> rcm) throws Exception {
        return this.daoRelacioRutaComarca2.add(rcm);
    }
    public boolean deleteRelacioComarcaRuta(Parell<Integer,Integer> rcm) throws Exception {
        return this.daoRelacioRutaComarca2.delete(rcm);
    }
    public List<Parell<Integer,Integer>> getRelacioComarcaRutaByIds_AND_OR (Integer id_comarca, String AND_OR, Integer id_ruta){
        return this.daoRelacioRutaComarca2.getByIds_AND_OR(id_comarca,AND_OR,id_ruta);
    }
    public List<Parell<Integer,Integer>> getRelacioComarcaRutaById_Comarca (Integer id){
        return this.daoRelacioRutaComarca2.getById_Comarca(id);
    }
    public List<Parell<Integer,Integer>> getRelacioComarcaRutaById_Ruta (Integer id){
        return this.daoRelacioRutaComarca2.getById_Ruta(id);
    }

    //Reserva
    public List<Reserva> getAllReservas() throws Exception {
        return this.daoReserva.getAll();
    }
    public Reserva getReservaById(Integer id) throws Exception {
        return this.daoReserva.getById(id.toString()).get();
    }
    public boolean addReserva(Reserva reserva) throws Exception {
        return this.daoReserva.add(reserva);
    }
    public boolean deleteReserva(Reserva reserva) throws Exception {
        return this.daoReserva.delete(reserva);
    }

    //Ruta
    public List<Ruta> getAllRutas() throws Exception {
        return this.daoRuta.getAll();
    }
    public Ruta getRutaById(Integer id) throws Exception {
        return this.daoRuta.getById(id.toString()).get();
    }
    public boolean addRuta(Ruta ruta) throws Exception {
        return this.daoRuta.add(ruta);
    }
    public boolean deleteRuta(Ruta ruta) throws Exception {
        return this.daoRuta.delete(ruta);
    }

    //TipusOpinio -> Quartet<Integer,String,String,Boolean>
    public List<Quartet<Integer,String,String,Boolean>> getAllTipusOpinions() throws Exception {
        return this.daoTipusOpinio.getAll();
    }
    public Quartet<Integer,String,String,Boolean> getTipusOpinioById(Integer id) throws Exception {
        return this.daoTipusOpinio.getById(id.toString()).get();
    }
    public boolean addTipusOpinio(Quartet<Integer,String,String,Boolean> tipusOpinio) throws Exception {
        return this.daoTipusOpinio.add(tipusOpinio);
    }
    public boolean deleteTipusOpinio(Quartet<Integer,String,String,Boolean> tipusOpinio) throws Exception {
        return this.daoTipusOpinio.delete(tipusOpinio);
    }

    //TipusVot -> Quartet<Integer,String,Integer,Boolean>
    public List<Quartet<Integer,String,Integer,Boolean>> getAllTipusVots() throws Exception {
        return this.daoTipusVot.getAll();
    }
    public Quartet<Integer,String,Integer,Boolean> getTipusVotById(Integer id) throws Exception {
        return this.daoTipusVot.getById(id.toString()).get();
    }
    public boolean addTipusVot(Quartet<Integer,String,Integer,Boolean> tipusVot) throws Exception {
        return this.daoTipusVot.add(tipusVot);
    }
    public boolean deleteTipusVot(Quartet<Integer,String,Integer,Boolean> tipusVot) throws Exception {
        return this.daoTipusVot.delete(tipusVot);
    }

    //Tram
    public List<Tram> getAllTrams() throws Exception {
        return this.daoTram.getAll();
    }
    public Tram getTramById(Integer id) throws Exception {
        return this.daoTram.getById(id.toString()).get();
    }
    public boolean addTram(Tram tram) throws Exception {
        return this.daoTram.add(tram);
    }
    public boolean deleteTram(Tram tram) throws Exception {
        return this.daoTram.delete(tram);
    }

    //TramEtapa
    public List<TramEtapa> getAllTramEtapas() throws Exception {
        return this.daoTramEtapa.getAll();
    }
    public TramEtapa getTramEtapaById(Integer id) throws Exception {
        return this.daoTramEtapa.getById(id.toString()).get();
    }
    public boolean addTramEtapa(TramEtapa tramEtapa) throws Exception {
        return this.daoTramEtapa.add(tramEtapa);
    }
    public boolean deleteTramEtapa(TramEtapa tramEtapa) throws Exception {
        return this.daoTramEtapa.delete(tramEtapa);
    }

    //TramTrack
    public List<TramTrack> getAllTramTracks() throws Exception {
        return this.daoTramTrack.getAll();
    }
    public TramTrack getTramTrackById(Integer id) throws Exception {
        return this.daoTramTrack.getById(id.toString()).get();
    }
    public boolean addTramTrack(TramTrack tramTrack) throws Exception {
        return this.daoTramTrack.add(tramTrack);
    }
    public boolean deleteTramTrack(TramTrack tramTrack) throws Exception {
        return this.daoTramTrack.delete(tramTrack);
    }

    //TransportPosibleTram -> Parell<Integer,Integer>
    public List<Parell<Integer,Integer>> getAllTransportsPosibleTrams() throws Exception {
        return this.daoTransportPosibleTram.getAll();
    }
    /* NO TIENE ID
    public Parell<Integer,Integer> getTransportPosibleTramById(Integer id) throws Exception {
        return this.daoTransportPosibleTram.getById(id.toString()).get();
    }
    */
    public boolean addTransportPosibleTram(Parell<Integer,Integer> tpt) throws Exception {
        return this.daoTransportPosibleTram.add(tpt);
    }
    public boolean deleteTransportPosibleTram(Parell<Integer,Integer> tpt) throws Exception {
        return this.daoTransportPosibleTram.delete(tpt);
    }
    public List<Parell<Integer,Integer>> getTransportPosibleTramByIds_AND_OR (Integer id_transport, String AND_OR, Integer id_tram){
        return this.daoTransportPosibleTram.getByIds_AND_OR(id_transport,AND_OR,id_tram);
    }
    public List<Parell<Integer,Integer>> getTransportPosibleTramById_Transport (Integer id){
        return this.daoTransportPosibleTram.getById_Transport(id);
    }
    public List<Parell<Integer,Integer>> getTransportPosibleTramById_Tram (Integer id){
        return this.daoTransportPosibleTram.getById_Tram(id);
    }

    //Transport
    public List<Transport> getAllTransports() throws Exception {
        return this.daoTransport.getAll();
    }
    public Transport getTransportById(Integer id) throws Exception {
        return this.daoTransport.getById(id.toString()).get();
    }
    public boolean addTransport(Transport transport) throws Exception {
        return this.daoTransport.add(transport);
    }
    public boolean deleteTransport(Transport transport) throws Exception {
        return this.daoTransport.delete(transport);
    }

    //TripUB
    public List<TripUB> getAllTripUBs() throws Exception {
        return this.daoTripUB.getAll();
    }
    public TripUB getTripUBById(Integer id) throws Exception {
        return this.daoTripUB.getById(id.toString()).get();
    }
    public boolean addTripUB(TripUB tripUB) throws Exception {
        return this.daoTripUB.add(tripUB);
    }
    public boolean deleteTripUB(TripUB tripUB) throws Exception {
        return this.daoTripUB.delete(tripUB);
    }

    //Vot
    public List<Vot> getAllVots() throws Exception {
        return this.daoVot.getAll();
    }
    public Vot getVotById(Integer id) throws Exception {
        return this.daoVot.getById(id.toString()).get();
    }
    public boolean addVot(Vot vot) throws Exception {
        return this.daoVot.add(vot);
    }
    public boolean deleteVot(Vot vot) throws Exception {
        return this.daoVot.delete(vot);
    }

    //OTHERS



}