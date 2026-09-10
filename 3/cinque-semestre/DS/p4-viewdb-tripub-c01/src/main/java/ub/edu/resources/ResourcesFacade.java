package ub.edu.resources;

import ub.edu.model.StatusType;
import ub.edu.model.Seguretat;
import ub.edu.model.*;
import ub.edu.resources.dao.Parell;
import ub.edu.resources.service.AbstractFactoryData;
import ub.edu.resources.service.DataService;

import java.util.List;

import ub.edu.resources.service.FactoryDB;

public class ResourcesFacade {

    private AbstractFactoryData factory;      // Origen de les dades
    private DataService dataService;         // Connexio amb les dades
    private TripUB tripUB;                  // Model a tractar

    private ModelFacade modelFacade;

    public ResourcesFacade(TripUB tripUB, ModelFacade modelFacade) {
        // factory = new FactoryMOCK();

        factory = new FactoryDB();
        dataService = new DataService(factory);
        this.tripUB = tripUB;
        this.modelFacade = modelFacade;

    }

    public void populateTripUB() {
        try {
            initXarxaPersones(); //success
            initGrups(); //success
            relacioPersonesGrups(); //success

            initRutes(); //success
            initComarques(); //success
            relacionarRutesComarques(); //success
            initLocalitats();  //success // Pressuposa que les comarques ja han estat creades

            initTramsRuta();  //success // Pressuposa que les rutes ja estan creades.
            initAllotjaments(); //success // Pressuposa que les etapes ja han estat creades

            initPerfilsPersones(); //success

        } catch (Exception e) {
            System.out.println("Exception: --> " + e.getMessage());
        }
    }

    private void relacioPersonesGrups() throws Exception {
        // Han estat creats els grups i les persones previament
        // Ara cal lligar les persones i els grups
        List<Parell<String, Integer>> relacionsPG = dataService.getAllGrupFormatPersones();

        for (Parell<String, Integer> parell: relacionsPG) {
            Persona persona;
            persona = modelFacade.findPersonaByCorreu(parell.getElement1());
            Grup grup;
            grup = tripUB.findGrup(parell.getElement2());
            grup.addPersona(persona);

            PerfilPersona pp = persona.getPerfil();
            pp.addGrup(grup);
        }

    }

    private void initPerfilsPersones() throws Exception {
        // Es considera que totes les classes amb les que estan relacionades
        // vots, opinio i reserves ja han estat creades
        initVots();
        initOpinio();
        initReserves();
    }

    private void initOpinio() throws Exception  {
        List<Opinio> llista = dataService.getAllOpinions();
        for (Opinio o: llista) {
            o.setPuntDePas(modelFacade.findPuntDePas(o.getIdPuntDePas()));
            Persona persona = modelFacade.findPersonaByCorreu(o.getCorreuPersona());
            o.setPersona(persona);

            PerfilPersona pp = persona.getPerfil();
            pp.addOpinio(o);
        }
    }

    private void initVots() throws Exception  {
        List<Vot> llista = dataService.getAllVots();
        for (Vot v: llista) {
            v.setRuta(tripUB.findRuta((v.getIdRuta())));
            Persona persona = modelFacade.findPersonaByCorreu(v.getCorreuPersona());
            v.setPersona(persona);
            v.setGrup(tripUB.findGrup(v.getIdGrup()));

            PerfilPersona pp = persona.getPerfil();
            pp.addVot(v);
        }
    }

    private void initReserves() throws Exception  {
        List<Reserva> llista = dataService.getAllReservas();
        for (Reserva r: llista) {
            r.setAllotjament(modelFacade.findAllotjament(r.getIdAllotjament()));
            Persona persona = modelFacade.findPersonaByCorreu(r.getCorreuPersona());
            r.setPersona(persona);

            PerfilPersona pp = persona.getPerfil();
            pp.addReserva(r);
        }
    }
    private void initLocalitats() throws Exception  {
        List<Localitat> llista = dataService.getAllLocalitats();
        for (Localitat l : llista) {
            Comarca c = tripUB.findComarca(l.getIdComarca());
            l.setComarca(c);
            c.addLocalitat(l);
            List<Ruta> origens = tripUB.findRutaByOrigen(l.getId());
            for (Ruta r: origens) {
                r.setLocalitatOrigen(l);
            }
            List<Ruta> destins = tripUB.findRutaByDesti(l.getId());
            for (Ruta r: destins) {
                r.setLocalitatDesti(l);
            }
        }
    }

    private void initAllotjaments() throws Exception {
        List<Allotjament> lallotjaments= dataService.getAllAllotjaments();
        for (Allotjament a : lallotjaments) {
            //Etapa e = dataService.getEtapaById(a.getIdEtapa());
            Etapa e = modelFacade.findEtapa(a.getIdEtapa());
            if (e!=null) {
                e.addAllotjament(a);
                a.setEtapa(e);
            }
        }
    }

    private void initTramsRuta() throws Exception {
        // Es donen d'alta els trams d'una ruta i depres, segons siguin
        // de tipus etapa o de tipus track es donen d'alta
        // les etapes o els punts de control
        // Al final es reparteixen els transports pels trams ja creats
        // no es consulta al dataservice pels trams sino a tripUB per
        // a no fer dos vegades new del mateix tram

        initTramsEtapaRuta();
        initTramsTrackRuta();
        // Lligar transports amb els trams etapa. Els transports també es creen a demanda
        // segons el que necessiti el tram.
        List<Parell<Integer, Integer>> llistaTransportsTrams= dataService.getAllTransportsPosibleTrams();

        for (Parell p: llistaTransportsTrams) {
            // element 1 Transport
            // element 2 tram
            Transport t = dataService.getTransportById((Integer)(p.getElement1()));
            modelFacade.addTransportTram((Integer)(p.getElement2()), t);
        }
    }

    private void initTramsEtapaRuta() throws Exception {
        // Es donen d'alta els trams d'una ruta i depres, segons siguin
        // de tipus etapa es donen d'alta
        // les etapes
        // Al final es reparteixen els transports pels trams ja creats
        // no es consulta al dataservice pels trams sino a tripUB per
        // a no fer dos vegades new del mateix tram

        List<TramEtapa> trams = dataService.getAllTramEtapas();
        for (TramEtapa te : trams) {
            // Reparticio dels trams a les rutes
            modelFacade.addTram(te);

            // Lligar etapa-TramEtapa. Les etapes es llegeixen una a una de la BD
            // segons ho necessiti el tram etapa

            Etapa etapa = dataService.getEtapaById(te.getIdEtapaDesti());
            te.setEtapaDesti(etapa);
            etapa = dataService.getEtapaById(te.getIdEtapaOrigen());
            te.setEtapaOrigen(etapa);
        }
    }
    private void initTramsTrackRuta() throws Exception {
            // Es donen d'alta els trams d'una ruta i depres, segons siguin
            // d de tipus track es donen d'alta
            // els punts de control

            List<TramTrack> trams = dataService.getAllTramTracks();
            for (TramTrack tt: trams) {
                // Reparticio dels trams a les rutes
                modelFacade.addTram(tt);

                PuntDePas pdp = dataService.getPuntDePasById(tt.getIdPuntDePasDesti());
                tt.setPuntDePasDesti(pdp);
                pdp = dataService.getPuntDePasById(tt.getIdPuntDePasOrigen());
                tt.setPuntDePasOrigen(pdp);
            }
        }


    public boolean initGrups() throws Exception {
        return (tripUB.setGrups(dataService.getAllGrups()));
    }

    public boolean initXarxaPersones() throws Exception {
        return modelFacade.setXarxaPersones(dataService.getAllPersonas());
    }

    public boolean initRutes() throws Exception {
        List<Ruta> listRutas = dataService.getAllRutas();
        return tripUB.setRutes(listRutas);
    }

    public boolean initComarques() throws Exception {
        List<Comarca> listComarques = dataService.getAllComarcas();
        return tripUB.setComarques(listComarques);
    }

    public void relacionarRutesComarques() throws Exception {
        //DB:
        List<Parell<Integer, Integer>> relacionsRC = dataService.getAllRelacioComarcasRutas();
        tripUB.setComarquesToRutes(relacionsRC);
    }


    public StatusType addNewPersona(String correu, String nom, String cognoms, String dni, String password, Integer id_tripUB) throws Exception {
        if  (modelFacade.findPersonaByCorreu(correu) != null)
            return StatusType.PERSONA_DUPLICADA;
        else if (Seguretat.isMail(correu) && Seguretat.isPasswordSegur(password)){
            Persona persona = new Persona(correu, nom, cognoms, dni, password, id_tripUB);
            if(this.dataService.addPersona(persona)){
                //actualizar lista
                this.initXarxaPersones();
            }
            return StatusType.REGISTRE_VALID;
        }
        else return StatusType.FORMAT_INCORRECTE_CORREU_PWD;
    }


    public Integer addNewGrup(String grupNom) throws Exception {
        Grup grup = new Grup(grupNom);
        dataService.addGrup(grup);
        tripUB.addGrup(grup);
        return grup.getId();
    }
    public boolean addRelacionGrupPersona(String correuPersona, Integer idGrup) throws Exception {
        Parell<String,Integer> gfp = new Parell<>(correuPersona,idGrup);
        modelFacade.addPersonaToGrup(idGrup, correuPersona);
        return dataService.addGrupFormatPersones(gfp);
    }

}
